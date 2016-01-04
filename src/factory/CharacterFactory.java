package factory;
import java.awt.image.BufferedImage;
import java.io.IOException;

import client.BufferedImageLoader;
import client.Main;
import client.Player;
import client.SpriteSheet;
import client.Character;

public class CharacterFactory extends Factory {
	
	private SpriteSheet spriteSheet = null;
	
	public CharacterFactory(Main main) {
		super(main);
		
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage loadedSS = null;
		
		try	{
			loadedSS = loader.loadImage("characterSpriteSheet.png");
		} catch(IOException ex) {
			
		}
		this.setSpriteSheet(new SpriteSheet(loadedSS));
	}
	
	public Character generate(String name) {
		BufferedImage spriteAlive = this.getSpriteSheet().grabSprite(0, 0, 24, 22, true);
		BufferedImage spriteDead = this.getSpriteSheet().grabSprite(1, 0, 24, 22, true);
		Character c = new Character(this.getMain(), name, spriteAlive, spriteDead);
		return c;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
}
