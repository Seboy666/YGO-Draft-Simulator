package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cards.Card;
import cards.RelatedCard;
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
	private Set<RelatedCard> buffedCards;
	
	private static final double DEFAULT_BUFF_PERCENT = 20.0d;
	
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
		buffedCards = new HashSet<RelatedCard>();
				
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
	public synchronized void shuffleCards() {
		cardList.clear();
		
		String formattedCardName;
		Card card;
		
		for(int i = 0; i < main_deck_cards_per_round; i++) {
			formattedCardName = db.getRandomMainDeckCard(withElim);
			card = URLParser.parseCardNameToCard(formattedCardName);
			cardList.add(card);
			removeBuffOnce(card);
		}
		
		for(int j = 0; j < spells_traps_per_round; j++) {
			formattedCardName = db.getRandomSpellTrapCard(withElim);
			card = URLParser.parseCardNameToCard(formattedCardName);
			cardList.add(card);
			removeBuffOnce(card);
		}
		
		for(int k = 0; k < extra_and_rituals_per_round; k++) {
			formattedCardName = db.getRandomExtraAndRitualCard(withElim);
			card = URLParser.parseCardNameToCard(formattedCardName);
			cardList.add(card);
			removeBuffOnce(card);
		}
	}
	
	@Override
	public List<Card> getCardList() { return cardList; }
	
	@Override
	public void addCard(Card card) { cardList.add(card); }
	
	/**
	 * Increases the weight of all cards related to the argument, then adds them to the buffed cards set
	 * 
	 * @param card All cards related to this one will be buffed by this method
	 */
	private void buffWeightOfRelatedCards(Card card) {
		Set<RelatedCard> relatedCards = card.getRelatedCardNames();
		for(RelatedCard rel : relatedCards) {
			db.buffCardWeight(rel, DEFAULT_BUFF_PERCENT);
			if(buffedCards.contains(rel)) {
				rel.incrementNumber(); // if already in the list, increment its number
			}
			else {
				buffedCards.add(rel); // otherwise add it to the list
			}
		}
		
	}
	
	/**
	 * Removes the card from buffed cards list if it has a number of 1, otherwise
	 * decrement number.
	 * 
	 * @param card The card we want to debuff
	 */
	private void removeBuffOnce(Card card) {
		RelatedCard toRemove = new RelatedCard("EMPTY", "EMPTY", 0, "none");
		for(RelatedCard each : buffedCards) {
			if(each.getFormattedName().contentEquals(card.getFormattedName())) {
				each.decrementNumber();
				if(each.getNumber() == 0) { // if number is at 0
					toRemove = each;
					break;
				}
			}
		}
		if(toRemove.getFormattedName() != "EMPTY") {
			buffedCards.remove(toRemove);
			db.buffCardWeight(toRemove.getFormattedName(), DatabaseReader.DEFAULT_WEIGHT);
		}
	}
	
	@Override
	public void removeCard(int index) {
		Card card = cardList.get(index);
		cardList.remove(index);
		buffWeightOfRelatedCards(card);
	}
	
	@Override
	public void removeCard(Card card) {
		cardList.remove(card);
		buffWeightOfRelatedCards(card);
	}
	
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
