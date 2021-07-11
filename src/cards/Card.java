package cards;

import java.awt.Image;

public abstract class Card {
	protected String name;
	protected String color; // monster, spell, trap
	protected boolean isExtraDeck;
	protected String passcode; // the unique number associated with the card, used for deckbuilding in YGO pro
	protected String desc; // the card description / effect / flavor text
	protected Image cardImage;
	
	/**
	 * The formatted name is the string needed to complete this card's url
	 */
	protected String formattedName; // the name of the card that completes the url
	
	public String getName() { return name; }
	
	public String getColor() { return color; }
	
	public abstract boolean isExtraDeck();
	
	public String getPasscode() { return passcode; }
	
	public String getText() { return desc; }
	
	public abstract String[] toArray();
	
	public String getFormattedName() { return formattedName; }
	
	public Image getImage() { return this.cardImage; }
	
	public abstract String getOneLineInfo();
	
	public abstract String getBetterDesc();
	
	public void print() {
		for(String i : toArray()) {
			System.out.println(i);
		}
	}
}
