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
	private Set<RelatedCard> unbuffableCards;
	
	private static final double DEFAULT_BUFF_PERCENT = 10.0d;
	private static final double FLAT_BUFF_WEIGHT_FUSION_SUPPORT = 7.0d;
	private static final double FLAT_BUFF_WEIGHT_RITUAL_SUPPORT = 20.0d;
	private static final double FLAT_BUFF_WEIGHT_TUNERS = 2.0d;
	
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
		unbuffableCards = new HashSet<RelatedCard>();
				
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
	 * Gives some ritual support cards a small buff, all at once.
	 */
	private void buffRitualSupportCards() {
		Set<RelatedCard> ritualSupportCards = new HashSet<RelatedCard>(); // TODO: does this really have to be coded this way?
		ritualSupportCards.add(new RelatedCard("Sonic Bird", "Sonic_Bird", 1, "Monster"));
		ritualSupportCards.add(new RelatedCard("Manju of the Ten Thousand Hands", "Manju_of_the_Ten_Thousand_Hands", 1, "Monster"));
		ritualSupportCards.add(new RelatedCard("Senju of the Thousand Hands", "Senju_of_the_Thousand_Hands", 1, "Monster"));
		ritualSupportCards.add(new RelatedCard("Preparation of Rites", "Preparation_of_Rites", 1, "Spell"));
		ritualSupportCards.add(new RelatedCard("Advanced Ritual Art", "Advanced_Ritual_Art", 1, "Spell"));
		
		for(RelatedCard each : ritualSupportCards) {
			if(!isBuffed(each.getFormattedName())) {
				buffedCards.add(each);
				db.buffCardWeight(each.getFormattedName(), FLAT_BUFF_WEIGHT_RITUAL_SUPPORT);
			}
		}
	}
	
	/**
	 * Gives some fusion support cards a small buff, all at once.
	 */
	private void buffFusionSupportCards() {
		Set<RelatedCard> fusionSupportCards = new HashSet<RelatedCard>(); // TODO: does this really have to be coded this way?
		fusionSupportCards.add(new RelatedCard("Fusion Gate", "Fusion_Gate", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Power Bond", "Power_Bond", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Overload Fusion", "Overload_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Dragon's Mirror", "Dragon's_Mirror", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Fusion Recovery", "Fusion_Recovery", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Parallel World Fusion", "Parallel_World_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Fusion Sage", "Fusion_Sage", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Future Fusion", "Future_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Gem-Knight Fusion", "Gem-Knight_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Instant Fusion", "Instant_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Miracle Fusion", "Miracle_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Miracle Synchro_Fusion", "Miracle_Synchro_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Revoke Fusion", "Revoke_Fusion", 1, "Spell"));
		fusionSupportCards.add(new RelatedCard("Super Polymerization", "Super_Polymerization", 1, "Spell"));
		
		for(RelatedCard each : fusionSupportCards) {
			if(!isBuffed(each.getFormattedName())) {
				buffedCards.add(each);
				db.buffCardWeight(each.getFormattedName(), FLAT_BUFF_WEIGHT_FUSION_SUPPORT);
			}
		}
		
	}
	
	/**
	 * If the card is not buffed, increase the weight of all cards related to it,
	 * then add them to the buffed cards set
	 * 
	 * @param card All cards related to this one will be buffed by this method
	 */
	private void buffWeightOfRelatedCards(Card card) {
		makeUnbuffable(card); // when a card is picked, make it unbuffable
		
		Set<RelatedCard> relatedCards = card.getRelatedCardNames();
		for(RelatedCard rel : relatedCards) {
			if(rel.getFormattedName().contentEquals("Polymerization") || rel.getFormattedName().contentEquals("Dark_Fusion"))
				buffFusionSupportCards();
			else if(card.getCategory().contains("Ritual"))
				buffRitualSupportCards();
			
			if(isUnbuffable(rel.getFormattedName())) { // if the card is unbuffable...
				RelatedCard toRemove = new RelatedCard("EMPTY", "EMPTY", 0, "none");
				for(RelatedCard each : unbuffableCards) {
					if(each.getFormattedName().contentEquals(rel.getFormattedName())) {
						toRemove = each;
					}
				}
				if(!toRemove.getFormattedName().contentEquals("EMPTY")) {
					unbuffableCards.remove(toRemove); // make it buffable next time
					System.out.println(toRemove.getFormattedName() + " IS NOW BUFFABLE");
				}
			}
			else {
				if(!isBuffed(rel.getFormattedName())) {
					buffedCards.add(rel); // if not in the list, add it to the list
					db.buffCardWeight(rel, DEFAULT_BUFF_PERCENT);
					
					System.out.println("Buffed Cards: ");
					for(RelatedCard each : buffedCards) {
						System.out.println(each.getFormattedName());
					}
					System.out.println("\n");
				}
			}
		}
		
		if(card.getCategory().contains("Synchro")) { // if the card is a synchro
			db.buffAllTuners(FLAT_BUFF_WEIGHT_TUNERS); // TODO: are tuners added to buffed cards list?
		}
		
	}
	
	private boolean isBuffed(String formattedName) {
		for (RelatedCard each : buffedCards) {
			if(each.getFormattedName().contentEquals(formattedName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isUnbuffable(String formattedName) {
		for (RelatedCard each : unbuffableCards) {
			if(each.getFormattedName().contentEquals(formattedName)) {
				return true;
			}
		}
		return false;
	}
	
	private void makeUnbuffable(Card card) {
		if(!card.getRelatedCardNames().isEmpty()) { // if it has related cards,
			if(!isUnbuffable(card.getFormattedName())) { // if it is not in the list, stop it from being buffed again
				unbuffableCards.add(new RelatedCard(card.getName(), card.getFormattedName(), 1, card.getColor()));
				
				System.out.println("Unbuffable Cards: ");
				for(RelatedCard each : unbuffableCards) {
					System.out.println(each.getFormattedName());
				}
				System.out.println("**End**\n");
			}
		}
	}
	
	private void checkRemoveAllTunerBuffs(Card card) {
		if(card.getCategory().contains("Tuner")) {
			db.buffAllTuners(DatabaseReader.DEFAULT_WEIGHT); // returns all tuners to default weight
			// TODO: are tuners removed from buffed cards list?
		}
	}
	
	/**
	 * Removes the card from buffed cards list if it has a number of 1, otherwise
	 * decrement number.
	 * 
	 * @param card The card we want to debuff
	 */
	private void removeBuffOnce(Card card) {
		checkRemoveAllTunerBuffs(card);
		
		RelatedCard toRemove = new RelatedCard("EMPTY", "EMPTY", 0, "none");
		for(RelatedCard each : buffedCards) {
			if(each.getFormattedName().contentEquals(card.getFormattedName())) {
				each.decrementNumber();
				if(each.getNumber() <= 0) { // if number is at 0
					toRemove = each;
					break;
				}
			}
		}
		if(!toRemove.getFormattedName().contentEquals("EMPTY")) {
			buffedCards.remove(toRemove);
			db.buffCardWeight(toRemove.getFormattedName(), DatabaseReader.DEFAULT_WEIGHT); // reset weight
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
