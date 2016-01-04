package client;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Menu extends JPanel {
	
	private String name;
	private String header;
	private int width;
	private int height;
	private Main owner;
	private ArrayList<JTextField> textFields = new ArrayList<JTextField>();
	private ArrayList<JComboBox> comboBoxes = new ArrayList<JComboBox>();
	
	public Menu(Main owner, String name, String title, int width, int height) {
		super();
		this.setOwner(owner);
		this.setName(name);
		this.setHeader(title);
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.setBackground(Color.WHITE);
		this.setBounds(0, 0, width, height);
		this.setWidth(width);
		this.setHeight(height);
		
		JLabel header = new JLabel(this.header);
		header.setFont(new Font("Serif", Font.BOLD, 30));
		header.setBorder(new EmptyBorder(0, 0, 20, 0));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		this.add(header, c);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Main getOwner() {
		return owner;
	}

	public void setOwner(Main owner) {
		this.owner = owner;
	}

	public ArrayList<JTextField> getTextFields() {
		return textFields;
	}

	public void setTextFields(ArrayList<JTextField> textFields) {
		this.textFields = textFields;
	}

	public ArrayList<JComboBox> getComboBoxes() {
		return comboBoxes;
	}

	public void setComboBoxes(ArrayList<JComboBox> comboBoxes) {
		this.comboBoxes = comboBoxes;
	}

	public void addLabel(String name, int x, int y, int width, int height) {
		JLabel option = new JLabel(name);
		option.setFont(new Font("Serif", Font.PLAIN, 20));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		this.add(option, c);
	}
	
	public void addOption(final String name, final int code, int x, int y, int width, int height) {
		JLabel option = new JLabel(name);
		option.setFont(new Font("Serif", Font.PLAIN, 20));
		option.addMouseListener(new MouseAdapter() {
		    public void mouseReleased(MouseEvent e) {
		        System.out.println("Clicked on " + name);
		        doAction(code);
		    }
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		this.add(option, c);
	}
	
	public void addTextField(final String name, int x, int y, int width, int height) {
		JTextField option = new JTextField(20);
		option.setFont(new Font("Serif", Font.PLAIN, 20));
		
		this.textFields.add(option);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		
		this.add(option, c);
		option.getText();
	}
	
	public void addComboBox(String[] options, int x, int y, int width, int height) {
		JComboBox combo = new JComboBox(options);
		combo.setFont(new Font("Serif", Font.PLAIN, 20));
		
		this.getComboBoxes().add(combo);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		this.add(combo, c);
	}
	
	public void doAction(int code) {
		switch(code) {
			case 0: //Do nothing
				break;
				
			case 1: //Close the menu
				this.getOwner().openMenu(-1);
				break;
			
			case 2: //Close the program
				System.exit(0);
				break;
			
			case 3: //Open the join server menu
				this.getOwner().openMenu(2);
				break;
			
			case 4: //Open the host server menu
				this.getOwner().openMenu(1);
				break;
			
			case 5: //Open the main menu
				this.getOwner().openMenu(0);
				break;
			
			case 6: //Start the server
				this.doAction(1);
				
				this.getOwner().startServer(this.getTextFields().get(0).getText(), (String) this.getComboBoxes().get(0).getSelectedItem());
				break;
			
			case 7: //Join a server
				this.doAction(1);
				
				this.getOwner().joinServer(this.getTextFields().get(0).getText(), this.getTextFields().get(1).getText());
				break;
			
			case 8: //Quit the game
				this.doAction(5);
				this.getOwner().leaveGame();
				break;
			
			case 9: //Resume the game
				this.getOwner().getGame().resumeGame();
				break;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
    }
	
}
