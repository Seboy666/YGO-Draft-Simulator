package readexternaldata;

import javax.swing.SwingUtilities;
import logic.WeightedRandomBag;
import gui.*;

public class Launcher {
	
	public static void main(String[] args) {
		
		
		FirstGUI mainMenu = new FirstGUI();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				mainMenu.createGUI();
			}
		});
		
	}
}
