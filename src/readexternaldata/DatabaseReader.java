package readexternaldata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cards.RelatedCard;
import logic.WeightedRandomBag;

public class DatabaseReader {
	private List<String[]> namesAndColorsDB; // cards and their color (spell, trap, monster, ritual, fusion, etc.)
	private WeightedRandomBag<String> cardNames; // just card names
	
	private WeightedRandomBag<String> fusionCards; // lists for single colors
	private WeightedRandomBag<String> monsterCards;
	private WeightedRandomBag<String> ritualCards;
	private WeightedRandomBag<String> spellCards;
	private WeightedRandomBag<String> synchroCards;
	private WeightedRandomBag<String> trapCards;
	private WeightedRandomBag<String> tunerCards;
	private WeightedRandomBag<String> xyzCards;
	
	private WeightedRandomBag<String> extraDeckAndRitualCards; // Fusion, synchros, xyz and rituals
	private WeightedRandomBag<String> extraDeckCards; // Fusion, synchros and xyz
	private WeightedRandomBag<String> mainDeckCards; // spell, traps and monsters, NO RITUALS
	private WeightedRandomBag<String> spellAndTrapCards; // just spell and traps
	
	public static final double DEFAULT_WEIGHT = 1.0d;
	private static final String STR_FUSION = "Fusion";
	private static final String STR_MONSTER = "Monster";
	private static final String STR_RITUAL = "Ritual";
	private static final String STR_SPELL = "Spell";
	private static final String STR_SYNCHRO = "Synchro";
	private static final String STR_TRAP = "Trap";
	private static final String STR_TUNER = "Tuner";
	private static final String STR_XYZ = "Xyz";
	
	public DatabaseReader() {
		namesAndColorsDB = makeDatabaseFromTxt(); // create entire DB
		
		this.cardNames = new WeightedRandomBag<String>(); // initialize all bags
		this.fusionCards = new WeightedRandomBag<String>();
		this.monsterCards = new WeightedRandomBag<String>();
		this.ritualCards = new WeightedRandomBag<String>();
		this.spellCards = new WeightedRandomBag<String>();
		this.synchroCards = new WeightedRandomBag<String>();
		this.trapCards = new WeightedRandomBag<String>();
		this.tunerCards = new WeightedRandomBag<String>();
		this.xyzCards = new WeightedRandomBag<String>();
		
		extraDeckAndRitualCards = new WeightedRandomBag<String>(); // Bags that include multiple colors
		extraDeckCards = new WeightedRandomBag<String>();
		mainDeckCards = new WeightedRandomBag<String>();
		spellAndTrapCards = new WeightedRandomBag<String>();
		
		makeAllColoredWeightedBags();
		
		makeExtraDeckAndRitualsLists();
		makeMainDeckLists();
		
		makeCardNamesDB();
	}
	
	
	public List<String[]> getFullDB() { return namesAndColorsDB; }
	
	public WeightedRandomBag<String> getCardNames() { return cardNames; }
	
	public WeightedRandomBag<String> getFusionCardNames() { return fusionCards; }
	public WeightedRandomBag<String> getMonsterCardNames() { return monsterCards; }
	public WeightedRandomBag<String> getRitualCardNames() { return ritualCards; }
	public WeightedRandomBag<String> getSpellCardNames() { return spellCards; }
	public WeightedRandomBag<String> getSynchroCardNames() { return synchroCards; }
	public WeightedRandomBag<String> getTrapCardNames() { return trapCards; }
	public WeightedRandomBag<String> getTunerCardNames() { return tunerCards; }
	public WeightedRandomBag<String> getXyzCardNames() { return xyzCards; }
	
	public WeightedRandomBag<String> getExtraAndRitualCardNames() { return extraDeckAndRitualCards; }
	public WeightedRandomBag<String> getExtraDeckCardNames() { return extraDeckCards; }
	
	/**
	 * Does NOT contain ritual cards. Contains all monsters, tuners, spells and traps in the
	 * whole database.
	 * 
	 * @return The main deck card bag
	 */
	public WeightedRandomBag<String> getMainDeckCardNames() { return mainDeckCards; }
	public WeightedRandomBag<String> getSpellAndTrapCardNames() { return spellAndTrapCards; }
	
	public String getRandomExtraAndRitualCard(boolean withElim) {
		return extraDeckAndRitualCards.getRandom(withElim);
	}
	
	public String getRandomExtraDeckCard(boolean withElim) {
		return extraDeckCards.getRandom(withElim);
	}
	
	public String getRandomMainDeckCard(boolean withElim) {
		return mainDeckCards.getRandom(withElim);
	}
	
	public String getRandomSpellTrapCard(boolean withElim) {
		return spellAndTrapCards.getRandom(withElim);
	}
	
