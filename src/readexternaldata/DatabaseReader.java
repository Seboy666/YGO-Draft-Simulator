package readexternaldata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
	private List<String[]> namesAndColorsDB; // cards and their color (spell, trap, monster, ritual, fusion, etc.)
	private List<String> cardNames; // just card names
	
	private List<String> fusionCards; // lists for single colors
	private List<String> monsterCards;
	private List<String> ritualCards;
	private List<String> spellCards;
	private List<String> synchroCards;
	private List<String> trapCards;
	private List<String> xyzCards;
	
	private List<String> extraDeckAndRitualCards; // Fusion, synchros, xyz and rituals
	private List<String> extraDeckCards; // Fusion, synchros and xyz
	private List<String> mainDeckCards; // spell, traps and monsters, NO RITUALS
	private List<String> spellAndTrapCards; // just spell and traps
	
	public DatabaseReader() {
		namesAndColorsDB = makeDatabaseFromTxt(); // create entire DB
		
		this.cardNames = new ArrayList<String>(); // initialize all member variables
		this.fusionCards = new ArrayList<String>();
		this.monsterCards = new ArrayList<String>();
		this.ritualCards = new ArrayList<String>();
		this.spellCards = new ArrayList<String>();
		this.synchroCards = new ArrayList<String>();
		this.trapCards = new ArrayList<String>();
		this.xyzCards = new ArrayList<String>();
		
		extraDeckAndRitualCards = new ArrayList<String>();
		extraDeckCards = new ArrayList<String>();
		mainDeckCards = new ArrayList<String>();
		spellAndTrapCards = new ArrayList<String>();
		
		makeCardNamesDB(); // fill all lists
		makeListOfThisColor("Fusion", fusionCards);
		makeListOfThisColor("Monster", monsterCards);
		makeListOfThisColor("Ritual", ritualCards);
		makeListOfThisColor("Spell", spellCards);
		makeListOfThisColor("Synchro", synchroCards);
		makeListOfThisColor("Trap", trapCards);
		makeListOfThisColor("Xyz", xyzCards);
		
		makeExtraDeckAndRitualsLists();
		makeMainDeckLists();
	}
	
	
	public List<String[]> getFullDB() { return namesAndColorsDB; }
	
	public List<String> getCardNames() { return cardNames; }
	
	public List<String> getFusionCardNames() { return fusionCards; }
	public List<String> getMonsterCardNames() { return monsterCards; }
	public List<String> getRitualCardNames() { return ritualCards; }
	public List<String> getSpellCardNames() { return spellCards; }
	public List<String> getSynchroCardNames() { return synchroCards; }
	public List<String> getTrapCardNames() { return trapCards; }
	public List<String> getXyzCardNames() { return xyzCards; }
	
	public List<String> getExtraAndRitualCardNames() { return extraDeckAndRitualCards; }
	public List<String> getExtraDeckCardNames() { return extraDeckCards; }
	public List<String> getMainDeckCardNames() { return mainDeckCards; }
	public List<String> getSpellAndTrapCardNames() { return spellAndTrapCards; }
	
	
	
	
	private void makeCardNamesDB() {
		for(int i = 0; i < namesAndColorsDB.size(); i++) {
			cardNames.add(namesAndColorsDB.get(i)[0]);
		}
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
	
	private void makeListOfThisColor(String color, List<String> list) {
		for(String[] each : namesAndColorsDB) {
			if(each[1].equals(color)) {
				list.add(each[0]);
			}
		}
	}
	
	/*
	 * Makes two lists, one that is the same as the other, but with rituals added in
	 */
	private void makeExtraDeckAndRitualsLists() {
		for(String[] each : namesAndColorsDB) {
			if(each[1].equals("Fusion") || each[1].equals("Synchro") || each[1].equals("Xyz")) {
				extraDeckAndRitualCards.add(each[0]);
				extraDeckCards.add(each[0]);
			}
			else if(each[1].equals("Ritual")) {
				extraDeckAndRitualCards.add(each[0]);
			}
		}
	}
	
	private void makeExtraDeckList() {
		for(String[] each : namesAndColorsDB) {
			if(each[1].equals("Fusion") || each[1].equals("Synchro") || each[1].equals("Xyz")) {
				extraDeckCards.add(each[0]);
			}
		}
	}
	
	/*
	 * Makes two lists, one that is the same as the other, but with monsters added in
	 */
	private void makeMainDeckLists() {
		for(String[] each : namesAndColorsDB) {
			if(each[1].equals("Spell") || each[1].equals("Trap")) {
				mainDeckCards.add(each[0]);
				spellAndTrapCards.add(each[0]);
			}
			else if(each[1].equals("Monster")) {
				mainDeckCards.add(each[0]);
			}
		}
	}
	
}
