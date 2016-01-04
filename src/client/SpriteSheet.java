package client;


import java.awt.image.BufferedImage;

public class SpriteSheet {
	
	private BufferedImage spriteSheet;
	
	public SpriteSheet(BufferedImage ss) {
		this.spriteSheet = ss;
	}
	
	public BufferedImage grabSprite(int x, int y, int width, int height) {
		BufferedImage sprite = spriteSheet.getSubimage(x, y, width, height);
		return sprite;
	}
	
	public BufferedImage grabSprite(int xcoord, int ycoord, int width, int height, boolean useCoords) {
		int x = xcoord * width;
		int y = ycoord * height;
		return this.grabSprite(x, y, width, height);
	}
}