	/**
	 * Safely changes the weight of the card with the given name by updating the
	 * total weight of all card bags.
	 * 
	 * @param formattedCardName The name of the card whose weight we want to modify.
	 * @param newWeight The new weight given to the card.
	 */
	public void buffCardWeight(String formattedCardName, double newWeight) {
		cardNames.modifyWeight(formattedCardName, newWeight);
		
		mainDeckCards.recalcTotalWeight();
		spellAndTrapCards.recalcTotalWeight();
		extraDeckAndRitualCards.recalcTotalWeight();
	}
	
	/**
	 * Safely changes the weight of the card so it has a % chance to be pulled.
	 * 
	 * @param relatedCard The card we want to buff.
	 * @param percent The minimum percent chance that we pull the card once during a round.
	 */
	public void buffCardWeight(RelatedCard relatedCard, double percent) {
		double relativeWeight = 1;
		if(relatedCard.getCategory() == "Monster") {
			relativeWeight = (mainDeckCards.size() / 100) * percent;
			
			// this makes sure the card is of the correct category
			for(String[] each : namesAndColorsDB) {
				if(relatedCard.getFormattedName() == each[0]) { // once the matching name is found
					if(each[1] == "Fusion" || each[1] == "Synchro" || each[1] == "Xyz") { // check it's color
						relatedCard.setCategoryToExtra();
					}
					relativeWeight = (extraDeckAndRitualCards.size() / 100) * percent;
					break;
				}
			}
		}
		else if(relatedCard.getCategory() == "Spell") {
			relativeWeight = (spellAndTrapCards.size() / 100) * percent;
		}
		else { // ritual or extra
			relativeWeight = (extraDeckAndRitualCards.size() / 100) * percent;
		}
		
		cardNames.modifyWeight(relatedCard.getFormattedName(), relativeWeight);
		
		mainDeckCards.recalcTotalWeight();
		spellAndTrapCards.recalcTotalWeight();
		extraDeckAndRitualCards.recalcTotalWeight();
	}
	
	private void makeCardNamesDB() {
		cardNames.concatenate(mainDeckCards);
		cardNames.concatenate(extraDeckAndRitualCards);
	}
	
	private List<String[]> makeDatabaseFromTxt() {
		try {
			BufferedReader buf = new BufferedReader(new FileReader("data/cards.txt"));
			List<String[]> words = new ArrayList<String[]>();
            String lineJustFetched = null;
            String[] wordsArray;

            while(true) {
                lineJustFetched = buf.readLine();
                if(lineJustFetched == null) {  // if line is empty, stop loop
                    break; 
                } 
                else {
                    wordsArray = lineJustFetched.split("\t"); // split where a tab character is found
                    words.add(wordsArray);
                }
            }

            buf.close();
            return words;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<String[]>();
		}
	}
	
	private void makeAllColoredWeightedBags() {
		for(String[] each : namesAndColorsDB) {
			if(each[1].equals(STR_FUSION)) {
				fusionCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_MONSTER)) {
				monsterCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_RITUAL)) {
				ritualCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_SPELL)) {
				spellCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_SYNCHRO)) {
				synchroCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_TRAP)) {
				trapCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_TUNER)) {
				tunerCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
			else if(each[1].equals(STR_XYZ)) {
				xyzCards.addEntry(each[0], DEFAULT_WEIGHT);
			}
		}
	}
	
	/**
	 * Fills the weighted bag with formatted card names that match the given color.
	 * All elements will be given the default weight.
	 * 
	 * @param color The card color with which we want the bag to be filled.
	 * @param bag   The bag we want to fill.
	 */
	private void makeWeightedBagOfThisColor(String color, WeightedRandomBag<String> bag) {
		for(String[] each : namesAndColorsDB) {
			if(each[1].equals(color)) {
				bag.addEntry(each[0], DEFAULT_WEIGHT);
			}
		}
	}
	
	/**
	 * Makes a list containing Fusion, Synchro and Xyz, as well as a bag containing
	 * Fusion, Synchro, Xyz and Rituals.
	 */
	private void makeExtraDeckAndRitualsLists() {
		makeExtraDeckList();
		
		extraDeckAndRitualCards.concatenate(extraDeckCards);
		extraDeckAndRitualCards.concatenate(ritualCards);
	}
	
	private void makeExtraDeckList() {
		extraDeckCards.concatenate(fusionCards); // this should make the bags share references, which is desired
		extraDeckCards.concatenate(synchroCards);
		extraDeckCards.concatenate(xyzCards);
	}
	
	/**
	 * Makes a bag containing Spell and Traps, as well as a bag containing Spell,
	 * Traps, Monsters and Tuners.
	 */
	private void makeMainDeckLists() {
		spellAndTrapCards.concatenate(spellCards);
		spellAndTrapCards.concatenate(trapCards);
		
		mainDeckCards.concatenate(spellAndTrapCards);
		mainDeckCards.concatenate(monsterCards);
		mainDeckCards.concatenate(tunerCards);
	}
	
}
