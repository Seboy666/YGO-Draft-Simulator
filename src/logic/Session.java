package logic;

import java.util.List;
import cards.*;
import readexternaldata.URLParser;

public abstract class Session {
	
	protected int cards_per_round;
	
	protected int round; // the round number
	protected int turn; // the turn number 
	protected int pickingPlayerID; // number that represents which player is currently picking a card (from 1 to playerList.size)
	protected int startPickPlayerID; // which player starts picking at the start of a round
	protected int myPlayerID; // the variable keeping track of the local player's ID
	
	protected List<Card> cardList;
	protected List<Player> playerList;
	
	public void nextRound() {
		round++;
		turn++;
		startPickPlayerID++;
		if(startPickPlayerID > playerList.size()) { // if outside list bounds...
			startPickPlayerID = 1; // loop back to first player
		}
		pickingPlayerID = startPickPlayerID;
		shuffleCards();
	}
	
	public void nextTurn() {
		// CHECK IF WE SHOULD START A NEW ROUND
		if(turn % playerList.size() == 0) {
			nextRound();
		}
		else {
			turn++;
			pickingPlayerID++;
			if(pickingPlayerID > playerList.size()) { // if outside list bounds...
				pickingPlayerID = 1; // loop back to first player
			}
		}
	}
	
	public void resetAndFillCardList(String[] cardNames) {
		cardList.clear();
		Card card;
		for(String each : cardNames) {
			card = URLParser.parseCardNameToCard(each);
			cardList.add(card);
		}
	}
	
	public synchronized String[] getCardNamesAsArray() {
		String[] myNameArray = new String[cardList.size()];
		for(int i = 0; i < cardList.size(); i++) { // this should keep the order
			myNameArray[i] = cardList.get(i).getFormattedName();
		}
		return myNameArray;
	}
	
	/**
	 * Sends a message to notify other players of the card picked. If this is the
	 * server, the message is sent to all clients. If this is a client, the message
	 * is sent to the server to be shared with other players.
	 * 
	 * @param cardToPick a card object representing the card picked
	 */
	public abstract void sharePickChoice(Card cardToPick);
	
	/**
	 * Sends a message to notify other players of the card picked. If this is the
	 * server, the message is sent to all clients. If this is a client, the message
	 * is sent to the server to be shared with other players.
	 * 
	 * @param cardIndex the index of the card picked
	 */
	public abstract void sharePickChoice(int cardIndex);
	
	public abstract void shuffleCards();
	
	public abstract void addCard(Card card);
	
	public abstract void removeCard(int index);
	
	public abstract void removeCard(Card card);
	
	public abstract Card getCardAt(int index);
	
	public abstract List<Card> getCardList();
	
	public abstract void addPlayer(Player player);
	
	public abstract Player getPlayerByID(int id);
	
	public abstract int getTotalPlayers();
	
	public abstract boolean isClient();
	
	public List<Player> getPlayerList() { return playerList; }
	
	public int getCardsPerRound() { return cards_per_round; }
	
	public int getPickingPlayerID() { return pickingPlayerID; }
	
	public int getStartPickPlayerID() { return startPickPlayerID; }
	
	public Player getPickingPlayer() { return getPlayerByID(pickingPlayerID); }
	
	public int getRoundNumber() { return round; }
	
	public int getTurnNumber() { return turn; }
	
	public int getMyPlayerID() { return myPlayerID; }
}
