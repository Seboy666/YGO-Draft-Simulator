package readexternaldata;

import javax.swing.SwingUtilities;
import gui.*;

public class Launcher {
	
	public static void main(String[] args) {
		
		FirstGUI mainMenu = new FirstGUI();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainMenu.createGUI();
			}
		});
		/*
		Card card;
		DatabaseReader db = new DatabaseReader();
		for(String each : db.monsterCardsNames) {
			card = URLParser.parseCardNameToCard(each);
			if(card.getCategory().contains("Tuner")) {
				System.out.println(card.getFormattedName());
			}
		}*/
	}
}
