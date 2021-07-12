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
import cards.*;
import logic.*;
import networking.*;

public class DraftSessionGUI {

	private Session mySession;
	
	private static final int NUM_OF_ROWS_FOR_LEFT_PANEL = 6;
	
	private JPanel cardListPanel;
	private JMenu menuInfo;
	private JFrame floatingCardFrame;
	private JPanel floatingCardInfoPanel;
	
	/**
	 * When set to false, buttons will not respond to clicks. Used to avoid
	 * undesired behavior.
	 */
	private boolean allowClick;
	
	public DraftSessionGUI(Session session) {
		mySession = session;
		this.cardListPanel = new JPanel();
		this.menuInfo = new JMenu("Info");
		allowClick = true;
	}
	
	public Session getSession() { return mySession; }
	
	public void createMainWindow() {
		
		floatingCardFrame = new JFrame("Card info"); // a window that appears when clicking "info" on a drafted card panel
		floatingCardFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		floatingCardFrame.setSize(400, 690);
		floatingCardFrame.setMinimumSize(new Dimension(400, 300));
		floatingCardInfoPanel = new JPanel();
		floatingCardFrame.setContentPane(floatingCardInfoPanel);
		
		// Create a JFrame with a title, append to that title the player's username
		JFrame frame = new JFrame("YGO Draft Simulator - " + mySession.getPlayerByID(mySession.getMyPlayerID()).getUsername());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        // close all sockets?
		    }
		});
		frame.setSize(800, 600);
		frame.setMinimumSize(new Dimension(400, 300));
		
		JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(windowPanel);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2, 0, 0));
		windowPanel.add(mainPanel);
		
		mainPanel.add(cardListPanel);
		
		int numOfColumnsForDraftedCards;
		if(mySession.getCardsPerRound() % NUM_OF_ROWS_FOR_LEFT_PANEL == 0) {
			numOfColumnsForDraftedCards = mySession.getCardsPerRound()/NUM_OF_ROWS_FOR_LEFT_PANEL;
		} else {
			numOfColumnsForDraftedCards = mySession.getCardsPerRound()/NUM_OF_ROWS_FOR_LEFT_PANEL + 1;
		}
		cardListPanel.setLayout(new GridLayout(NUM_OF_ROWS_FOR_LEFT_PANEL, numOfColumnsForDraftedCards, 0, 0));
		
		populateLeftCardPanel(cardListPanel);
		
		JPanel allPlayersDeckPanel = new JPanel();
		mainPanel.add(allPlayersDeckPanel);
		allPlayersDeckPanel.setLayout(new GridLayout(1, mySession.getTotalPlayers(), 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		populatePlayerTabs(tabbedPane);
		allPlayersDeckPanel.add(tabbedPane);
		
		JMenuBar menuBar = new JMenuBar();
		windowPanel.add(menuBar, BorderLayout.NORTH);
		
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
		
		updateInfoMenu(menuInfo);
		
		JMenu menuGame = new JMenu("Game");
		JMenuItem newRoundMnItem = new JMenuItem("Start new round");
		newRoundMnItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				startNewRound();
			}
		});
		menuGame.add(newRoundMnItem);

		menuBar.add(menuSettings);
		menuBar.add(menuInfo);
		menuBar.add(menuGame);
		
		frame.setVisible(true);
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
						e1.printStackTrace();
						try (PrintWriter out = new PrintWriter("data/player" + each.getID() + ".ydk")) { // if the username is invalid, try with this file name instead
							
						} catch (FileNotFoundException e2) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	public void startNewRound() { // TODO: not allowed for clients, maybe disable this method entirely?
		mySession.nextRound();
		cardListPanel.removeAll();
		populateLeftCardPanel(cardListPanel);
		cardListPanel.repaint();
		cardListPanel.revalidate();
		updateInfoMenu(menuInfo);
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
			lblImage = new JLabel(new ImageIcon(card.getImage().getScaledInstance(110, 160, Image.SCALE_SMOOTH)));
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
		
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 2, 2));
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
				updateCardShown(floatingCardInfoPanel, card);
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
		    		updateInfoMenu(menuInfo); // local
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
		JPanel playerPanel = new JPanel(new BorderLayout());
		JLabel lblPlayerName = new JLabel(player.getUsername());
		
		JPanel cardDisplayPanel = new JPanel(new BorderLayout());
		
		JList<String> playerCardList = new JList<String>(player.getListModel());
		playerCardList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList<String> thisList = (JList<String>)evt.getSource();
				int index = thisList.getSelectedIndex();
				if(index != -1) { // if the list isn't empty
					updateCardShown(cardDisplayPanel, player.getCardAt(index));
				}
			}
		});
		
		JScrollPane playerCardListScrollPane = new JScrollPane(playerCardList);
		playerPanel.add(lblPlayerName, BorderLayout.NORTH);
		playerPanel.add(playerCardListScrollPane, BorderLayout.WEST);
		
		playerPanel.add(cardDisplayPanel, BorderLayout.CENTER);
		
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
	private void updateInfoMenu(JMenu infoMenu) {
		infoMenu.removeAll();
		JMenuItem roundMnItem = new JMenuItem("Round " + mySession.getRoundNumber());
		JMenuItem turnMnItem = new JMenuItem("Turn " + mySession.getTurnNumber());
		JMenuItem pickingPlayerMnItem = new JMenuItem("#" + mySession.getPickingPlayer().getID() + " (" + mySession.getPickingPlayer().getUsername() + ") is picking");
		infoMenu.add(roundMnItem);
		infoMenu.add(turnMnItem);
		infoMenu.add(pickingPlayerMnItem);
		infoMenu.repaint();
		infoMenu.revalidate();
	}
	
	/**
	 * Updates the secondary JFrame containing card information
	 */
	private JPanel updateCardShown(JPanel panel, Card card) {
		panel.removeAll();
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel cardPanel = new JPanel();
		panel.add(cardPanel, BorderLayout.CENTER);
		JLabel imageLabel;
		try {
			imageLabel = new JLabel(new ImageIcon(card.getImage()));
		}
		catch(Exception e) {
			imageLabel = new JLabel(card.getFormattedName());
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
					//String[] cardNames = NetworkHandler.parseUpdateCardListMsg(incomingMsg);
					//mySession.resetAndFillCardList(cardNames);
		    		//populateLeftCardPanel(cardListPanel); // local
		    		//cardListPanel.repaint(); // local
		    		//cardListPanel.revalidate(); // local
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
					updateInfoMenu(menuInfo); // TODO : might be unnecessary here
		    		populateLeftCardPanel(cardListPanel); // local
		    		cardListPanel.repaint(); // local
		    		cardListPanel.revalidate(); // local
				}
			}
			
		}
		
	}
}
