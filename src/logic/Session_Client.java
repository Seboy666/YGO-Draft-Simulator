package logic;

import java.util.ArrayList;
import java.util.List;

import cards.Card;
import networking.NetworkClient;

public class Session_Client extends Session {
	
	private NetworkClient network;

	public Session_Client(int cards_per_round, int startPickPlayerID, int playerID, List<Player> playerList, NetworkClient network) {
		this.cards_per_round = cards_per_round;
		this.startPickPlayerID = startPickPlayerID;
		this.myPlayerID = playerID;
		this.playerList = playerList;
		this.cardList = new ArrayList<>();
		this.network = network;
		
		this.pickingPlayerID = this.startPickPlayerID;
		round = 1;
		turn = 1;
	}
	
	@Override
	public void sharePickChoice(Card cardToPick) {
		network.requestPickCard(cardList.indexOf(cardToPick));
	}
	
	@Override
	public void sharePickChoice(int cardIndex) {
		network.requestPickCard(cardIndex);
	}

	@Override
	public void shuffleCards() {
		cardList.clear();
		network.requestUpdatedCardList(myPlayerID);
	}
	
	@Override
	public List<Card> getCardList() { return cardList; }
	
	@Override
	public void addCard(Card card) { cardList.add(card); }
	
	@Override
	public void removeCard(int index) { cardList.remove(index); }
	
	@Override
	public void removeCard(Card card) { cardList.remove(card); }
	
	@Override
	public Card getCardAt(int index) { return cardList.get(index); }

	@Override
	public void addPlayer(Player player) { playerList.add(player); }
	
	@Override
	public Player getPlayerByID(int id) { return playerList.get(id - 1); }
	
	@Override
	public int getTotalPlayers() { return playerList.size(); }
	
	@Override
	public boolean isClient() { return true; }
	
}
