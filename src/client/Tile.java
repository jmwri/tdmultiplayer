package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Tile {
	
	private int id;
	private int width;
	private int height;
	private int xcoord;
	private int ycoord;
	private int x;
	private int y;
	private boolean solid;
	private BufferedImage sprite;
	
	public Tile(int id, int width, int height, int xcoord, int ycoord, boolean solid, BufferedImage sprite) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.xcoord = xcoord;
		this.ycoord = ycoord;
		this.x = this.width * this.xcoord;
		this.y = this.height * this.ycoord;
		this.setSolid(solid);
		this.sprite = sprite;
	}
	
	public void tick() {
		
	}
	
	public void render(Graphics g, int offsetx, int offsety) {
		g.drawImage(this.sprite, this.x - offsetx, this.y - offsety, this.width, this.height, null);
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setWidth(int width) {
		this.id = width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getXCoord() {
		return this.xcoord;
	}
	
	public void setXCoord(int xcoord) {
		this.xcoord = xcoord;
	}
	
	public int getYCoord() {
		return this.ycoord;
	}
	
	public void setYCoord(int ycoord) {
		this.ycoord = ycoord;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public BufferedImage getSprite() {
		return this.sprite;
	}
	
	public void setSprite(BufferedImage sprite) {
		this.sprite = sprite;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) this.getX(), (int) this.getY(), this.getWidth(), this.getHeight());
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	
}
