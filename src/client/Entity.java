package client;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Entity {

	private String name;
	private double x, y, moveX, moveY, oldX, oldY;
	private int width, height;
	private double rotation;
	private int rotationCenterX, rotationCenterY;
	private int hitboxOffset;
	private BufferedImage sprite;
	private int speed, strafeSpeed;
	private Main main;
	private boolean inWorld;
	
	public Entity(Main main, String name, int width, int height, BufferedImage sprite) {
		this.setMain(main);
		this.setName(name);
		this.setWidth(width);
		this.setHeight(height);
		this.setSprite(sprite);
		this.setRotationCenterX(this.width/2);
		this.setRotationCenterY(this.height/2);
		this.setHitboxOffset(0);
		this.setInWorld(true);
	}
	
	public void tick() {
	}
	
	public void render(Graphics g, int offsetx, int offsety) {
		AffineTransform at = new AffineTransform();
        at.translate(this.x - offsetx, this.y - offsety); //Move to center of screen

        at.rotate(Math.toRadians(this.rotation)); //Do the rotation
        at.translate(-this.getRotationCenterX(), -this.getRotationCenterY()); //Rotate around the correct point
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.sprite, at, null);
	}
	
	public void move() {
		this.doMove(this.getMoveX(), this.getMoveY());
	}
	
	public void doMove(double x, double y) {
		boolean movedx = this.doMoveX(x);
		boolean movedy = this.doMoveY(y);
		if(!movedx || !movedy) {
			this.collided();
		}
	}
	
	public boolean doMoveX(double x) {
		if(x != 0) {
			this.setOldX(this.getX());
			this.setX(this.getX() + x);
			boolean[] corners = this.checkCollisions();
			if(corners[0] || corners[1] || corners[2] || corners[3]) {
				this.moveBack(true, false);
				return false;
			}
			this.setMoveX(0);
		}
		return true;
	}
	
	public boolean doMoveY(double y) {
		if(y != 0) {
			this.setOldY(this.getY());
			this.setY(this.getY() + y);
			boolean[] corners = this.checkCollisions();
			if(corners[0] || corners[1] || corners[2] || corners[3]) {
				this.moveBack(false, true);
				return false;
			}
			this.setMoveY(0);
		}
		return true;
	}
	
	public void moveToTarget() {
		double rad = Math.toRadians(this.getRotation());
		double xvector = Math.sin(rad) * this.getSpeed();
		double yvector = Math.cos(rad) * this.getSpeed();
		
		this.setMoveX(xvector);
		this.setMoveY(-yvector);
		
		this.move();
	}
	
	public void moveBack() {
		this.setX(this.getOldX());
		this.setY(this.getOldY());
	}
	
	public void moveBack(boolean x, boolean y) {
		if(x) this.setX(this.getOldX());
		if(y) this.setY(this.getOldY());
	}
	
	public boolean[] checkCollisions() {
		ArrayList<Tile> tiles = this.getMain().getGame().getMap().getTilesInRadius(this.getX() - (this.getWidth() / 2), this.getY() - (this.getHeight() / 2), 64);
		boolean[] corners = {false, false, false, false};
		
		for(Tile t : tiles) {
			if(t.isSolid()) {
				for(int c = 0; c < 4; c++) {
					if(!corners[c]) {
						if(this.hitTest(t.getBounds(), c)) {
							//Current corner is colliding
							corners[c] = true;
						}
					}
				}
			}
		}
		return corners;
	}
	
	public ArrayList<Character> checkCharacterCollisions() {
		ArrayList<Character> chars = new ArrayList<Character>();
		
		double x = this.getX() - (this.getWidth() / 2);
		double y = this.getY() - (this.getHeight() / 2);
		int radius = 64;
		
		for(Character c : this.getMain().getGame().getCharacters()) {
			if(c.getX() > x - radius && c.getX() < x + radius && c.getY() > y - radius && c.getY() < y + radius) {
				if(this.hitTest(c.getBounds())) {
					chars.add(c);
				}
			}
		}
		return chars;
	}
	
	public boolean hitTest(Rectangle rec) {
		return this.getBounds().intersects(rec);
	}
	
	public boolean hitTest(Rectangle rec, int section) {
		return this.getBounds(section).intersects(rec);
	}
	
	public void collided() {
		
	}
	
	public void remove() {
		this.setInWorld(false);
		this.getMain().getGame().removeEntity(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setX(double x) {
		this.setX((int) x);
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setY(double y) {
		this.setY((int) y);
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
	
	public double getRotation() {
		return this.rotation;
	}
	
	public void setRotation(double d) {
		while(d > 360) {
			d -= 360;
		}
		while(d < 0) {
			d += 360;
		}
		this.rotation = d;
	}
	
	public BufferedImage getSprite() {
		return this.sprite;
	}
	
	public void setSprite(BufferedImage sprite) {
		this.sprite = sprite;
	}

	public int getRotationCenterX() {
		return rotationCenterX;
	}

	public void setRotationCenterX(int rotationCenterX) {
		this.rotationCenterX = rotationCenterX;
	}

	public int getRotationCenterY() {
		return rotationCenterY;
	}

	public void setRotationCenterY(int rotationCenterY) {
		this.rotationCenterY = rotationCenterY;
	}
	
	public int getSpeed() {
		return this.speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getStrafeSpeed() {
		return this.strafeSpeed;
	}

	public void setStrafeSpeed(int speed) {
		this.strafeSpeed = speed;
	}

	public double getMoveX() {
		return moveX;
	}

	public void setMoveX(double xvector) {
		this.moveX = xvector;
	}

	public double getMoveY() {
		return moveY;
	}

	public void setMoveY(double yvector) {
		this.moveY = yvector;
	}

	public double getOldX() {
		return oldX;
	}

	public void setOldX(int oldX) {
		this.oldX = oldX;
	}
	
	public void setOldX(double oldX) {
		this.oldX = oldX;
	}

	public double getOldY() {
		return oldY;
	}

	public void setOldY(int oldY) {
		this.oldY = oldY;
	}
	
	public void setOldY(double oldY) {
		this.oldY = oldY;
	}
	
	public Rectangle getBounds() {
		return this.getBounds(999);
	}
	
	public Rectangle getBounds(int section) {
		int x, y, width, height;
		double thisX = this.getX() - this.getHitboxOffset();
		double thisY = this.getY() - this.getHitboxOffset();
		int thisWidth = this.getWidth() + (this.getHitboxOffset() * 2);
		int thisHeight = this.getHeight() + (this.getHitboxOffset() * 2);
		
		switch(section) {
			default: //Whole hitbox
				x = (int) thisX - this.getRotationCenterX();
				y = (int) thisY - this.getRotationCenterY();
				width = thisWidth;
				height = thisHeight;
				break;
			
			case 0: //Top left corner
				x = (int) thisX - this.getRotationCenterX();
				y = (int) thisY - this.getRotationCenterY();
				width = 4;
				height = 4;
				break;
			
			case 1: //Top right corner
				x = (int) (thisX - this.getRotationCenterX()) + (thisWidth - 4);
				y = (int) thisY - this.getRotationCenterY();
				width = 4;
				height = 4;
				break;
				
			case 2: //Bottom right corner
				x = (int) (thisX - this.getRotationCenterX()) + (thisWidth - 4);
				y = (int) (thisY - this.getRotationCenterY()) + (thisHeight - 4);
				width = 4;
				height = 4;
				break;
				
			case 3: //Bottom left corner
				x = (int) thisX - this.getRotationCenterX();
				y = (int) (thisY - this.getRotationCenterY()) + (thisHeight - 4);
				width = 4;
				height = 4;
				break;
		}
		
		return new Rectangle(x, y, width, height);
	}

	public int getHitboxOffset() {
		return hitboxOffset;
	}

	public void setHitboxOffset(int hitboxOffset) {
		this.hitboxOffset = hitboxOffset;
	}

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public boolean isInWorld() {
		return inWorld;
	}

	public void setInWorld(boolean inWorld) {
		this.inWorld = inWorld;
	}
	
}
