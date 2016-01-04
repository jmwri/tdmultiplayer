package server;

import java.util.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import net.Listener;

public class Server implements Runnable {

	private String map;
	private boolean isRunning;
	private DatagramSocket serverSocket;
	private Listener listener;
	private int port = 5000;
	private String splitter = ":split:";
	
	private List<Player> players = new CopyOnWriteArrayList<Player>();
	private ArrayList<Player> playersToRemove = new ArrayList<Player>();
	private int nextPlayerId = 1;
	private List<Entity> entities = new CopyOnWriteArrayList<Entity>();
	private int nextEntityId = 1;
	
	public Server(String map) {
		this.setMap(map);
		this.setPlayers(new ArrayList<Player>());
		this.setNextPlayerId(1);
		this.setEntities(new ArrayList<Entity>());
		this.setNextEntityId(1);
	}
	
	public void initialise() {
		try {
			this.serverSocket = new DatagramSocket(this.getPort());
		} catch (SocketException e) {
			System.out.println("Port already in use");
		}
		
		this.setListener(new Listener("Server", this.getServerSocket(), this));
		new Thread(this.getListener()).start();
		
		this.setRunning(true);
	}
	
	public void stop() {
		this.setRunning(false);
	}

	@Override
	public void run() {
		System.out.println("Server started...");
		
		while(this.isRunning()) {
			long curTime = System.currentTimeMillis();
			for(Player p : this.getPlayers()) {
				for(Player pr : this.playersToRemove) {
					if(p.getId() != pr.getId()) {
						this.sendPacket(10, p.getIp(), p.getPort(), ""+pr.getId());
					}
				}
			}
			for(Player pr : this.playersToRemove) {
				this.players.remove(pr);
			}
			this.playersToRemove.clear();
			
			for(Player p : 
				this.getPlayers()) {
				if(p.getLastHeard() < (curTime - 10000)) {
					this.playersToRemove.add(p);
				}
				if(p.getLastSent() < (curTime - 500)) {
					this.sendPacket(11, p.getIp(), p.getPort(), "Still connected");
					p.setLastSent(curTime);
				}
			}
		}
		System.out.println("Server stopped...");
	}
	
