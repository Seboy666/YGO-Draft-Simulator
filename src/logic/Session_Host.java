package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import cards.Card;
import networking.NetworkServer;
import readexternaldata.DatabaseReader;
import readexternaldata.URLParser;

public class Session_Host extends Session {

	//private static final String WIKI_URL_START = "https://yugioh.fandom.com/wiki/";
	
	private boolean withElim; // if set to true, cards will be removed from the database as they are picked
	
	private int extra_and_rituals_per_round;
	private int spells_traps_per_round;
	private int main_deck_cards_per_round; // without counting rituals
	
	private DatabaseReader db;
	private NetworkServer network;
	
	public Session_Host(boolean withElim, int cards_per_round, int extra_and_rituals_per_round,
			int spells_traps_per_round, List<Player> playerList, DatabaseReader db, NetworkServer network) {
		this.withElim = withElim;
		this.cards_per_round = cards_per_round;
		this.extra_and_rituals_per_round = extra_and_rituals_per_round;
		this.spells_traps_per_round = spells_traps_per_round;
		this.main_deck_cards_per_round = cards_per_round - (extra_and_rituals_per_round + spells_traps_per_round);
		this.playerList = playerList;
		this.cardList = new ArrayList<>();
		this.db = db;
		this.network = network;
		this.myPlayerID = 1; // the host is always ID = 1
				
		startPickPlayerID = new Random().nextInt(playerList.size()); // randomly decide the player that starts
		startPickPlayerID++; // increment because we want a value from 1 to playerList.size (inclusive)
		pickingPlayerID = startPickPlayerID;
		round = 1;
		turn = 1;
		shuffleCards();
	}
	
	@Override
	public void sharePickChoice(Card cardToPick) {
		network.broadcastCardChoice(pickingPlayerID, cardList.indexOf(cardToPick));
	}
	
	@Override
	public void sharePickChoice(int cardIndex) {
		network.broadcastCardChoice(pickingPlayerID, cardIndex);
	}
	
	@Override
	public void shuffleCards() {
		cardList.clear();
		
		int rndIndex;
		String cardName;
		Card card;
		
		for(int i = 0; i < main_deck_cards_per_round; i++) {
			rndIndex = new Random().nextInt(db.getMainDeckCardNames().size()); // get a random main deck card name
			cardName = db.getMainDeckCardNames().get(rndIndex);
			card = URLParser.parseCardNameToCard(cardName);
			cardList.add(card);
		}
		
		for(int j = 0; j < spells_traps_per_round; j++) {
			rndIndex = new Random().nextInt(db.getSpellAndTrapCardNames().size()); // get a random S&T card name
			cardName = db.getSpellAndTrapCardNames().get(rndIndex);
			card = URLParser.parseCardNameToCard(cardName);
			cardList.add(card);
		}
		
		for(int k = 0; k < extra_and_rituals_per_round; k++) {
			rndIndex = new Random().nextInt(db.getExtraAndRitualCardNames().size()); // get a random extra or ritual card name
			cardName = db.getExtraAndRitualCardNames().get(rndIndex);
			card = URLParser.parseCardNameToCard(cardName);
			cardList.add(card);
		}
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
	public boolean isClient() { return false; }
	
}
