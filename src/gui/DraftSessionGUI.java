package gui;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import cards.*;
import logic.*;
import net.miginfocom.swing.MigLayout;
import networking.*;

public class DraftSessionGUI {

	private Session mySession;
	
	private static final int NUM_OF_ROWS_FOR_LEFT_PANEL = 6;
	private static final double CARD_DIM_RATIO = 1.45;
	private static final double MAX_CARD_SIZE_SCREEN_RATIO = 0.160377;
	
	private JFrame frame;
	private JPanel cardListPanel;
	private JMenu menuInfo;
	private JMenu menuPickingPlayerName;
	private JFrame floatingCardFrame;
	private JPanel floatingCardInfoPanel;
	
	private int screenHeight;
	private int screenWidth;
	
	private static final String NOW_PICKING_STR = "Now picking: ";
	
	public DraftSessionGUI(Session session) {
		mySession = session;
		this.cardListPanel = new JPanel();
		this.menuInfo = new JMenu("Info");
		this.menuPickingPlayerName = new JMenu(NOW_PICKING_STR + mySession.getPickingPlayer().getUsername());
	}
	
	public Session getSession() { return mySession; }
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void createMainWindow() {
		
		floatingCardFrame = new JFrame("Card info"); // a window that appears when clicking "info" on a drafted card panel
		floatingCardFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		floatingCardFrame.setSize(400, 690);
		floatingCardFrame.setMinimumSize(new Dimension(400, 300));
		floatingCardInfoPanel = new JPanel();
		floatingCardFrame.setContentPane(floatingCardInfoPanel);
		
		// Create a JFrame with a title, append to that title the player's username
		frame = new JFrame("YGO Draft Simulator - " + mySession.getPlayerByID(mySession.getMyPlayerID()).getUsername());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        // close all sockets?
		    }
		});
		frame.setSize(800, 600);
		frame.setMinimumSize(new Dimension(400, 300));
		
		frame.setContentPane(new JPanel(new BorderLayout(0, 0)));
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu menuSettings = new JMenu("Settings");
		JMenuItem refreshMnItem = new JMenuItem("Refresh");
		menuSettings.add(refreshMnItem);
		refreshMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				cardListPanel.removeAll();
				populateLeftCardPanel(cardListPanel);
				cardListPanel.repaint();
				cardListPanel.revalidate();
			}
		});
		addDeckExtractionToSettingsMenu(menuSettings);
		
		updateMenu();

		menuBar.add(menuSettings);
		menuBar.add(menuInfo);
		menuBar.add(menuPickingPlayerName);
		
		frame.setVisible(true);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		GridBagConstraints constr = new GridBagConstraints();
		
		// Make first cell wider than the next
		constr.fill = GridBagConstraints.BOTH;
		constr.gridx = 0;
		constr.gridy = 0;
		constr.weightx = 0.7;
		
		mainPanel.add(cardListPanel, constr);
		
		int numOfColumnsForDraftedCards;
		if(mySession.getCardsPerRound() % NUM_OF_ROWS_FOR_LEFT_PANEL == 0) {
			numOfColumnsForDraftedCards = mySession.getCardsPerRound()/NUM_OF_ROWS_FOR_LEFT_PANEL;
		} else {
			numOfColumnsForDraftedCards = mySession.getCardsPerRound()/NUM_OF_ROWS_FOR_LEFT_PANEL + 1;
		}
		cardListPanel.setLayout(new GridLayout(NUM_OF_ROWS_FOR_LEFT_PANEL, numOfColumnsForDraftedCards, 0, 0));
		
		populateLeftCardPanel(cardListPanel);
		
		JPanel allPlayersDeckPanel = new JPanel();
		
		// make second collumn cell narrower
		constr.gridx = 1;
		constr.weightx = 0.1;
		
		mainPanel.add(allPlayersDeckPanel, constr);
		allPlayersDeckPanel.setLayout(new GridLayout(1, mySession.getTotalPlayers(), 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		populatePlayerTabs(tabbedPane);
		allPlayersDeckPanel.add(tabbedPane);
	}
	
	private void addDeckExtractionToSettingsMenu(JMenu theSettingsMenu) {
		for(Player each : mySession.getPlayerList()) {
			JMenuItem extractDeckMnItem = new JMenuItem("Save " + each.getUsername() + "'s deck");
			theSettingsMenu.add(extractDeckMnItem);
			extractDeckMnItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try (PrintWriter out = new PrintWriter("data/" + each.getUsername() + ".ydk")) {
					    out.println(each.extractDeckAsYGOProTxtString());
					} catch (FileNotFoundException e1) {
						System.err.println(e1);
						try (PrintWriter out = new PrintWriter("data/player" + each.getID() + ".ydk")) { // if the username is invalid, try with this file name instead
							out.println(each.extractDeckAsYGOProTxtString());
							System.out.println("Successfully saved player deck");
						} catch (FileNotFoundException e2) {
							System.err.println(e2);
						}
					}
				}
			});
		}
	}
	
	private JPanel populateLeftCardPanel(JPanel leftPanel) {
		for(Card card : mySession.getCardList()) {
			leftPanel.add(createDraftedCardPanel(card));
		}
		return leftPanel;
	}
	
	private JPanel createDraftedCardPanel(Card card) {
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());
		JLabel lblImage;
		try {
			screenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
			int topBarHeight =  frame.getInsets().top + menuInfo.getParent().getBounds().height;
			int cardHeight = (screenHeight - topBarHeight)/NUM_OF_ROWS_FOR_LEFT_PANEL;
			lblImage = new JLabel(new ImageIcon(card.getImage().getScaledInstance((int)(cardHeight/CARD_DIM_RATIO), cardHeight, Image.SCALE_SMOOTH)));
		}
		catch(Exception e) {
			lblImage = new JLabel(card.getFormattedName());
		}
		
		cardPanel.add(lblImage, BorderLayout.WEST);
		
		JPanel nameAndDescPanel = new JPanel(new BorderLayout());
		JLabel lblCardName = new JLabel(card.getOneLineInfo());
		JTextArea lblCardDesc = new JTextArea(card.getBetterDesc());
		lblCardDesc.setLineWrap(true);
		lblCardDesc.setWrapStyleWord(true);
		lblCardDesc.setEditable(false);
		JScrollPane descScrollPane = new JScrollPane(lblCardDesc);
		
		nameAndDescPanel.add(lblCardName, BorderLayout.NORTH);
		nameAndDescPanel.add(descScrollPane, BorderLayout.CENTER);
		cardPanel.add(nameAndDescPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 1, 1));
		JButton btnPick = new JButton("Pick");
		btnPick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				if(mySession.getPickingPlayerID() == mySession.getMyPlayerID()) { // only allow picking if it is the player's turn
					mySession.sharePickChoice(card);
					if(!mySession.isClient()) { // if this is the server, apply choice now
						playerPickCard(card);
						nextTurn();
					}
				}
			}
		});
		
		JButton btnInfo = new JButton("Info");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // this action is only local, no need for network
				updateCardShown(floatingCardInfoPanel, card, false);
				floatingCardFrame.setVisible(true);
			}
		});
		
		buttonPanel.add(btnPick);
		buttonPanel.add(btnInfo);
		cardPanel.add(buttonPanel, BorderLayout.EAST);
		
		return cardPanel;
	}
	
	/**
	 * Picks a card, adds it to the picking player's card list, 
	 * and removes it from the drafted cards list. Argument is
	 * a Card object reference.
	 */
	public void playerPickCard(Card card) {
		mySession.getPickingPlayer().addCard(card); // local
		mySession.removeCard(card); // local
		cardListPanel.removeAll(); // local
	}
	
	/**
	 * Picks a card, adds it to the picking player's card list, 
	 * and removes it from the drafted cards list. Argument is
	 * the index of the drafted card list.
	 */
	public void playerPickCard(int index) {
		playerPickCard(mySession.getCardAt(index));
	}
	
	/**
	 * Updates the drafted card panel after modifying card list
	 */
	public void nextTurn() {
		// done in a worker thread, so the GUI doesn't freeze
		new SwingWorker<Void, Void>() {
		    @Override
		    public Void doInBackground() {
		    	mySession.nextTurn(); // may make a request to server on new round
				return null;
		    }

		    @Override
		    public void done() {
		        try {
		    		updateMenu(); // local
		    		populateLeftCardPanel(cardListPanel); // local
		    		cardListPanel.repaint(); // local
		    		cardListPanel.revalidate(); // local
		        }
		        catch(Exception e) {
		        	String why = null;
		            Throwable cause = e.getCause();
		            if (cause != null) 
		                why = cause.getMessage();
		             else 
		                why = e.getMessage();
		            System.err.println("Error shuffling cards: " + why);
		        }
		    }
		}.execute();
	}
	
	private JPanel createPlayerDeckPanel(Player player) {
		JPanel playerPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		
		JPanel cardDisplayPanel = new JPanel();
		
		JList<String> playerCardList = new JList<String>(player.getListModel());
		
		playerCardList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if(playerCardList.getSelectedIndex() != -1) // if the list isn't empty
					updateCardShown(cardDisplayPanel, player.getCardAt(playerCardList.getSelectedIndex()), true);
			}
			public void mouseReleased(MouseEvent evt) {
				if(playerCardList.getSelectedIndex() != -1)  // if the list isn't empty
					updateCardShown(cardDisplayPanel, player.getCardAt(playerCardList.getSelectedIndex()), true);
			}
		});
		
		JScrollPane playerCardListScrollPane = new JScrollPane(playerCardList);

		playerPanel.add(playerCardListScrollPane);
		playerPanel.add(cardDisplayPanel);
		
		return playerPanel;
	}
	
	/**
	 * Populates the tabbed pane with a panel for every player in the current session
	 * 
	 * @param tabbedPane the tabbed pane to populate
	 * @return the populated tabbed pane
	 */
	private JTabbedPane populatePlayerTabs(JTabbedPane tabbedPane) {
		for(int i = 1; i <= mySession.getTotalPlayers(); i++) {
			tabbedPane.addTab(mySession.getPlayerByID(i).getUsername(), createPlayerDeckPanel(mySession.getPlayerByID(i)));
		}
		return tabbedPane;
	}
	
	/**
	 * Updates the menubar info menu to display the correct turn & round number, as
	 * well as the player that is picking
	 * 
	 * @param infoMenu the info menu to be updated
	 */
	private void updateMenu() {
		menuInfo.removeAll();
		JMenuItem roundMnItem = new JMenuItem("Round " + mySession.getRoundNumber());
		JMenuItem turnMnItem = new JMenuItem("Turn " + mySession.getTurnNumber());
		JMenuItem pickingPlayerMnItem = new JMenuItem("#" + mySession.getPickingPlayer().getID() + " (" + mySession.getPickingPlayer().getUsername() + ") is picking");
		menuInfo.add(roundMnItem);
		menuInfo.add(turnMnItem);
		menuInfo.add(pickingPlayerMnItem);
		menuPickingPlayerName.setText(NOW_PICKING_STR + mySession.getPickingPlayer().getUsername());
		menuInfo.repaint();
		menuInfo.revalidate();
	}
	
	/**
	 * Updates the secondary JFrame containing card information
	 */
	private JPanel updateCardShown(JPanel panel, Card card, boolean resizeImg) {
		panel.removeAll();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel cardPanel = new JPanel();
		panel.add(cardPanel, BorderLayout.CENTER);
		JLabel imageLabel;
		try {
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			screenWidth = gd.getDisplayMode().getWidth();
			screenHeight = gd.getDisplayMode().getHeight();
			
			if((screenWidth == 1920 && screenHeight == 1080) || !resizeImg) {
				imageLabel = new JLabel(new ImageIcon(card.getImage()));
			}
			else {
				int cardWidth = (int)(screenWidth * MAX_CARD_SIZE_SCREEN_RATIO);
				int cardHeight = (int)(cardWidth * CARD_DIM_RATIO);
				imageLabel = new JLabel(new ImageIcon(card.getImage().getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH)));
			}
		}
		catch(Exception e) {
			try{
				// use an image in case the website cant find the image
				imageLabel = new JLabel(new ImageIcon("data/invalid.jpg"));
			}
			catch(Exception uwu){
				imageLabel = new JLabel(card.getFormattedName());
			}
		}
		cardPanel.add(imageLabel);
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		
		JLabel lblCardName = new JLabel(card.getOneLineInfo());
		lblCardName.setHorizontalAlignment(SwingConstants.CENTER);
		JTextArea lblCardDesc = new JTextArea(card.getBetterDesc());
		lblCardDesc.setLineWrap(true);
		lblCardDesc.setWrapStyleWord(true);
		lblCardDesc.setEditable(false);
		lblCardDesc.setRows(10);
		JScrollPane descScrollPane = new JScrollPane(lblCardDesc);
		
		infoPanel.add(lblCardName, BorderLayout.NORTH);
		infoPanel.add(descScrollPane, BorderLayout.CENTER);
		
		panel.add(infoPanel, BorderLayout.SOUTH);
		panel.repaint();
		panel.revalidate();
		
		return panel;
	}
	
	public void backgroundNetworkListenerLoop(NetworkHandler networkHandler) {
		if(networkHandler.isServer()) {
			// build loops for server...
			for(ClientInfoHolder each : NetworkServer.clientSet) {
				Thread serverThread = new Thread(new ClientListener(each));
				serverThread.start();
			}
			
		}
		else {
			// build loop for client...
			Thread clientThread = new Thread(new ServerListener((NetworkClient) networkHandler));
			clientThread.start(); // executes the run() method in the ServerListener class
		}
	}
	
	private class ClientListener implements Runnable {
		private ClientInfoHolder thisClient;
		
		public ClientListener(ClientInfoHolder clientInfoHandler) {
			this.thisClient = clientInfoHandler;
		}
		
		@Override
		public void run() {
			String incomingMsg = "";
			while(true) {
				incomingMsg = thisClient.receiveMsg();
				
				if(incomingMsg.startsWith(NetworkHandler.PICK_CARD_REQ_START)) {
					int cardIndex = Integer.parseInt(incomingMsg.split(NetworkHandler.MSG_SEPARATOR)[NetworkHandler.PICK_CARD_REQ_INDEX_CARD_INDEX]);
					playerPickCard(cardIndex);
					mySession.sharePickChoice(cardIndex);
					nextTurn();
				}
				else if(incomingMsg.startsWith(NetworkHandler.REQ_UPDATE_MSG_START)) {
					String[] cardNames = mySession.getCardNamesAsArray();
					thisClient.sendUpdatedCardList(cardNames);
				}
			}
		}
		
	}
	
	/**
	 * Listens for incoming messages from the server
	 * @author Seb
	 *
	 */
	private class ServerListener implements Runnable {
		private NetworkClient networkClientHandler;
		
		public ServerListener(NetworkClient networkHandler) {
			this.networkClientHandler = networkHandler;
		}

		@Override
		public void run() {
			String incomingMsg = "";
			while(true) {
				incomingMsg = networkClientHandler.waitToReceiveLineFromServer();
				
				if(incomingMsg.startsWith(NetworkHandler.PICKED_CARD_CONFIRMED_MSG_START)) {
					int cardIndex = Integer.parseInt(incomingMsg.split(NetworkHandler.MSG_SEPARATOR)[NetworkHandler.PICKED_CARD_CONFIRMED_MSG_INDEX_CARD_INDEX]);
					playerPickCard(cardIndex);
					nextTurn();
				}
				else if(incomingMsg.startsWith(NetworkHandler.UPDATE_CARD_LIST_MSG_START)) {
					String[] cardNames = NetworkHandler.parseUpdateCardListMsg(incomingMsg);
					mySession.resetAndFillCardList(cardNames);
					updateMenu(); // TODO : might be unnecessary here
		    		populateLeftCardPanel(cardListPanel); // local
		    		cardListPanel.repaint(); // local
		    		cardListPanel.revalidate(); // local
				}
			}
			
		}
		
	}
}
