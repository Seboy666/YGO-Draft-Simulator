package networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import logic.Player;
import logic.Session_Client;

public class NetworkClient extends NetworkHandler {

	public NetworkClient(String username, String address) {
		myUsername = username;
		ip = address;
		try {
    		socket = new Socket(ip, PORT);
    		System.out.println("Connected");
    		
            input  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		
    		// sends output to the socket
            output = new PrintWriter(socket.getOutputStream(), true);
    	}
    	catch(Exception e) {
    		//e.printStackTrace();
    		System.out.println("Connection Failure");
    	}
	}
	
	public String waitToReceiveLineFromServer() {
		String readLine = "";
		try
        {
			readLine = readInputLine();
        }
		catch(Exception i)
        {
			System.out.println("Exception caught in NetworkClient : waitToReceiveLineFromServer()");
        	i.printStackTrace();
        }
		return readLine;
	}
	
	/**
	 * Sends a message containing the current player username, and waits for an
	 * answer from the server to start the Draft. The answer should contain the ID
	 * of the player that starts picking and all player usernames followed by their
	 * ID
	 * 
	 * @return The Session_Client object
	 */
	public Session_Client connectAndWaitToStart() {
		String answer = "";
		while(true) {
			try
            {
				output.println(CONN_REQ_OUTBOUND_MSG_START + myUsername);
                answer = readInputLine();
            }
            catch(Exception i)
            {
            	//System.err.println(i);
            	i.printStackTrace();
            }
			if(answer.startsWith(CONN_REQ_ANSWER_START)) {
				break;
			}
		}
		String[] decode = answer.split(MSG_SEPARATOR); // array should be [LAUNCH][cardsPerRound][startingPlayerID][username][id][username][id]... etc.
		int startingPlayerID = Integer.parseInt(decode[STARTING_PLAYER_ID_INDEX]);
		int cardsPerRound = Integer.parseInt(decode[CARDS_PER_ROUND_INDEX]);
		List<Player> playerList = new ArrayList<Player>();
		Player tempPlayer;
		int myPlayerID = 0;
		for(int i = FIRST_USERNAME_INDEX; i <= decode.length; i = i+2) {
			if(decode[i] == myUsername) {
				myPlayerID = Integer.parseInt(decode[i+1]);
			}
			tempPlayer = new Player(decode[i], Integer.parseInt(decode[i+1]));
			playerList.add(tempPlayer);
		}
		
		Session_Client session = new Session_Client(cardsPerRound, startingPlayerID, myPlayerID, playerList, this);
		
		return session;
	}
	
	
	public void requestUpdatedCardList(int sendingPlayerID) {
		try
        {
			output.println(REQ_UPDATE_MSG_START + sendingPlayerID);
        }
        catch(Exception i)
        {
        	i.printStackTrace();
        }
	}
	
	/**
	 * Sends a message containing the index of the card the client is trying to
	 * pick.
	 * 
	 * @param index The index of the card the client is trying to pick.
	 */
	public void requestPickCard(int index) {
		try
        {
			output.println(PICK_CARD_REQ_START + index);
        }
        catch(Exception i)
        {
        	i.printStackTrace();
        }
	}
	
	public int waitForCardToRemove() {
		String answer = "";
		while(true) {
			try
            {
                answer = readInputLine();
            }
            catch(Exception i)
            {
            	i.printStackTrace();
            }
			if(answer.startsWith(PICKED_CARD_CONFIRMED_MSG_START)) {
				break;
			}
		}
		String[] decode = answer.split(MSG_SEPARATOR);
		return Integer.parseInt(decode[PICKED_CARD_CONFIRMED_MSG_INDEX_CARD_INDEX]);
	}
	
	/**
	 * Waits for the server to send a message that updates the card list for all clients.
	 * @return An array containing the new card list
	 */
	public String[] waitForCardList() {
		String answer = "";
		while(true) {
			try
            {
                answer = readInputLine();
            }
            catch(Exception i)
            {
            	i.printStackTrace();
            }
			if(answer.startsWith(UPDATE_CARD_LIST_MSG_START)) {
				break;
			}
		}
		String[] decode = answer.split(MSG_SEPARATOR);
		String[] toReturn = new String[decode.length - 1];
		for(int i = UPDATE_CARD_LIST_MSG_FIRST_CARD_NAME_INDEX; i < decode.length; i++) {
			toReturn[i-1] = decode[i];
		}
		return toReturn;
	}
	
	@Override
	public boolean isServer() { return false; }
	
	@Override
	public void close() {
		try
        {
            input.close();
            output.close();
            socket.close();
        }
        catch(Exception i)
        {
        	i.printStackTrace();
        }
	}
	
}
