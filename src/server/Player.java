package server;

import java.net.InetAddress;

public class Player {
	
	private int id;
	private String name;
	private int x;
	private int y;
	private int weaponId;
	private int health;
	private double rotation;
	private InetAddress ip;
	private int port;
	private long lastHeard;
	private long lastSent;

	public Player(int id, String name, double x, double y, int weaponId, int health, double rotation, InetAddress ip, int port) {
		this.setId(id);
		this.setName(name);
		this.setX((int)x);
		this.setY((int)y);
		this.setWeaponId(weaponId);
		this.setHealth(health);
		this.setRotation(rotation);
		this.setIp(ip);
		this.setPort(port);
		this.setLastHeard(System.currentTimeMillis());
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWeaponId() {
		return weaponId;
	}

	public void setWeaponId(int weaponId) {
		this.weaponId = weaponId;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
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

}
