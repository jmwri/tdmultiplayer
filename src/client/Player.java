package client;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Player extends Character {

	private Point mousePosition;
	private boolean mouseClicked = false;
	private boolean loaded;
	private String splitter = ":split:";
	private long diedAt;
	
	public Player(Main main, String name, int speed, BufferedImage spriteAlive, BufferedImage spriteDead) {
		super(main, name, spriteAlive, spriteDead);
		this.setSpeed(speed);
		this.setLoaded(true);
		
		this.addWeapon(this.getMain().getGame().getWeaponFactory().generate(1), 1);
		this.equip(1);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if(this.isDead()) {
			if(System.currentTimeMillis() >= (this.getDiedAt() + 3000)) {
				this.respawn();
			}
		} else {
			if(this.getMousePosition() != null) {
				double xDistance = this.getMousePosition().getX() - this.getX();
				double yDistance = this.getMousePosition().getY() - this.getY();
				this.setRotation(Math.toDegrees(Math.atan2(xDistance, -yDistance)));
			}
			
			
			//Movement
			InputHandler i = this.getMain().getInputHandler();
			if(i.isKeyDown(KeyEvent.VK_W)) {
				this.setMoveY(-this.getSpeed());
			}
			if(i.isKeyDown(KeyEvent.VK_S)) {
				this.setMoveY(this.getSpeed());
			}
			if(i.isKeyDown(KeyEvent.VK_A)) {
				this.setMoveX(-this.getSpeed());
			}
			if(i.isKeyDown(KeyEvent.VK_D)) {
				this.setMoveX(this.getSpeed());
			}
			
			//Weapons & slots
			if(i.isKeyDown(KeyEvent.VK_R)) {
				this.reload();
			}
			if(i.isKeyDown(KeyEvent.VK_1)) {
				this.equip(1);
			}
			if(i.isKeyDown(KeyEvent.VK_2)) {
				this.equip(2);
			}
			if(i.isKeyDown(KeyEvent.VK_RIGHT)) {
				this.setRotation(this.getRotation() + 5);
			}
			if(i.isKeyDown(KeyEvent.VK_LEFT)) {
				this.setRotation(this.getRotation() - 5);
			}
			if(isMouseClicked()) {
				this.fire();
			}
		}
	}

	public void fire() {
		if(this.getEquippedWeapon() != null) this.getEquippedWeapon().fire();
	}
	
	public void reload() {
		if(this.getEquippedWeapon() != null) this.getEquippedWeapon().reload();
	}
	
	public boolean checkCollision() {
		this.getMain().getGame().getMap().getTileFromCoords(this.getX(), this.getY());
		return false;
	}
	
	@Override
	public void die() {
		this.setDiedAt(System.currentTimeMillis());
		super.die();
	}
	
	public void respawn() {
		super.spawn();
		this.getMain().getGame().sendPacket(06, this.getId() + this.splitter + this.getX() + this.splitter + this.getY() + this.splitter + this.getHealth());
	}
	
	@Override
	public void decreaseHealth(int player, int weaponid, int amount) {
		super.decreaseHealth(player, weaponid, amount);
	}
	
	@Override
	public boolean doMoveX(double x) {
		if(x != 0) this.getMain().getGame().sendPacket(02, this.getId() + this.splitter + this.getX() + this.splitter + this.getY());
		return super.doMoveX(x);
	}
	
	@Override
	public boolean doMoveY(double y) {
		if(y != 0) this.getMain().getGame().sendPacket(02, this.getId() + this.splitter + this.getX() + this.splitter + this.getY());
		return super.doMoveY(y);
	}
	
	@Override
	public void setRotation(double rotation) {
		if((int)rotation != (int)this.getRotation()) {
			
			int rot = (int) rotation;
			this.getMain().getGame().sendPacket(03, this.getId() + this.splitter + rot);
			super.setRotation(rotation);
		}
	}
	
	private Point getMousePosition() {
		return this.mousePosition;
	}

	public void setMousePosition(double x, double y) {
		this.mousePosition = new Point(x,y - this.getHeight());
	}

	public boolean isMouseClicked() {
		return mouseClicked;
	}

	public void setMouseClicked(boolean mouseClicked) {
		this.mouseClicked = mouseClicked;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public long getDiedAt() {
		return diedAt;
	}

	public void setDiedAt(long diedAt) {
		this.diedAt = diedAt;
	}

}
