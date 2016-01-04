package factory;
import java.awt.image.BufferedImage;
import java.io.IOException;

import client.BufferedImageLoader;
import client.Character;
import client.Main;
import client.SpriteSheet;
import client.Player;

public class PlayerFactory extends Factory {
	
	private SpriteSheet spriteSheet = null;
	
	public PlayerFactory(Main main) {
		super(main);
		
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage loadedSS = null;
		
		try	{
			loadedSS = loader.loadImage("characterSpriteSheet.png");
		} catch(IOException ex) {
			
		}
		this.setSpriteSheet(new SpriteSheet(loadedSS));
	}
	
	public Player generate(String name) {
		int speed = 2;
		BufferedImage spriteAlive = this.getSpriteSheet().grabSprite(0, 0, 24, 22, true);
		BufferedImage spriteDead = this.getSpriteSheet().grabSprite(1, 0, 24, 22, true);
		Player p = new Player(this.getMain(), name, speed, spriteAlive, spriteDead);
		return p;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
}
