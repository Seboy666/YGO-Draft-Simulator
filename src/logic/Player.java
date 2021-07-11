package logic;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import cards.Card;

public class Player {
	private String username;
	private int id;
	private List<Card> cardList;
	private DefaultListModel<String> listModel;
	
	public Player(String username, int id) {
		this.username = username;
		this.id = id;
		cardList = new ArrayList<>();
		listModel = new DefaultListModel<String>();
	}
	
	public Player(String username, int id, List<Card> cardList) {
		this.username = username;
		this.id = id;
		this.cardList = cardList;
		listModel = new DefaultListModel<String>();
	}
	
	public String getUsername() { return username; }
	
	public int getID() { return id; }
	
	public List<Card> getCardList() { return cardList; }
	
	public Card getCardAt(int index) { return cardList.get(index); }
	
	public void removeCardAt(int index) { 
		cardList.remove(index);
		listModel.remove(index);
	}
	
	public void addCard(Card card) { 
		cardList.add(card); 
		listModel.addElement(cardList.size() + " - " + card.getName() + " ");
	}
	
	public DefaultListModel<String> getListModel() { return listModel; }
	
	public String extractDeckAsYGOProTxtString() {
		String newLine = "\n";
		String main = "#created by ..." + newLine + "#main" + newLine;
		String extra = "#extra" + newLine;
		String side = "!side" + newLine;
		
		// build extra deck string stream
		int extra_deck_size = 0;
		for(Card each : cardList) {
			if(each.isExtraDeck()) {
				extra = extra + each.getPasscode() + newLine;
				extra_deck_size++;
			}
		}
		
		// build side deck string stream
		int main_deck_size = cardList.size() - extra_deck_size;
		int side_deck_size = main_deck_size - 60;
		
		if(side_deck_size < 0) { // if main deck is smaller than 60 cards, do nothing
			side_deck_size = 0;
		}
		else { // otherwise make it 60 cards and add leftover cards to side deck
			main_deck_size = 60;
			for(Card each : cardList) {
				if(side_deck_size <= 0) {
					break;
				}
				if(!each.isExtraDeck()) {
					side = side + each.getPasscode() + newLine;
					side_deck_size--;
				}
			}
		}
		// build main deck string stream
		for(Card each : cardList) {
			if(main_deck_size <= 0) {
				break;
			}
			if(!each.isExtraDeck()) {
				main = main + each.getPasscode() + newLine;
				main_deck_size--;
			}
		}
		
		return main + extra + side;
	}
}
