package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import logic.Session_Client;
import net.miginfocom.swing.MigLayout;
import networking.NetworkClient;

public class FirstGUI {
	
	private JTextField usernameTxtField;
	private JTextField ipTxtField;
	
	public FirstGUI() {
		
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void createGUI() {
		JFrame frame = new JFrame("YGO Draft Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800,600);
		frame.setMinimumSize(new Dimension(400,400));
		frame.getContentPane().setLayout(new MigLayout("", "[][][][][grow][][][][][][][][][grow][][][][][][][][grow]", "[][grow][][][][][][][][grow][][][][][][grow][grow]"));
		
		Box horizontalBox = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox, "cell 0 0");
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, "cell 3 1,grow");
		
		JLabel lblNewLabel_3 = new JLabel("YGO Draft Simulator");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 20));
		frame.getContentPane().add(lblNewLabel_3, "cell 13 1,alignx center");
		
		JPanel panel_3 = new JPanel();
		frame.getContentPane().add(panel_3, "cell 21 1,grow");
		
		JButton hostDraftBtn = new JButton("Host Draft");
		hostDraftBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		hostDraftBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				HostGameGUI lobbySetup = new HostGameGUI();
				lobbySetup.createHostGameGUI();
			}
		});
		frame.getContentPane().add(hostDraftBtn, "cell 13 4,alignx center");
		
		JPanel panel_4 = new JPanel();
		frame.getContentPane().add(panel_4, "cell 6 5 8 10,alignx center,aligny center");
		panel_4.setLayout(new MigLayout("", "[][grow][]", "[][][]"));
		
		JButton connectDraftBtn = new JButton("Connect to Draft");
		connectDraftBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		connectDraftBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameTxtField.getText();
				String ip = ipTxtField.getText();
				NetworkClient networkHandler = new NetworkClient(username, ip); // connect to server here
				try {
					Session_Client mySession = networkHandler.connectAndWaitToStart();
					frame.dispose();
					DraftSessionGUI draftSession = new DraftSessionGUI(mySession);
					draftSession.createMainWindow();
					draftSession.backgroundNetworkListenerLoop(networkHandler);
					mySession.shuffleCards(); // sends a first request to the server to fill the card list
				}
				catch(NullPointerException ex) {
					System.err.println(ex + " Error, shutting down...");
				}
				
			}
		});
		
		panel_4.add(connectDraftBtn, "cell 1 0");
		
		JLabel usernameLbl = new JLabel("Username");
		usernameLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_4.add(usernameLbl, "cell 0 1,alignx trailing");
		
		usernameTxtField = new JTextField();
		panel_4.add(usernameTxtField, "cell 1 1,growx");
		usernameTxtField.setColumns(10);
		
		JLabel ipAddressLbl = new JLabel("IP Address");
		ipAddressLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_4.add(ipAddressLbl, "cell 0 2,alignx trailing");
		
		ipTxtField = new JTextField();
		panel_4.add(ipTxtField, "cell 1 2,growx");
		ipTxtField.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, "cell 2 15,grow");
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, "cell 21 15,grow");

		
		
		
		frame.setVisible(true);
	}

}
