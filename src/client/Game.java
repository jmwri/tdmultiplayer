package client;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.*;

import factory.AbstractFactory;
import factory.CharacterFactory;
import factory.Factory;
import factory.PlayerFactory;
import factory.ProjectileFactory;
import factory.WeaponFactory;

import net.Listener;

@SuppressWarnings("serial")
public class Game extends JPanel {
	
	//Client details
	private int screenWidth;
	private int screenHeight;
	private Character screenFocus;
	
	//Game
	private int screenOffsetX;
	private int screenOffsetY;
	private Map map;
	private Player player;
	private ArrayList<Character> characters = new ArrayList<Character>();
	private ArrayList<Character> charactersRemove = new ArrayList<Character>();
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList<Entity> entitiesRemove = new ArrayList<Entity>();
	private boolean paused;
	
	//Factories
	private WeaponFactory weaponFactory;
	private CharacterFactory characterFactory;
	private PlayerFactory playerFactory;
	private ProjectileFactory projectileFactory;

	//Main
	private Main main;
	
	//Server stuff
	private InetAddress serverIP;
	private DatagramSocket clientSocket;
	private Listener listener;
	private int port = 5000;
	private String splitter = ":split:";
	private long lastHeard;
	private long lastSent;
	
	public Game(String ip, int screenWidth, int screenHeight, Main m) {
		try {
			this.setClientSocket(new DatagramSocket());
		} catch (SocketException e) {
			System.out.println("Port already in use");
		}
		
		this.setListener(new Listener("Client", this.getClientSocket(), this));
		new Thread(this.getListener()).start();
		
		this.setMain(m);
		this.setBackground(Color.BLACK);
		try {
			this.setServerIp(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.setScreenWidth(screenWidth);
		this.setScreenHeight(screenHeight);
		this.setBounds(0, 0, screenWidth, screenHeight);
		this.setSize(screenWidth, screenHeight);
		this.setMap(new Map("Default", this));
		
		AbstractFactory af = new AbstractFactory();
		this.setWeaponFactory((WeaponFactory) af.getFactory("weapon", this.getMain()));
		this.setCharacterFactory((CharacterFactory) af.getFactory("character", this.getMain()));
		this.setPlayerFactory((PlayerFactory) af.getFactory("player", this.getMain()));
		this.setProjectileFactory((ProjectileFactory) af.getFactory("projectile", this.getMain()));
	}
	
	public void start(String playerName) {
		this.setLastHeard(System.currentTimeMillis());
		Player p = this.getPlayerFactory().generate(playerName);
		p.spawn();
		this.setPlayer(p);
		this.getCharacters().add(p);
		this.sendPacket(01, this.getPlayer().getName() + this.splitter + this.getPlayer().getX() + this.splitter + this.getPlayer().getY() + this.splitter + this.getPlayer().getEquippedWeaponId() + this.splitter + this.getPlayer().getHealth() + this.splitter + this.getPlayer().getRotation());
		
		this.setScreenFocus(this.getPlayer());
	}
	
	public void tick() {
		if(this.getLastHeard() < (System.currentTimeMillis() - 10000)) {
			JOptionPane.showMessageDialog(null, "Cannot connect to server.");
			System.exit(0);
		}
		
		if(!this.isPaused()) {
			double sfx = this.getScreenFocus().getX();
			double sfy = this.getScreenFocus().getY();
			
			this.setScreenOffsetX((int)sfx - (this.getScreenWidth() / 2));
			this.setScreenOffsetY((int)sfy - (this.getScreenHeight() / 2));
			
			if(this.getMain().getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) {
				this.pauseGame();
			}
			
			for(Character c : this.charactersRemove) {
				this.getCharacters().remove(c);
			}
			this.getCharactersRemove().clear();
			
			for(Entity e : this.entitiesRemove) {
				this.getEntities().remove(e);
			}
			this.getEntitiesRemove().clear();
			
			this.getMap().tick();
			Point mPos = this.getMain().getMousePosition();
			
			if(mPos != null) {
				this.getPlayer().setMousePosition(mPos.getX() + this.getScreenOffsetX(), mPos.getY() + this.getScreenOffsetY());
			}
			for(Character c : this.getCharacters()) {
				if(c.isInWorld()) c.tick();
			}
			for(Entity e : this.getEntities()) {
				if(e.isInWorld()) e.tick();
			}
			
			if(sfx != 0 && sfy != 0) this.getMain().openMenu(-1);
			
			if(this.isPaused()) {
				this.getMain().openMenu(3);
			}
			
			if(this.getLastSent() < (System.currentTimeMillis() - 500)) {
				this.sendPacket(07, ""+this.getPlayer().getId());
				this.setLastSent(System.currentTimeMillis());
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(!this.isPaused()) {
			super.paintComponent(g);
			this.getMap().render(g, this.getScreenOffsetX(), this.getScreenOffsetY(), this.getScreenWidth(), this.getScreenHeight());
			for(Character c : this.getCharacters()) {
				c.render(g, this.getScreenOffsetX(), this.getScreenOffsetY());
			}
			for(Entity e : this.getEntities()) {
				e.render(g, this.getScreenOffsetX(), this.getScreenOffsetY());
			}
			
			this.drawHud(g);
		}
    }
	
	public void drawHud(Graphics g) {
		Color bg = new Color(138, 138, 138, 235);
		Character c = this.getScreenFocus();
		if(c != null) {
			//Player health
			g.setColor(Color.YELLOW);
			int hp = c.getHealth();
			if(hp <= 50) g.setColor(Color.ORANGE);
			if(hp <= 30) g.setColor(Color.RED);
			g.setFont(new Font("Helvetica", Font.PLAIN, 30));
			g.drawString("Health: " + hp, 10, this.getHeight() - 10);
			
			//Weapon stats
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Helvetica", Font.PLAIN, 30));
			String curAmmo = c.getWeaponAmmo();
			int emptySpaces = 6 - curAmmo.length();
			g.drawString(curAmmo, (this.getWidth() - 100) + emptySpaces * 17, this.getHeight() - 10);
			
			g.setFont(new Font("Helvetica", Font.PLAIN, 20));
			
			if(c.isWeaponReloading()) {
				g.setColor(bg);
				int reloadBoxWidth = 200;
				int reloadBoxHeight = 40;
				int reloadBoxX = (this.getWidth()/2) - (reloadBoxWidth/2);
				int reloadBoxY = (this.getHeight()/2) - (reloadBoxHeight/4);
				g.fillRect(reloadBoxX, reloadBoxY, reloadBoxWidth, reloadBoxHeight);
				
				g.setColor(Color.WHITE);
				int pbarwidth = (int) (((reloadBoxWidth) / 100) * c.getWeaponReloadPercentage());
				g.fillRect(reloadBoxX, reloadBoxY + 30, pbarwidth, 10);
				
				g.setColor(Color.BLACK);
				g.setFont(new Font("Helvetica", Font.BOLD, 14));
				g.drawString("Reloading", reloadBoxX + 62, reloadBoxY + 20);
			}
		}
	}
	
	public void sendPacket(int type, String data) {
		byte[] sendData = new byte[1024];
		data = type + this.splitter + data;
		
		if(type < 10) {
			data = "0" + data;
		}
		
		sendData = (data).getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.getServerIp(), this.getPort());
		
		try {
			this.clientSocket.send(sendPacket);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void dataReceived(String[] data, InetAddress ip, int port) {
		int packetType = Integer.parseInt(data[0]);
		switch(packetType) {
			//Give user id
			case 01:
				this.getPlayer().setId((int) Double.parseDouble(data[1]));
				this.getPlayer().setX((int) Double.parseDouble(data[2]));
				this.getPlayer().setY((int) Double.parseDouble(data[3]));
				this.getPlayer().setLoaded(true);
				break;
				
			//Another player moved
			case 02:
				int id = (int)Double.parseDouble(data[1]);
				double x = Double.parseDouble(data[2]);
				double y = Double.parseDouble(data[3]);
				for(Character p : this.getCharacters()) {
					if(p.getId() == id) {	
						p.setX(x);
						p.setY(y);
					}
				}
				break;
			
			//Another player joined the server
			case 03:
				id = (int)Double.parseDouble(data[1]);
				String name = data[2];
				x = Double.parseDouble(data[3]);
				y = Double.parseDouble(data[4]);
				int weaponid = (int)Double.parseDouble(data[5]);
				int health = (int)Double.parseDouble(data[6]);
				double rotation = Double.parseDouble(data[7]);
				
				Character c = this.getCharacterFactory().generate(name);
				c.setId(id);
				c.spawn((int)x, (int)y);
				c.addWeapon(this.getWeaponFactory().generate(1), 1);
				c.equip(1);
				c.setRotation(rotation);
				this.getCharacters().add(c);
				System.out.println("Player joined the server. ID: " + c.getId() + ", X: " + x + ", Y: " + y);
				break;
			
			//Another player rotated
			case 04:
				id = (int)Double.parseDouble(data[1]);
				rotation = Double.parseDouble(data[2]);
				for(Character p : this.getCharacters()) {
					if(p.getId() == id) {
						p.setRotation(rotation);
					}
				}
				break;
			
			//Another player fired
			case 05:
				id = (int)Double.parseDouble(data[1]);
				int ownerId = (int)Double.parseDouble(data[2]);
				x = (int)Double.parseDouble(data[3]);
				y = (int)Double.parseDouble(data[4]);
				rotation = (int)Double.parseDouble(data[5]);
				
				Character owner = null;
				for(Character ch : this.getCharacters()) {
					if(ch.getId() == ownerId) {
						owner = ch;
						break;
					}
				}
				
				Projectile p = this.createProjectile(id, owner, (int)x, (int)y, (int)rotation);
				break;
			
			//A players health changed
			case 06:
				id = (int)Double.parseDouble(data[1]);
				health = (int)Double.parseDouble(data[2]);
				int attacker = (int)Double.parseDouble(data[3]);
				weaponid = (int)Double.parseDouble(data[4]);
				
				for(Character ch : this.getCharacters()) {
					if(ch.getId() == id) {
						ch.setHealth(health);
						break;
					}
				}
				break;
			
			//A player spawned
			case 07:
				id = (int)Double.parseDouble(data[1]);
				x = (int)Double.parseDouble(data[2]);
				y = (int)Double.parseDouble(data[3]);
				health = (int)Double.parseDouble(data[4]);
				
				for(Character ch : this.getCharacters()) {
					if(ch.getId() == id) {
						ch.respawn((int)x, (int)y);
						break;
					}
				}
				break;
			
			//A player dc'd
			case 10:
				id = (int)Double.parseDouble(data[1]);
				
				for(Character ch : this.getCharacters()) {
					if(ch.getId() == id) {
						this.charactersRemove.add(ch);
						break;
					}
				}
				break;
			
			//Still connected
			case 11:
				this.lastHeard = System.currentTimeMillis();
				break;
		}
	}
	
	public Projectile createProjectile(int id, Character owner, int x, int y, int rotation) {
		Projectile p = this.getProjectileFactory().generate(id, owner, x, y, rotation);
		this.getEntities().add(p);
		
		return p;
	}
	
	public void addEntity(Entity e) {
		this.getEntities().add(e);
	}
	
	public void removeEntity(Entity e) {
		this.getEntitiesRemove().add(e);
	}
	
	public int getScreenWidth() {
		return this.screenWidth;
	}
	
	public int getScreenHeight() {
		return this.screenHeight;
	}
	
	public int getScreenOffsetX() {
		return this.screenOffsetX;
	}
	
	public void setScreenOffsetX(int offset) {
		this.screenOffsetX = offset;
	}
	
	public int getScreenOffsetY() {
		return this.screenOffsetY;
	}
	
	public void setScreenOffsetY(int offset) {
		this.screenOffsetY = offset;
	}
	
	public void setPlayer(final Player p) {
		this.player = p;
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
		    	p.setMouseClicked(true);
		    }
			
			public void mouseReleased(MouseEvent e) {
		    	p.setMouseClicked(false);
		    }
		});
	}
	
	public Map getMap() {
		return this.map;
	}

	public InetAddress getServerIp() {
		return this.serverIP;
	}

	public void setServerIp(InetAddress ip) {
		this.serverIP = ip;
	}

	public Character getScreenFocus() {
		return screenFocus;
	}

	public void setScreenFocus(Character screenFocus) {
		this.screenFocus = screenFocus;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
	}

	public ArrayList<Weapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(ArrayList<Weapon> weapons) {
		this.weapons = weapons;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}

	public ArrayList<Entity> getEntitiesRemove() {
		return entitiesRemove;
	}

	public void setEntitiesRemove(ArrayList<Entity> entitiesRemove) {
		this.entitiesRemove = entitiesRemove;
	}

	public Player getPlayer() {
		return player;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public boolean isPaused() {
		return paused;
	}

	public void pauseGame() {
		if(!this.isPaused()) {
			this.paused = true;
		}
	}
	
	public void resumeGame() {
		if(this.isPaused()) {
			this.paused = false;
			this.getMain().openMenu(-1);
			System.out.println("resume");
		}
	}
	
	public void setMain(Main m) {
		this.main = m;
	}
	
	public Main getMain() {
		return this.main;
	}

	public DatagramSocket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(DatagramSocket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public long getLastHeard() {
		return lastHeard;
	}

	public void setLastHeard(long lastHeard) {
		this.lastHeard = lastHeard;
	}

	public long getLastSent() {
		return lastSent;
	}

	public void setLastSent(long lastSent) {
		this.lastSent = lastSent;
	}

	public ArrayList<Character> getCharactersRemove() {
		return charactersRemove;
	}

	public void setCharactersRemove(ArrayList<Character> charactersRemove) {
		this.charactersRemove = charactersRemove;
	}
	
	public WeaponFactory getWeaponFactory() {
		return weaponFactory;
	}

	public void setWeaponFactory(WeaponFactory factory) {
		this.weaponFactory = factory;
	}

	public CharacterFactory getCharacterFactory() {
		return characterFactory;
	}

	public void setCharacterFactory(CharacterFactory factory) {
		this.characterFactory = factory;
	}
	
	public PlayerFactory getPlayerFactory() {
		return playerFactory;
	}

	public void setPlayerFactory(PlayerFactory factory) {
		this.playerFactory = factory;
	}

	public void setProjectileFactory(ProjectileFactory projectileFactory) {
		this.projectileFactory = projectileFactory;
	}

	public ProjectileFactory getProjectileFactory() {
		return projectileFactory;
	}

	public InetAddress getServerIP() {
		return serverIP;
	}

	public void setServerIP(InetAddress serverIP) {
		this.serverIP = serverIP;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
}
