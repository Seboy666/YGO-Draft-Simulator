package networking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import logic.Player;

public class ClientInfoHolder {
	private Player player;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	
	public ClientInfoHolder(Player thisPlayer, Socket socket, Scanner in, PrintWriter out) {
		this.player = thisPlayer;
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	
	public void sendMsg(String msg) { out.println(msg); }
	
	public String receiveMsg() { return in.nextLine(); }
	
	public void sendUpdatedCardList(String[] cardNames) {
		String theMessage = NetworkHandler.UPDATE_CARD_LIST_MSG_START;
		for(String each : cardNames) {
			theMessage = theMessage + each + NetworkHandler.MSG_SEPARATOR;
		}
		out.println(theMessage);
	}
	
	public void close() throws IOException {
		in.close();
        out.close();
        socket.close();
	}
	
	public Player getPlayer() { return player; }

	public Socket getSocket() { return socket; }

	public Scanner getInputScanner() { return in; }

	public PrintWriter getOutputPrintWriter() { return out; }

}
