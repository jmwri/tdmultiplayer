package client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Weapon extends Entity {
	private Character owner;
	private int id;
	private int damage;
	private int loadedAmmo;
	private int loadedCapacity;
	private int totalAmmo;
	private int reloadTime;
	private boolean reloading;
	private int speed;
	private long lastShot = 0;
	private int timeBetweenShots;
	private BufferedImage bulletSprite;
	private int shootFromX, shootFromY;
	private String splitter = ":split:";

	public Weapon(Main main, int id, String name, int width, int height, BufferedImage sprite, BufferedImage bulletSprite, int renderPositionX, int renderPositionY, int shootFromX, int shootFromY, int damage, int loadedAmmo, int loadedCapacity, int totalAmmo, int reloadTime, int speed, int timeBetweenShots) {
		super(main, name, width, height, sprite);
		this.setId(id);
		this.setDamage(damage);
		this.setLoadedAmmo(loadedAmmo);
		this.setLoadedCapacity(loadedCapacity);
		this.setTotalAmmo(totalAmmo);
		this.setReloadTime(reloadTime);
		this.setSpeed(speed);
		this.setRotationCenterX((this.getWidth() / 2) + renderPositionX);
		this.setRotationCenterY((this.getHeight() / 2) + renderPositionY);
		this.setShootFromX(shootFromX);
		this.setShootFromY(shootFromY);
		this.setTimeBetweenShots(timeBetweenShots);
		this.setBulletSprite(bulletSprite);
	}
	
	public void tick() {
		long curTime = System.currentTimeMillis();
		if(this.isReloading()) {
			long tarTime = this.getLastShot() + this.getReloadTime();
			if(curTime >= tarTime) {
				//Finished reloading
				int toReload = 0;
				int ammoNeeded = this.getLoadedCapacity() - this.getLoadedAmmo();
				if(this.getTotalAmmo() >= ammoNeeded) {
					toReload = ammoNeeded;
				} else {
					toReload = this.getTotalAmmo();
				}
				
				this.setLoadedAmmo(this.getLoadedAmmo() + toReload);
				this.setTotalAmmo(this.getTotalAmmo() - toReload);
				this.setReloading(false);
				
				System.out.println("Reloaded! Ammo left: " + this.getLoadedAmmo() + "/" + this.getTotalAmmo());
			} else {
				//Still reloading...
				int timeLeft = (int) (tarTime - curTime);
			}
		}
		
		this.move();
	}
	
	public void fire() {
		if(System.currentTimeMillis() >= this.getLastShot() + this.getTimeBetweenShots()) {
			if(this.getLoadedAmmo() > 0 && !this.isReloading()) {
				this.setLastShot(System.currentTimeMillis());
				this.setLoadedAmmo(this.getLoadedAmmo() - 1);
				
				Projectile p = this.getMain().getGame().createProjectile(this.getId(), this.getOwner(), (int)this.getShootFromX(), (int)this.getShootFromY(), (int)this.getRotation());
				this.getMain().getGame().sendPacket(04, p.getId() + this.splitter + p.getOwner().getId() + this.splitter + p.getX() + this.splitter + p.getY() + this.splitter + p.getRotation());
			}
		}
	}
	
	public void reload() {
		if(this.getLoadedAmmo() < this.getLoadedCapacity() && (this.getLoadedAmmo() > 0 || this.getTotalAmmo() > 0)) {
			this.setReloading(true);
			this.setLastShot(System.currentTimeMillis());
		}
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getLoadedAmmo() {
		return loadedAmmo;
	}

	public void setLoadedAmmo(int loadedAmmo) {
		this.loadedAmmo = loadedAmmo;
	}

	public int getLoadedCapacity() {
		return loadedCapacity;
	}

	public void setLoadedCapacity(int loadedCapacity) {
		this.loadedCapacity = loadedCapacity;
	}

	public int getTotalAmmo() {
		return totalAmmo;
	}

	public void setTotalAmmo(int totalAmmo) {
		this.totalAmmo = totalAmmo;
	}

	public int getReloadTime() {
		return reloadTime;
	}

	public void setReloadTime(int reloadTime) {
		this.reloadTime = reloadTime;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public long getLastShot() {
		return lastShot;
	}

	public void setLastShot(long lastShot) {
		this.lastShot = lastShot;
	}

	public int getTimeBetweenShots() {
		return timeBetweenShots;
	}

	public void setTimeBetweenShots(int timeBetweenShots) {
		this.timeBetweenShots = timeBetweenShots;
	}

	public boolean isReloading() {
		return reloading;
	}

	public void setReloading(boolean reloading) {
		this.reloading = reloading;
	}
	
	public double getReloadPercentage() {
		int totTime = this.getReloadTime();
		long tarTime = this.getLastShot() + totTime;
		long curTime = System.currentTimeMillis();
		double timeLeft = tarTime - curTime;
		double timeThrough = totTime - timeLeft;
		
		double p = (timeThrough / totTime) * 100;
		return p;
	}

	public BufferedImage getBulletSprite() {
		return bulletSprite;
	}

	public void setBulletSprite(BufferedImage bulletSprite) {
		this.bulletSprite = bulletSprite;
	}

	public double getShootFromX() {
		return (this.getX() + (Math.sin(Math.toRadians(this.getRotation())) * this.shootFromX));
	}

	public void setShootFromX(int shootFromX) {
		this.shootFromX = shootFromX;
	}

	public double getShootFromY() {
		return (this.getY() + (Math.cos(Math.toRadians(this.getRotation())) * this.shootFromY));
	}

	public void setShootFromY(int shootFromY) {
		this.shootFromY = shootFromY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Character getOwner() {
		return owner;
	}

	public void setOwner(Character owner) {
		this.owner = owner;
	}

}
