package cards;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Card_Monster extends Card {
	private String attribute; // light, dark, fire, etc.
	private String types; // Machine/effect, Fiend, Warrior/fusion/effect, etc.
	private String level;
	private String atk;
	private String def;
	
	public Card_Monster(String name, String color, boolean isExtraDeck, String att, String types, String level,
			String atk, String def, String passcode, String desc, String frmtdName, Image image, List<String> related) {
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
		this.related = related;
	}
	
	public Card_Monster(String name, String color, boolean isExtraDeck, String att, String types, String level,
			String atk, String def, String passcode, String desc, String frmtdName, Image image) {
		this(name, color, isExtraDeck, att, types, level, atk, def, passcode, desc, frmtdName, image, new ArrayList<String>());
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
