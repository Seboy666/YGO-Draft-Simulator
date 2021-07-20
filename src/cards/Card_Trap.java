package cards;

import java.awt.Image;
import java.util.HashSet;
import java.util.Set;

public class Card_Trap extends Card{
	private String property; // continuous, counter, etc.
	
	public Card_Trap(String name, String color, String property, String passcode, String desc, String frmtdName, Image image, Set<RelatedCard> related){
		this.name = name;
		this.color = color;
		this.isExtraDeck = false;
		this.property = property;
		this.passcode = passcode;
		this.desc = desc;
		this.formattedName = frmtdName;
		this.cardImage = image;
		this.related = related;
	}
	
	public Card_Trap(String name, String color, String property, String passcode, String desc, String frmtdName, Image image) {
		this(name, color, property, passcode, desc, frmtdName, image, new HashSet<RelatedCard>());
	}
	
	@Override
	public boolean isExtraDeck() { return isExtraDeck; }
	
	@Override
	public String getCategory() { return property + " " + color; }
	
	public String getProperty() { return property; }
	
	@Override
	public String getOneLineInfo() {
		return name + " - " + property + " Trap";
	}
	
	@Override
	public String getBetterDesc() {
		return desc;
	}
	
	@Override
	public String[] toArray() {
		return new String[] {name, color, property, passcode, desc, formattedName};
	}
}
