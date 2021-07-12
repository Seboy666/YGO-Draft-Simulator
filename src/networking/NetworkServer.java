package networking;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

import logic.Player;

public class NetworkServer extends NetworkHandler {

	private ExecutorService pool;
	private static JTextArea theConsole;
	
	/**
	 * The host should be added last, when the draft is started
	 */
	private static List<Player> playerList;
	private static int nextPlayerID;
	private static final int FIRST_CLIENT_PLAYER_ID = 2; // the host is always player ID = 1
	
	// The set of all the print writers for all the clients, used for broadcast.
    public static Set<ClientInfoHolder> clientSet = new HashSet<>();
	
	public NetworkServer(String username, String address, JTextArea console) {
		this.myUsername = username;
		this.ip = address;
		this.pool = Executors.newFixedThreadPool(25); // 25 users/threads max
		NetworkServer.theConsole = console;
		nextPlayerID = FIRST_CLIENT_PLAYER_ID;
	}
	
	public void setUsername(String name) { myUsername = name; }
	
	/**
	 * Shares with all clients the card that was picked
	 * 
	 * @param index The picked card index
	 */
	public void broadcastCardChoice(int playerID, int index) {
		for(ClientInfoHolder each : clientSet) {
			each.sendMsg(PICKED_CARD_CONFIRMED_MSG_START + playerID + MSG_SEPARATOR + index);
		}
	}
	
	public void broadcastStartGame(int startingPlayerID, int cardsPerRound) {
		String theMsg = CONN_REQ_ANSWER_START + startingPlayerID + MSG_SEPARATOR + cardsPerRound;
		for(Player each : playerList) {
			theMsg = theMsg + MSG_SEPARATOR + each.getUsername() + MSG_SEPARATOR + each.getID();
		}
		for(ClientInfoHolder each : clientSet) {
			each.sendMsg(theMsg);
		}
	}
	
	@Override
	public boolean isServer() { return true; }
	
	@Override
	public void close() {
		try {
			socket.close();
            input.close();
            server.close();
            
            for (ClientInfoHolder each : clientSet) {
				each.close();
			}
            
		}
		catch(Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public void listenForConnectingPlayers(List<Player> initPlayerList) {
		playerList = initPlayerList; // initialize the player list
		try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                pool.execute(new LoginHandler(listener.accept()));
            }
        }catch (Exception e){
        	e.printStackTrace();
        }
	}
	
	/**
     * The client handler task.
     */
	private static class LoginHandler implements Runnable {
		private String fromClient;
		private Player thisPlayer;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		private ClientInfoHolder thisClient;

		/**
		 * Constructs a handler thread, squirreling away the socket. All the interesting
		 * work is done in the run method. Remember the constructor is called from the
		 * server's main method, so this has to be as short as possible.
		 */
		public LoginHandler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name until a
		 * unique one has been submitted, then acknowledges the name and registers the
		 * output stream for the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(USERNAME_REQ_MSG); // request the player's username
				// Wait for the player to submit a username
				while (true) {
					fromClient = in.nextLine();
					System.out.println("Read from client : " + fromClient);
					if (fromClient == null) {
						return; // Does this line stop this thread?
					}
					synchronized (playerList) {
						String playerName = fromClient.split(MSG_SEPARATOR)[CONN_REQ_OUTBOUND_MSG_USERNAME_INDEX];
						thisPlayer = new Player(playerName, nextPlayerID);
						playerList.add(thisPlayer); // this *should* keep players in the right order, but even if they aren't, it may not cause any problems
						nextPlayerID++;
						thisClient = new ClientInfoHolder(thisPlayer, socket, in, out);
						break;
					}
				}
				synchronized (theConsole) {
					theConsole.append(thisPlayer.getUsername() + " connected with ID #" + thisPlayer.getID());
				}
				clientSet.add(thisClient);
			}
			catch (Exception e) {
				System.out.println(e);
			}
			/*
			finally {
				if (out != null) {
					clientSet.remove(thisClient);
				}
				if (fromServer != null) {
					System.out.println(fromServer + " is leaving");
					//names.remove(name);
					for (ClientInfoHolder each : clientSet) {
						each.sendMsg("MESSAGE " + fromServer + " has left");
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}*/
		}
	}
	
}
