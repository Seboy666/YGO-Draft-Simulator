package cards;

public class RelatedCard {
	
	private String name;
	private String formattedName;
	private int number;
	private String category; // monster, ritual, extra, spell
	
	public RelatedCard(String name, String formattedName, int number, String category) {
		this.name = name;
		this.formattedName = formattedName;
		this.number = number;
		this.category = category;
	}
	
	public RelatedCard(String name, String formattedName, String category) {
		this(name, formattedName, 1, category);
	}
	
	public RelatedCard(String name, String formattedName, int number) {
		this(name, formattedName, number, "Monster");
	}
	
	public RelatedCard(String name, String formattedName) {
		this(name, formattedName, 1, "Monster");
	}
	
	public String getFormattedName() { return formattedName; }
	public String getName() { return name; }
	public int getNumber() { return number; }
	public String getCategory() { return category; }
	
	
	/**
	 * Decreases number by one, turns negative values into 0;
	 * 
	 * @return FALSE when the number is equal to 0.
	 */
	public boolean decrementNumber() {
		number--;
		if(number > 0) {
			return true;
		}
		else {
			number = 0;
			return false;
		}
	}
	
	public void incrementNumber() { number ++; }
	
}
