package client;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {
	
	private boolean tileSet = false;
	private int tileWidth;
	private int tileHeight;
	private boolean mouseDown = false;
	
	public MouseListener() {
	}
	
	public MouseListener(int tileWidth, int tileHeight) {
		this.setTileSet(true);
		this.setTileWidth(tileWidth);
		this.setTileHeight(tileHeight);
	}
	
    public void mouseClicked(MouseEvent e) {
        // Finds the location of the mouse
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point pointerLocation = pointerInfo.getLocation();

        // Gets the x -> and y co-ordinates
        int x = (int) pointerLocation.getX();
        int y = (int) pointerLocation.getY();
        System.out.println("Mouse x: " + x);
        System.out.println("Mouse y: " + y);
        
        if(this.isTileSet()) {
	        // Determines which tile the click occured on
	        int xTile = x/this.getTileWidth();
	        int yTile = y/this.getTileHeight();
	
	        System.out.println("X Tile: " + xTile);
	        System.out.println("Y Tile: " + yTile);
        }
    }

	public boolean isTileSet() {
		return tileSet;
	}

	public void setTileSet(boolean tileSet) {
		this.tileSet = tileSet;
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
}