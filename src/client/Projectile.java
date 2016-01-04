package client;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Projectile extends Entity {
	
	private int id;
	private Character owner;
	private int damage;
	
	public Projectile(Main main, int id, Character owner, String name, double x, double y, BufferedImage sprite, double rotation, int speed, int damage) {
		super(main, name, sprite.getWidth(), sprite.getHeight(), sprite);
		this.setX((int) x);
		this.setY((int) y);
		this.setRotation(rotation);
		this.setSpeed(speed);
		this.setDamage(damage);
		this.setOwner(owner);
		this.setId(id);
	}
	
	public void tick() {
		super.tick();
		
		if(this.getOwner().getId() 
				== 
				this.getMain().getGame().getPlayer().getId()) {
			ArrayList<Character> chars = this.checkCharacterCollisionsExcludeOwner();
			
			if(!chars.isEmpty() && chars != null) {
				this.collided();
				for(Character c : chars) {
					if(!c.isDead()) c.decreaseHealth(this.getOwner().getId(), this.getId(), this.getDamage());
				}
			}
		}
		
		this.moveToTarget();
	}
	
	@Override
	public void collided() {
		this.remove();
	}
	
	public ArrayList<Character> checkCharacterCollisionsExcludeOwner() {
		ArrayList<Character> chars = new ArrayList<Character>();
		
		double x = this.getX() - (this.getWidth() / 2);
		double y = this.getY() - (this.getHeight() / 2);
		int radius = 64;
		
		for(Character c : this.getMain().getGame().getCharacters()) {
			if(c.getX() > x - radius && c.getX() < x + radius && c.getY() > y - radius && c.getY() < y + radius) {
				if(c != this.getOwner()) {
					if(this.hitTest(c.getBounds())) {
						chars.add(c);
					}
				}
			}
		}
		return chars;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setOwner(Character owner) {
		this.owner = owner;
	}

	public Character getOwner() {
		return owner;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
