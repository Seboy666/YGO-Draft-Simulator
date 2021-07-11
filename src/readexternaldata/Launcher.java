package readexternaldata;

import javax.swing.SwingUtilities;

import gui.*;
import logic.*;
import networking.*;

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
