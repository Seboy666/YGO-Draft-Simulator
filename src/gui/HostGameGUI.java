package gui;

import logic.Player;
import logic.Session;
import logic.Session_Host;
import networking.NetworkServer;
import readexternaldata.DatabaseReader;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;

public class HostGameGUI {
	private Session_Host session;
	private DatabaseReader db;
	
	private boolean withElim; // if set to true, cards will be removed from the database as they are picked
	
	private int cards_per_round;
	private int extra_and_rituals_per_round;
	private int spells_traps_per_round;
	
	private JTextField cardsPerRoundTextField;
	private JTextField extraAndRitualsTextField;
	private JTextField spellAndTrapTextField;
	
	private List<Player> playerList;
	private JTextField usernameTxtField;
	private JTextArea connectedPlayersTextArea;
	
	private boolean canLaunchSession; // when set to true, the draft session can start
	
	private NetworkServer network;
	
	private static final int MAX_CARDS_PER_DRAFT = 30;
	private static final String DEFAULT_USERNAME = "Host";
	private static final String LOCAL_HOST_IP = "127.0.0.1";
	private static final String DEFAULT_NUM_SPELL_TRAPS_string = "1";
	private static final int DEFAULT_NUM_SPELL_TRAPS_int = 1;
	
	public HostGameGUI() {
		db = new DatabaseReader();
		canLaunchSession = false;
		playerList = new ArrayList<Player>();
	}
	
	public boolean canLaunchSession() { return canLaunchSession; }
	public List<Player> getPlayerList() { return playerList; }
	public void addPlayer(Player player) { playerList.add(player); }
	public Session getSession() { return session; }
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void createHostGameGUI() {
		JFrame frame = new JFrame("YGO Draft Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 500);
		frame.setMinimumSize(new Dimension(400, 300));
		frame.getContentPane().setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new GridLayout(2, 1, 0, 0));
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new GridLayout(4, 0, 0, 0));
		mainPanel.add(valuesPanel);
		
		JPanel panel = new JPanel();
		valuesPanel.add(panel);
		
		JCheckBox chckbxWithElimination = new JCheckBox("With Elimination");
		panel.add(chckbxWithElimination);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		panel.add(separator);
		
		JLabel lblCardsPerRound = new JLabel("Cards per round : ");
		panel.add(lblCardsPerRound);
		
		cardsPerRoundTextField = new JTextField();
		cardsPerRoundTextField.setText("12");
		panel.add(cardsPerRoundTextField);
		cardsPerRoundTextField.setColumns(4);
		
		JPanel panel_1 = new JPanel();
		valuesPanel.add(panel_1);
		
		JLabel lblExtraDeckMonsters = new JLabel("Extra Deck Monsters and Rituals per round : ");
		panel_1.add(lblExtraDeckMonsters);
		
		extraAndRitualsTextField = new JTextField();
		extraAndRitualsTextField.setText("2");
		panel_1.add(extraAndRitualsTextField);
		extraAndRitualsTextField.setColumns(4);
		
		JPanel panel_2 = new JPanel();
		valuesPanel.add(panel_2);
		
		JLabel lblMinimumSpell = new JLabel("Minimum Spell & Trap cards per round : ");
		panel_2.add(lblMinimumSpell);
		
		spellAndTrapTextField = new JTextField();
		spellAndTrapTextField.setText(DEFAULT_NUM_SPELL_TRAPS_string);
		panel_2.add(spellAndTrapTextField);
		spellAndTrapTextField.setColumns(4);
		
		JPanel panel_3 = new JPanel();
		valuesPanel.add(panel_3);
		
		JLabel lblWaitForAll = new JLabel("Wait for all players to connect before launching the game !");
		lblWaitForAll.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_3.add(lblWaitForAll);
		
