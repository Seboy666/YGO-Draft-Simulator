package readexternaldata;
import javax.swing.*;
import java.awt.GridLayout;


public class CardDisplay {
	public String title;
	
	CardDisplay(String info) {
		this.title = info;
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void BuildPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2, 5, 0));
		
		JPanel cardImagepanel = new JPanel();
		panel.add(cardImagepanel);
		
		JLabel lblImageGoesHere = new JLabel("Image goes here");
		cardImagepanel.add(lblImageGoesHere);
		
		JPanel infoPanel = new JPanel();
		panel.add(infoPanel);
		infoPanel.setLayout(new GridLayout(7, 2, 0, 5));
		
		JLabel lblCardName = new JLabel("Name");
		infoPanel.add(lblCardName);
		
		JLabel lblType = new JLabel("Color");
		infoPanel.add(lblType);
		
		JLabel lblAttribute = new JLabel("Attribute");
		infoPanel.add(lblAttribute);
		
		JLabel lblTypes = new JLabel("Types");
		infoPanel.add(lblTypes);
		
		JLabel lblLevel = new JLabel("Level");
		infoPanel.add(lblLevel);
		
		JLabel lblAtkDef = new JLabel("ATK / DEF");
		infoPanel.add(lblAtkDef);
		
		
		
	}

}
