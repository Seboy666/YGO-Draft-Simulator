package cards;

import java.awt.Image;

public class Card_Monster extends Card {
	private String attribute; // light, dark, fire, etc.
	private String types; // Machine/effect, Fiend, Warrior/fusion/effect, etc.
	private String level;
	private String atk;
	private String def;

	private static final int NUM_OF_FIELDS_THAT_ARENT_TYPES = 9; // this.types can be multiple strings, we use this value to get the exact number of types in the array passed in the constructor
	private static final int START_INDEX_OF_TYPES = 3; // this.types is at this index in this.asArray
	
	public Card_Monster(String name, String color, boolean isExtraDeck, String att, String types, String level, String atk, String def, String passcode, String desc, String frmtdName, Image image) {
		this.name = name;
		this.color = color;
		this.isExtraDeck = isExtraDeck;
		this.attribute = att;
		this.types = types;
		this.level = level;
		this.atk = atk;
		this.def = def;
		this.passcode = passcode;
		this.desc = desc;
		this.formattedName = frmtdName;
		this.cardImage = image;
	}
	
	public Card_Monster(String[] array, Image image, boolean isExtraDeck) {
		this.name = array[0];
		this.color = array[1];
		this.attribute = array[2];
		
		this.level = array[array.length - 6];
		this.atk = array[array.length - 5];
		this.def = array[array.length - 4];
		this.passcode = array[array.length - 3];
		this.desc = array[array.length - 2];
		this.formattedName = array[array.length - 1];
		this.isExtraDeck = isExtraDeck;
		
		String buffer = "";
		int number_of_types = array.length - NUM_OF_FIELDS_THAT_ARENT_TYPES;
		
		// separates all types by " / "
		for(int i = 0; i < number_of_types; i++) { 
			buffer = buffer + array[START_INDEX_OF_TYPES + i];
			if(i < number_of_types - 1) { // don't add " / " at the end of the string
				buffer = buffer + " / ";
			}
		}
		this.types = buffer;
		
		this.cardImage = image;
	}
	
	public String getAttribute() { return attribute; }
	
	public String getTypes() { return types; }
	
	@Override
	public boolean isExtraDeck() { return isExtraDeck; }
	
	public String getLevel() { return level; }
	
	public String getAtk() { return atk; }
	
	public String getDef() { return def; }
	
	@Override
	public String getOneLineInfo() {
		return name + " - LVL " + level + " " + attribute;
	}
	
	@Override
	public String getBetterDesc() {
		return types + "\n" + desc + "\n\n" + atk + " / " + def;
	}
	
	@Override
	public String[] toArray() {
		return new String[]{name, color, attribute, types, level, atk, def, passcode, desc, formattedName};
	}
}
