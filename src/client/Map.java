package client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Map {
	
	private String name;
	private int[][] base;
	private Tile[][] tiles;
	private int tileWidth;
	private int tileHeight;
	private Game game;
	private int[][] spawns;
	
	private SpriteSheet mapSS = null;
	
	public Map(String name, Game game) {
		this.tileWidth = 32;
		this.tileHeight = 32;
		this.game = game;
		
		this.name = name;
		this.base = new int[][] {
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,1,1,1,1,1,1,1,1,1,2,2,1,3,3,1,1,1,1,2,2,2,2,2,2,1},
				{1,2,2,2,2,1,3,3,3,3,3,3,3,1,2,2,1,3,3,3,3,3,3,2,2,2,2,2,2,1},
				{1,2,2,2,2,3,3,3,3,3,3,3,3,1,2,2,1,3,3,1,1,1,1,2,2,2,2,2,2,1},
				{1,2,2,2,2,1,3,3,3,3,3,3,3,1,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,1,1,1,1,3,3,3,3,1,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,1,3,3,3,3,1,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,1,1,3,1,1,1,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,3,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,3,1,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,1,2,2,2,2,1,1,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
				{1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
		};
		
		
		
		int[][] spawn = new int[4][2];
		spawn[0][0] = (2 * this.tileWidth) + this.tileWidth/2;
		spawn[0][1] = (2 * this.tileHeight) + this.tileHeight/2;
		
		spawn[1][0] = (11 * this.tileWidth) + this.tileWidth/2;
		spawn[1][1] = (6 * this.tileHeight) + this.tileHeight/2;
		
		spawn[2][0] = (23 * this.tileWidth) + this.tileWidth/2;
		spawn[2][1] = (2 * this.tileHeight) + this.tileHeight/2;
		
		spawn[3][0] = (16 * this.tileWidth) + this.tileWidth/2;
		spawn[3][1] = (14 * this.tileHeight) + this.tileHeight/2;
		this.setSpawns(spawn);
		
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage loadedSS = null;
		
		try	{
			loadedSS = loader.loadImage("mapSpriteSheet.png");
		} catch(IOException ex) {
			
		}
		this.mapSS = new SpriteSheet(loadedSS);
		
		this.tiles = new Tile[this.base.length][];
		for(int y = 0; y < this.base.length; y++) {
			Tile[] row = new Tile[this.base[y].length];
			for(int x = 0; x < this.base[y].length; x++) {
				row[x] = this.newTile(x, y, this.base[y][x]);
			}
			this.tiles[y] = row;
		}
	}
	
	public void tick() {
	}
	
	public void render(Graphics g, int offsetx, int offsety, int screenWidth, int screenHeight) {
		
		int leftVisible = offsetx/this.tileWidth;
		int topVisible = offsety/this.tileHeight;
		int rightVisible = leftVisible + (int) Math.ceil(screenWidth/this.tileWidth) + 1;
		int bottomVisible = topVisible + (int) Math.ceil(screenHeight/this.tileHeight) + 2;
		
		
		
		if(leftVisible < 0) leftVisible = 0;
		if(topVisible < 0) topVisible = 0;
		if(rightVisible > this.tiles[0].length) rightVisible = this.tiles[0].length;
		if(bottomVisible > this.tiles.length) bottomVisible = this.tiles.length;
		
		for(int y = topVisible; y < bottomVisible; y++) {
			for(int x = leftVisible; x < rightVisible; x++) {
				this.tiles[y][x].render(g, offsetx, offsety);
			}
		}
	}
	
	public Tile newTile(int xcoord, int ycoord, int id) {
		Tile t = null;
		BufferedImage sprite;
		switch(id) {
			case 0: //Null block
				sprite = mapSS.grabSprite(3, 0, this.tileWidth, tileHeight, true);
				t = new Tile(id, this.tileWidth, tileHeight, xcoord, ycoord, true, sprite);
				break;
			
			case 1: //Wall
				sprite = mapSS.grabSprite(0, 0, this.tileWidth, tileHeight, true);
				t = new Tile(id, this.tileWidth, tileHeight, xcoord, ycoord, true, sprite);
				break;
			
			case 2: //Sand
				sprite = mapSS.grabSprite(1, 0, this.tileWidth, tileHeight, true);
				t = new Tile(id, this.tileWidth, tileHeight, xcoord, ycoord, false, sprite);
				break;
			
			case 3: //Floorboard
				sprite = mapSS.grabSprite(2, 0, this.tileWidth, tileHeight, true);
				t = new Tile(id, this.tileWidth, tileHeight, xcoord, ycoord, false, sprite);
				break;
		}
		return t;
	}
	
	public Tile getTileFromCoords(double x, double y) {
		x = (int) Math.floor(this.tileWidth/x);
		y = (int) Math.floor(this.tileHeight/y);
		Tile t = this.getTile((int) x, (int) y);
		return t;
	}
	
	public Tile getTile(int x, int y) {
		Tile t = this.tiles[y][x];
		return t;
	}
	
	public ArrayList<Tile> getTilesInRadius(double x, double y, int radius) {
		int startx = (int) (x - radius);
		int starty = (int) (y - radius);
		int endx = (int) (x + radius);
		int endy = (int) (y + radius);
		
		Point start = this.getCoord(startx, starty);
		Point end = this.getCoord(endx, endy);
		startx = (int) start.getX();
		starty = (int) start.getY();
		endx = (int) end.getX();
		endy = (int) end.getY();
		
		if(startx < 0) startx = 0;
		if(starty < 0) starty = 0;
		if(endx > this.base[0].length) endx = this.base[0].length;
		if(endy > this.base.length) endy = this.base.length;
		
		ArrayList<Tile> t = new ArrayList<Tile>();
		
		for(int ycoord = starty; ycoord < endy; ycoord++) {
			for(int xcoord = startx; xcoord < endx; xcoord++) {
				t.add(this.getTile(xcoord, ycoord));
			}
		}
		
		return t;
	}
	
	public Point getCoord(int x, int y) {
		x = (int) Math.floor(x / this.tileWidth);
		y = (int) Math.floor(y / this.tileHeight);
		
		return new Point(x, y);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[][] getBase() {
		return base;
	}

	public void setBase(int[][] base) {
		this.base = base;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public SpriteSheet getMapSS() {
		return mapSS;
	}

	public void setMapSS(SpriteSheet mapSS) {
		this.mapSS = mapSS;
	}

	public int[][] getSpawns() {
		return spawns;
	}

	public void setSpawns(int[][] spawns) {
		this.spawns = spawns;
	}
	
}
