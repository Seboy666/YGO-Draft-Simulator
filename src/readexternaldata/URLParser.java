package readexternaldata;
import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import cards.*;


public class URLParser {
	
	private static final String WIKI_URL_START = "https://yugioh.fandom.com/wiki/";
	
	public static Card parseCardNameToCard(String formattedCardName) {
		String name;
		String color; // monster, trap, spell
		boolean isExtraDeck;
		String property; // for spell & traps, if they are counter, field, quickplay, etc.
		String attribute; // light, dark, fire, etc.
		String types; // Machine/effect, Fiend, Warrior/fusion/effect, etc.
		String level;
		String atk;
		String def;
		String passcode;
		String desc; // the card description / text
		Image image;
		Card cardToReturn;
		
		Document doc; // the html document of the webpage
		
		try {
			doc = Jsoup.connect(WIKI_URL_START + formattedCardName).get();
			Elements cardtablerow = doc.getElementsByClass("cardtablerow");
			name = cardtablerow.select("th.cardtablerowheader:matches(English) + td.cardtablerowdata").text(); // get the card name
			color = cardtablerow.select("th.cardtablerowheader:matches(Card type) + td.cardtablerowdata").text(); // if the card is a spell / trap / monster
			passcode = cardtablerow.select("th.cardtablerowheader:matches(Passcode) + td.cardtablerowdata").text(); // get the card unique ID / passcode
			desc = doc.getElementsByClass("navbox-list").eachText().get(0); // get card description in english
			
			Elements imgElemnt = doc.getElementsByClass("cardtable-cardimage");
			try {
				imgElemnt = imgElemnt.select("img[src]");
				URL imageURL = new URL(imgElemnt.attr("src"));
				image = ImageIO.read(imageURL); // this image should be scaled to a width of 300, done by the website
			}
			catch(Exception ex) {
				image = null;
			}
			
			if(color.contentEquals("Monster")) { // if the card is a monster,
				attribute = cardtablerow.select("th.cardtablerowheader:matches(Attribute) + td.cardtablerowdata").text();
				types = cardtablerow.select("th.cardtablerowheader:matches(Type) + td.cardtablerowdata").text();
				if(types.contains("Fusion") || types.contains("Synchro") || types.contains("Xyz")) {
					isExtraDeck = true;
				}
				else {
					isExtraDeck = false;
				}
				level = cardtablerow.select("th.cardtablerowheader:matches(Level) + td.cardtablerowdata").text();
				atk = cardtablerow.select("th.cardtablerowheader:matches(ATK) + td.cardtablerowdata > a").first().text();
				def = cardtablerow.select("th.cardtablerowheader:matches(DEF) + td.cardtablerowdata > a").get(1).text();
				cardToReturn = new Card_Monster(name, color, isExtraDeck, attribute, types, level, atk, def, passcode, desc, formattedCardName, image);
			}
			else { // if the card is a spell or trap,
				property = cardtablerow.select("th.cardtablerowheader:matches(Property) + td.cardtablerowdata").text();
				if(color.contentEquals("Spell")) { // if the card is a spell
					cardToReturn = new Card_Spell(name, color, property, passcode, desc, formattedCardName, image);
				}
				else { // if the card is a trap
					cardToReturn = new Card_Trap(name, color, property, passcode, desc, formattedCardName, image);
				}
			}
			return cardToReturn;
		}
		catch(Exception e) {
			System.out.println(e);
			image = null;
			cardToReturn = new Card_Spell("INVALID CARD", "Spell", "OwO", "420", "You should not be seeing this, this is the bad card name : " + formattedCardName, formattedCardName, image);
			return cardToReturn;
		}
	}
}