		JButton btnBeginDraft = new JButton("Begin Draft");
		btnBeginDraft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				withElim = chckbxWithElimination.isSelected();
				try {
					cards_per_round = Integer.parseInt(cardsPerRoundTextField.getText());
					extra_and_rituals_per_round = Integer.parseInt(extraAndRitualsTextField.getText());
					spells_traps_per_round = Integer.parseInt(spellAndTrapTextField.getText());
					
					if(extra_and_rituals_per_round + spells_traps_per_round >= cards_per_round) { // ensure values are realistic
						setDefaultCardNumbers();
					}
					else if(extra_and_rituals_per_round < 0 || spells_traps_per_round < 0 || cards_per_round <= 0) {
						setDefaultCardNumbers();
					}
					else if(cards_per_round > MAX_CARDS_PER_DRAFT) {
						cards_per_round = MAX_CARDS_PER_DRAFT;
					}
				}
				catch(Exception excep) {
					setDefaultCardNumbers();
				}
				String username = usernameTxtField.getText().replaceAll("/", "");
				username = username.replaceAll(":", "");
				if(username.length() > 15) {
					username = username.substring(0, 14); // shorten username length
				}
				Player host = new Player(username, 1); // host is always first player
				playerList.add(0, host);
				network.setUsername(usernameTxtField.getText());
				session = new Session_Host(withElim, cards_per_round, extra_and_rituals_per_round, spells_traps_per_round, playerList, db, network);
				canLaunchSession = true;
				frame.dispose();
				DraftSessionGUI draftSession = new DraftSessionGUI(session);
				draftSession.createMainWindow();
				draftSession.backgroundNetworkListenerLoop(network);
				// once loop is started, tell all clients to start
				network.broadcastStartGame(session.getStartPickPlayerID(), cards_per_round);
			}
		});
		
		JLabel lblEnterUsername = new JLabel("Enter Username : ");
		panel_3.add(lblEnterUsername);
		
		usernameTxtField = new JTextField();
		usernameTxtField.setText(DEFAULT_USERNAME);
		panel_3.add(usernameTxtField);
		usernameTxtField.setColumns(10);
		
		panel_3.add(btnBeginDraft);
		
		JPanel lobbyPanel = new JPanel();
		mainPanel.add(lobbyPanel);
		lobbyPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblConnectedPlayers = new JLabel("Connected Players :");
		lblConnectedPlayers.setHorizontalAlignment(SwingConstants.CENTER);
		lobbyPanel.add(lblConnectedPlayers, BorderLayout.NORTH);
		
		
		connectedPlayersTextArea = new JTextArea();
		connectedPlayersTextArea.setLineWrap(true);
		connectedPlayersTextArea.setWrapStyleWord(true);
		connectedPlayersTextArea.setEditable(false);
		JScrollPane connectedPlayersScrollPane = new JScrollPane(connectedPlayersTextArea);
		lobbyPanel.add(connectedPlayersScrollPane, BorderLayout.CENTER);
		
		JLabel lblHostALobby = new JLabel("Host a lobby");
		lblHostALobby.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblHostALobby.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblHostALobby, BorderLayout.NORTH);
		
		network = new NetworkServer(DEFAULT_USERNAME, LOCAL_HOST_IP, connectedPlayersTextArea);
		listenForPlayers();
		
		frame.setVisible(true);
	}
	
	private void setDefaultCardNumbers() {
		cards_per_round = 12;
		extra_and_rituals_per_round = 2;
		spells_traps_per_round = DEFAULT_NUM_SPELL_TRAPS_int;
	}
	
	/**
	 * Listens for incoming players connecting to the server and those that do
	 * connect are given an ID and are added to the playerList.
	 */
	private void listenForPlayers() {
		Thread bgThread = new Thread(new PlayerListener());
		bgThread.start();
	}
	
	private class PlayerListener implements Runnable {
		
		public PlayerListener() {}
		
		@Override
		public void run() {
			network.listenForConnectingPlayers(playerList);
		}
		
	}
	
}
