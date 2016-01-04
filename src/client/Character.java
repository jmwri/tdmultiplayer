package client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Character extends Entity {

	private int id;
	private int health;
	private boolean dead;
	private Weapon[] slots = new Weapon[2];
	private int equippedSlot;
	private String splitter = ":split:";
	private BufferedImage spriteAlive;
	private BufferedImage spriteDead;
	
	public Character(Main main, String name, BufferedImage spriteAlive, BufferedImage spriteDead) {
		super(main, name, 24, 24, spriteAlive);
		this.spriteAlive = spriteAlive;
		this.spriteDead = spriteDead;
		this.setHitboxOffset(-3);
		this.setDead(true);
	}
	
	public void tick() {
		this.move();
		super.tick();
		if(this.getEquippedWeapon() != null) {
			this.getEquippedWeapon().setX(this.getX());
			this.getEquippedWeapon().setY(this.getY());
			this.getEquippedWeapon().setRotation(this.getRotation());
			this.getEquippedWeapon().tick();
		}
	}
	
	public void render(Graphics g, int offsetx, int offsety) {
		super.render(g, offsetx, offsety);
		if(this.getEquippedWeapon() != null) this.getEquippedWeapon().render(g, offsetx, offsety);
	}
	
	public void spawn() {
		int[][] spawns = this.getMain().getGame().getMap().getSpawns();
		int limit = spawns.length;
		Random randomno = new Random();
	    int rand = randomno.nextInt(limit);
		this.spawn(spawns[rand][0], spawns[rand][1]);
	}
	
	public void spawn(int x, int y) {
		this.setHealth(100);
		this.setX(x);
		this.setY(y);
		this.setDead(false);
		this.setSprite(this.getSpriteAlive());
		this.addWeapon(this.getMain().getGame().getWeaponFactory().generate(1), 1);
		this.equip(1);
	}
	
	public void respawn(int x, int y) {
		this.spawn(x, y);
	}
	
	public void die() {
		this.setDead(true);
		this.setSprite(this.getSpriteDead());
		this.addWeapon(null, 1);
	}
	
	public void addWeapon(Weapon w, int slot) {
		if(w != null) w.setOwner(this);
		this.slots[slot-1] = w;
	}
	
	public Weapon getEquippedWeapon() {
		if(this.getEquippedSlot() > 0) {
			return this.slots[this.equippedSlot-1];
		} else {
			return null;
		}
	}
	
	public int getEquippedWeaponId() {
		if(this.getEquippedSlot() > 0) {
			return this.slots[this.equippedSlot-1].getId();
		} else {
			return -1;
		}
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		if(this.health < 0) this.health = 0;
		if(this.health == 0) this.die();
	}
	
	public void decreaseHealth(int player, int weaponid, int amount) {
		this.getMain().getGame().sendPacket(05, this.getId() + this.splitter + amount + this.splitter + player + this.splitter + weaponid);
	}
	
	public void decreaseHealth(int amount) {
		this.setHealth(this.health -= amount);
	}
	
	public void increaseHealth(int amount) {
		this.health += amount;
	}

	public int getEquippedSlot() {
		return equippedSlot;
	}

	public void equip(int equippedSlot) {
		this.equippedSlot = equippedSlot;
		if(this.getEquippedWeapon() != null) {
			this.getEquippedWeapon().setX(this.getRotationCenterX());
			this.getEquippedWeapon().setY(this.getRotationCenterY());
			this.getEquippedWeapon().setRotation(this.getRotation());
		}
	}
	
	public String getWeaponName() {
		if(this.getEquippedWeapon() != null) {
			return this.getEquippedWeapon().getName();
		} else {
			return "Nothing";
		}
	}
	
	public String getWeaponAmmo() {
		if(this.getEquippedWeapon() != null) {
			Weapon w = this.getEquippedWeapon();
			return w.getLoadedAmmo() + "/" + w.getTotalAmmo();
		} else {
			return "0/0";
		}
	}
	
	public boolean isWeaponReloading() {
		if(this.getEquippedWeapon() != null) {
			Weapon w = this.getEquippedWeapon();
			if(w.isReloading()) {
				return true;
			}
		}
		return false;
	}
	
	public double getWeaponReloadPercentage() {
		return this.getEquippedWeapon().getReloadPercentage();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public boolean isDead() {
		return dead;
	}

	public Weapon[] getSlots() {
		return slots;
	}

	public void setSlots(Weapon[] slots) {
		this.slots = slots;
	}

	public String getSplitter() {
		return splitter;
	}

	public void setSplitter(String splitter) {
		this.splitter = splitter;
	}

	public BufferedImage getSpriteAlive() {
		return spriteAlive;
	}

	public void setSpriteAlive(BufferedImage spriteAlive) {
		this.spriteAlive = spriteAlive;
	}

	public BufferedImage getSpriteDead() {
		return spriteDead;
	}

	public void setSpriteDead(BufferedImage spriteDead) {
		this.spriteDead = spriteDead;
	}

	public void setEquippedSlot(int equippedSlot) {
		this.equippedSlot = equippedSlot;
	}
	
}
