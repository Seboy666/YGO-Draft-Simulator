package cards;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Card_Spell extends Card {
	private String property; // quick-play, field, equip, etc
	
	public Card_Spell(String name, String color, String property, String passcode, String desc, String frmtdName, Image image, List<String> related){
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
	
	public Card_Spell(String name, String color, String property, String passcode, String desc, String frmtdName, Image image){
		this(name, color, property, passcode, desc, frmtdName, image, new ArrayList<String>());
	}
	
	@Override
	public boolean isExtraDeck() { return isExtraDeck; }
	
	public String getProperty() { return property; }
	
	@Override
	public String getOneLineInfo() {
		return name + " - " + property + " Spell";
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
