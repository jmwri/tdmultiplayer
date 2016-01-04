package client;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

import server.Server;

@SuppressWarnings("serial")
public class Main extends JFrame {

	public static void main(String[] args) {
		Main m = new Main();
		m.initialise();		
		m.run();
		System.exit(0);
	}
	
	private int[] screenSize = {800, 600};
	private boolean isRunning;
	private Insets windowInsets;
	private Menu[] menus = new Menu[100];
	private Menu menu;
	private Server server;
	private Game game;
	private boolean gameExists;
	private JLayeredPane lp;
	
	//Input
	private InputHandler inputHandler;
	private MouseListener mouseListener;
	
	//Cursor
	private Cursor crosshair;
	private Cursor def = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	
	//Debug
	private JLabel debug;
	private int fps;
	private int ticks;
	
	//User info
	private InetAddress clientIp;
	private String playerName;
	
	public void initialise() {
		//Window stuff
		setLayout(new BorderLayout());
		this.setTitle("Team Deathmatch Multiplayer");
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setWindowInsets(this.getInsets());
		this.requestFocus();
		this.setInputHandler(new InputHandler(this)); //Add input handler for the window
		this.lp = new JLayeredPane();
		this.lp.setPreferredSize(new Dimension(this.getScreenSize()[0], this.getScreenSize()[1]));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getScreenSize()[0]/2, dim.height/2-this.getScreenSize()[1]/2);
		
		//Create menus and display main menu
		this.createMenus();
		this.openMenu(0);
		
		this.add(this.lp);
		
		//Debug
		this.debug = new JLabel();
		this.debug.setForeground(Color.GREEN);
		this.debug.setLocation(100, 100);
		this.debug.setBounds(0, 0, this.getScreenSize()[0], this.getScreenSize()[1]);
		this.lp.add(this.getDebug(), new Integer(102));
		
		//Custom cursor
		BufferedImageLoader imgLoader = new BufferedImageLoader();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		BufferedImage cur = null;
		try {
			cur = imgLoader.loadImage("crosshair.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setCrosshair(toolkit.createCustomCursor(cur, new Point(16,16), "Crosshair"));
		
		
		//Set user details
		this.setPlayerName("Guest");
		try {
			 this.setClientIp(InetAddress.getByName("localhost"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.setRunning(true);
		
		//Show the window
		this.setVisible(true);
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while(this.isRunning()) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while(delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(shouldRender) {
				frames++;
				render();
			}
			
			if(System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				this.setFps(frames);
				this.setTicks(ticks);
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick() {
		if(this.gameExists) {
			if(!this.getGame().isPaused()) this.getGame().tick();
		}
		this.getDebug().setText("FPS: " + this.getFps());
	}
	
	public void render() {
		this.pack();
		//this.revalidate();
		this.repaint();
	}
	
	public void startServer(String playerName, String map) {
		System.out.println("Name: " + playerName);
		System.out.println("Map: " + map);
		this.setServer(new Server(map));
		this.getServer().initialise();
		new Thread(this.getServer()).start();
		this.joinServer("localhost", playerName);
	}
	
	public void joinServer(String ip, String playerName) {
		this.openMenu(4);
		this.game = new Game(ip, this.screenSize[0], this.screenSize[1], this);
		this.game.start(playerName);
		this.game.setCursor(crosshair);
		this.lp.add(this.getGame(), new Integer(100));
		this.gameExists = true;
	}
	
	public void joinServer(InetAddress ip, String playerName) {
		this.joinServer(ip.toString(), playerName);
	}
	
	public void leaveGame() {
		this.game.getClientSocket().close();
		this.gameExists = false;
		this.remove(game);
		this.game = null;
	}
	
	public void createMenus() {
		//Main menu
		Menu menu = new Menu(this, "Main menu", "Team Deathmatch Multiplayer!", screenSize[0], screenSize[1]);
		menu.addOption("Join", 3, 0, 1, 1, 1);
		menu.addOption("Host", 4, 0, 2, 1, 1);
		menu.addOption("Exit", 2, 0, 3, 1, 1);
		this.menus[0] = menu;
		
		//Host a server
		menu = new Menu(this, "Server options", "Server Options", screenSize[0], screenSize[1]);
		menu.addLabel("Player name", 0, 1, 1, 1);
		menu.addTextField("Player name", 1, 1, 1, 1);
		
		menu.addLabel("Map", 0, 2, 1, 1);
		String[] options = {"Default"};
		menu.addComboBox(options, 1, 2, 1, 1);
		
		menu.addOption("Host", 6, 0, 3, 1, 1);
		menu.addOption("Back", 5, 0, 4, 1, 1);
		
		this.menus[1] = menu;
		
		//Join a server
		menu = new Menu(this, "Join a server", "Join A Server", screenSize[0], screenSize[1]);
		menu.addLabel("Server IP", 0, 1, 1, 1);
		menu.addTextField("Server IP", 1, 1, 1, 1);
		
		menu.addLabel("Player name", 0, 2, 1, 1);
		menu.addTextField("Player name", 1, 2, 1, 1);
		
		menu.addOption("Connect", 7, 0, 3, 1, 1);
		menu.addOption("Back", 5, 0, 4, 1, 1);
		
		this.menus[2] = menu;
		
		//Pause menu
		menu = new Menu(this, "Pause menu", "Paused", screenSize[0], screenSize[1]);
		menu.addOption("Resume", 9, 0, 1, 1, 1);
		menu.addOption("Quit", 8, 0, 2, 1, 1);
		this.menus[3] = menu;
		
		//Loading screen
		menu = new Menu(this, "Loading screen", "Loading", screenSize[0], screenSize[1]);
		this.menus[4] = menu;
	}
	
	public Menu getMenu(int id) {
		return this.menus[id];
	}
	
	public Menu getMenu() {
		return this.menu;
	}
	
	public void openMenu(int id) {
		if(this.getMenu() != null) this.lp.remove(this.getMenu());
		if(id != -1) {
			Menu menu = this.getMenu(id);
			this.setMenu(menu);
		}
	}
	
	public void setMenu(Menu menu) {
		this.menu = menu;
		this.lp.add(menu, new Integer(101));
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}

	public MouseListener getMouseListener() {
		return mouseListener;
	}

	public void setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}

	public int[] getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(int[] screenSize) {
		this.screenSize = screenSize;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Insets getWindowInsets() {
		return windowInsets;
	}

	public void setWindowInsets(Insets insets) {
		this.windowInsets = insets;
	}

	public Menu[] getMenus() {
		return menus;
	}

	public void setMenus(Menu[] menus) {
		this.menus = menus;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean isGameExists() {
		return gameExists;
	}

	public void setGameExists(boolean gameExists) {
		this.gameExists = gameExists;
	}

	public Cursor getCrosshair() {
		return crosshair;
	}

	public void setCrosshair(Cursor crosshair) {
		this.crosshair = crosshair;
	}

	public Cursor getDef() {
		return def;
	}

	public void setDef(Cursor def) {
		this.def = def;
	}

	public JLabel getDebug() {
		return debug;
	}

	public void setDebug(JLabel debug) {
		this.debug = debug;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public InetAddress getClientIp() {
		return this.clientIp;
	}

	public void setClientIp(InetAddress iPAddress) {
		this.clientIp = iPAddress;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