	public void sendPacket(int type, InetAddress ip, int port, String data) {
		byte[] sendData = new byte[1024];
		data = type + this.splitter + data;
		
		if(type < 10) {
			data = "0" + data;
		}
		sendData = (data).getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
		
		try {
			this.getServerSocket().send(sendPacket);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void dataReceived(String[] data, InetAddress ip, int port) {
		int packetType = Integer.parseInt(data[0]);
		switch(packetType) {
		//New user joined
		case 01:
			String name = data[1];
			double x = Double.parseDouble(data[2]);
			double y = Double.parseDouble(data[3]);
			int weapon = (int) Double.parseDouble(data[4]);
			int health = (int) Double.parseDouble(data[5]);
			double rotation = Double.parseDouble(data[6]);
			Player np = this.addPlayer(name, x, y, weapon, health, rotation, ip, port);
			np.setLastHeard(System.currentTimeMillis());
			break;
		
		//User moved
		case 02:
			int id = (int) Double.parseDouble(data[1]);
			x = Double.parseDouble(data[2]);
			y = Double.parseDouble(data[3]);
			
			for(Player p : this.getPlayers()) {
				if(p.getId() == id) {
					p.setX((int)x);
					p.setY((int)y);
					
					try {
						File file = new File("users.txt");
						FileReader fileReader = new FileReader(file);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						StringBuffer stringBuffer = new StringBuffer();
						String line;
						ArrayList<String> lines = new ArrayList<String>();
						while((line = bufferedReader.readLine()) != null) {
							//FIND PLAYER IN FILE AND REWRITE
							String[] tdata = line.split("---");
							String tname = tdata[0];
							Double tx = Double.parseDouble(tdata[1]);
							Double ty = Double.parseDouble(tdata[2]);
							if(p.getName().equalsIgnoreCase(tname)) {
								lines.add(tname + "---" + x + "---" + y);
							} else {
								lines.add(tname + "---" + tx + "---" + ty);
							}
						}
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("users.txt")));
					    for(String userline : lines) {
					    	out.println(userline);
					    }
					    out.close();
						fileReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					this.sendPacket(02, p.getIp(), p.getPort(), id + this.splitter + x + this.splitter + y);
				}
			}
			break;
		
			//User changed rotation
			case 03:
				id = (int) Double.parseDouble(data[1]);
				rotation = Double.parseDouble(data[2]);
			
				for(Player p : this.getPlayers()) {
					if(p.getId() == id) {
						p.setRotation(rotation);
					} else {
						this.sendPacket(04, p.getIp(), p.getPort(), id + this.splitter + rotation);
					}
				}
				break;
			
			//A player fired
			case 04:
				id = (int) Double.parseDouble(data[1]);
				int ownerId = (int) Double.parseDouble(data[2]);
				x = (int) Double.parseDouble(data[3]);
				y = (int) Double.parseDouble(data[4]);
				rotation = Double.parseDouble(data[5]);
				
				this.addProjectile(id, ownerId, x, y, rotation);
				System.out.println("Player " + ownerId + " shot");
				break;
			
			//A player was shot
			case 05:
				id = (int) Double.parseDouble(data[1]);
				int amount = (int) Double.parseDouble(data[2]);
				int attacker = (int) Double.parseDouble(data[3]);
				weapon = (int) Double.parseDouble(data[4]);
				Player shotp = null;
				
				for(Player p : this.getPlayers()) {
					if(p.getId() == id) {
						shotp = p;
						break;
					}
				}
				shotp.setHealth(shotp.getHealth() - amount);
				for(Player p : this.getPlayers()) {
					this.sendPacket(06, p.getIp(), p.getPort(), shotp.getId() + this.splitter + shotp.getHealth() + this.splitter + attacker + this.splitter + weapon);
				}
				System.out.println("Player " + attacker + " damaged player " + shotp.getId() + " ("+amount+")");
				break;
			
			//User respawned
			case 06:
				id = (int) Double.parseDouble(data[1]);
				x = Double.parseDouble(data[2]);
				y = Double.parseDouble(data[3]);
				health = (int) Double.parseDouble(data[4]);
				
				for(Player p : this.getPlayers()) {
					if(p.getId() == id) {
						p.setX((int)x);
						p.setY((int)y);
						p.setHealth(health);
					} else {
						this.sendPacket(07, p.getIp(), p.getPort(), id + this.splitter + x + this.splitter + y + this.splitter + health);
					}
				}
				break;
			
			//User connection check
			case 07:
				id = (int) Double.parseDouble(data[1]);
				for(Player p : this.getPlayers()) {
					if(p.getId() == id) {
						p.setLastHeard(System.currentTimeMillis());
					}
				}
				break;
		}
	}
	
	public Player addPlayer(String name, double x, double y, int weaponId, int health, double rotation, InetAddress ip, int port) {
		Player p = null;
		
		try {
			File file = new File("users.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] tdata = line.split("---");
				String tname = tdata[0];
				Double tx = Double.parseDouble(tdata[1]);
				Double ty = Double.parseDouble(tdata[2]);
				if(name.equalsIgnoreCase(tname)) {
					p = new Player(this.getNextPlayerIdAndIncrement(), tname, tx, ty, weaponId, health, rotation, ip, port);
					break;
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(p == null) {
			p = new Player(this.getNextPlayerIdAndIncrement(), name, x, y, weaponId, health, rotation, ip, port);
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("users.txt", true)));
			    out.println(name + "---" + x + "---" + y);
			    out.close();
			} catch (IOException e) {
			    
			}
		}
		
		this.getPlayers().add(p);
		
		//Send the new player their id
		this.sendPacket(01, p.getIp(), p.getPort(), p.getId() + this.splitter + p.getX() + this.splitter + p.getY());
		
		//Send player details to other players
		for(Player pl : this.getPlayers()) {
			if(pl.getId() != p.getId()) {
				this.sendPacket(03, pl.getIp(), pl.getPort(), p.getId() + this.splitter + p.getName() + this.splitter + p.getX() + this.splitter + p.getY() + this.splitter + p.getWeaponId() + this.splitter + p.getHealth() + this.splitter + p.getRotation());
			}
		}
		
		//Send other players details to new player
		for(Player pl : this.getPlayers()) {
			if(pl.getId() != p.getId()) {
				this.sendPacket(03, p.getIp(), p.getPort(), pl.getId() + this.splitter + pl.getName() + this.splitter + pl.getX() + this.splitter + pl.getY() + this.splitter + pl.getWeaponId() + this.splitter + pl.getHealth() + this.splitter + pl.getRotation());
			}
		}
		
		System.out.println("Server: Player connected: (" + p.getId() + ", x: "+p.getX()+" y:"+p.getY()+")" + p.getName() + " from " + p.getIp().toString() + ":" + p.getPort());
		return p;
	}
	
	public void addProjectile(int id, int ownerId, double x, double y, double rotation) {
		Projectile p = new Projectile(id, ownerId, (int) x, (int) y, rotation);
		this.getEntities().add(p);
		
		//Send proj to other players
		for(Player pl : this.getPlayers()) {
			if(pl.getId() != p.getOwnerId()) {
				this.sendPacket(05, pl.getIp(), pl.getPort(), p.getId() + this.splitter + p.getOwnerId() + this.splitter + p.getX() + this.splitter + p.getY() + this.splitter + p.getRotation());
			}
		}
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DatagramSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(DatagramSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}

	public List<Entity> getEntities() {
		return entities;
	}
	
	public int getNextEntityIdAndIncrement() {
		this.nextEntityId = this.nextEntityId + 1;
		return nextEntityId - 1;
	}

	public void setNextEntityId(int nextEntityId) {
		this.nextEntityId = nextEntityId;
	}

	public int getNextEntityId() {
		return nextEntityId;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public int getNextPlayerIdAndIncrement() {
		this.nextPlayerId = this.nextPlayerId + 1;
		return nextPlayerId - 1;
	}
	
	public int getNextPlayerId() {
		return nextPlayerId;
	}

	public void setNextPlayerId(int nextPlayerId) {
		this.nextPlayerId = nextPlayerId;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
}
