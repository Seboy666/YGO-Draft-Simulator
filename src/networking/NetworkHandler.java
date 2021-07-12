package networking;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class NetworkHandler {
	
	protected Socket socket;
	protected ServerSocket server;
	protected BufferedReader input;
	protected PrintWriter output;
	protected static final int PORT = 8090; // for now, the port number is hard-coded
	protected String myUsername;
	protected String ip;
    
	public static final String MSG_SEPARATOR = "::";
	
	public static final String USERNAME_REQ_MSG = "USERNAMEREQ";
	
	/**
	 * Append the current player username to this string
	 */
	public static final String CONN_REQ_OUTBOUND_MSG_START = "CREQ::";
	public static final int CONN_REQ_OUTBOUND_MSG_USERNAME_INDEX = 1;
	
	/**
	 * Appended to this string should be the starting player ID, then the number of
	 * cards drafted per round, then all the players in order, alternating between
	 * their username and ID. All should be separated by "::" regex
	 */
	public static final String CONN_REQ_ANSWER_START = "LAUNCH::";
	public static final int STARTING_PLAYER_ID_INDEX = 1;
	public static final int CARDS_PER_ROUND_INDEX = 2;
	public static final int FIRST_USERNAME_INDEX = 3;
	
	/**
	 * A client sends a message starting by this string to tell the server which
	 * card the local player has picked.
	 */
	public static final String PICK_CARD_REQ_START = "PICKREQ::";
	public static final int PICK_CARD_REQ_INDEX_CARD_INDEX = 1;
	
	/**
	 * When the server accepts a card pick, it broadcasts a message starting with
	 * this string to all clients.
	 */
	public static final String PICKED_CARD_CONFIRMED_MSG_START = "PICKANSWER::";
	public static final int PICKED_CARD_CONFIRMED_MSG_INDEX_PLAYER_ID = 1;
	public static final int PICKED_CARD_CONFIRMED_MSG_INDEX_CARD_INDEX = 2;
	
	/**
	 * Message start of the request sent by clients to get the updated card list from the server
	 */
	public static final String REQ_UPDATE_MSG_START = "REQUPDATE::";
	
	/**
	 * Message start of the message telling clients to update their card list
	 */
	public static final String UPDATE_CARD_LIST_MSG_START = "UPDATE::";
	public static final int UPDATE_CARD_LIST_MSG_FIRST_CARD_NAME_INDEX = 1;
	
	/**
	 * Message sent by a client to resync their game to the same state the server is in
	 */
	public static final String RESYNC_CLIENT_REQ_MSG_START = "RESYNC::";
	
	/**
	 * Parses the string received by the server to extract the card names from said
	 * message.
	 * 
	 * @param msg The message received, as a string.
	 * @return The array containing all card names.
	 */
	public static String[] parseUpdateCardListMsg(String msg) {
		String[] array = msg.split(MSG_SEPARATOR);
		String[] toReturn = new String[array.length - 1];
		for(int i = UPDATE_CARD_LIST_MSG_FIRST_CARD_NAME_INDEX; i < array.length; i++) {
			toReturn[i-1] = array[i];
		}
		return toReturn;
	}
	
	public synchronized String readInputLine() throws IOException {
		String test = input.readLine();
		System.out.println("Read line : " + test);
		return test; 
	}
	
	public String getUsername() { return myUsername; }
	public String getIP() { return ip; }
    public abstract boolean isServer();
    public abstract void close();
    
}
