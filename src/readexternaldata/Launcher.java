package readexternaldata;

import javax.swing.SwingUtilities;
import logic.WeightedRandomBag;
import gui.*;

public class Launcher {
	
	public static void main(String[] args) {
		
		WeightedRandomBag<String> test = new WeightedRandomBag<String>();
		for(int i = 0; i <= 15; i++) {
			test.addEntry("test" + i, 1.0d);
		}
		WeightedRandomBag<String> practice = new WeightedRandomBag<String>();
		for(int i = 0; i <= 15; i++) {
			practice.addEntry("practice" + i, 1.0d);
		}
		WeightedRandomBag<String> total = new WeightedRandomBag<String>();
		total.concatenate(test);
		total.concatenate(practice);
		
		total.modifyWeight("practice12", 30.0d);
		practice.recalcTotalWeight();
		System.out.println(practice.getRandom());
		System.out.println(practice.getRandom());
		System.out.println(practice.getRandom());
		
		/*
		FirstGUI mainMenu = new FirstGUI();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				mainMenu.createGUI();
			}
		});*/
		
	}
}
