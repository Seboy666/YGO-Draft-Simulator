package cards;

import java.awt.Image;

public class Card_Spell extends Card {
	private String property; // quick-play, field, equip, etc
	
	public Card_Spell(String name, String color, String property, String passcode, String desc, String frmtdName, Image image){
		this.name = name;
		this.color = color;
		this.isExtraDeck = false;
		this.property = property;
		this.passcode = passcode;
		this.desc = desc;
		this.formattedName = frmtdName;
		this.cardImage = image;
	}
	
	public Card_Spell(String[] array, Image image) {
		this.name = array[0];
		this.color = array[1];
		this.property = array[2];
		this.passcode = array[3];
		this.desc = array[4];
		this.formattedName = array[5];
		this.cardImage = image;
		this.isExtraDeck = false;
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
