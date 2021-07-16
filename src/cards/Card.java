package cards;

import java.awt.Image;
import java.util.List;

public abstract class Card {
	protected String name;
	protected String color; // monster, spell, trap
	protected boolean isExtraDeck;
	protected String passcode; // the unique number associated with the card, used for deckbuilding in YGO pro
	protected String desc; // the card description / effect / flavor text
	protected Image cardImage;
	protected List<String> related;
	
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
	
	/**
	 * Gives all formatted card names related to this one. Used to increase those cards'
	 * probability of being pulled.
	 * 
	 * @return A list of all related cards, as formatted card names.
	 */
	public List<String> getRelatedCardNames() { return related; }
	
	public abstract String getOneLineInfo();
	
	public abstract String getBetterDesc();
	
	public void print() {
		for(String i : toArray()) {
			System.out.println(i);
		}
	}
}
