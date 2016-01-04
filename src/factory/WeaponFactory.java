package factory;
import java.awt.image.BufferedImage;
import java.io.IOException;

import client.BufferedImageLoader;
import client.Main;
import client.SpriteSheet;
import client.Weapon;

public class WeaponFactory extends Factory {
	
	private SpriteSheet spriteSheet = null;
	
	public WeaponFactory(Main main) {
		super(main);
		
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage loadedSS = null;
		
		try	{
			loadedSS = loader.loadImage("weaponSpriteSheet.png");
		} catch(IOException ex) {
			
		}
		this.setSpriteSheet(new SpriteSheet(loadedSS));
	}
	
	public Weapon generate(int id) {
		Weapon w = null;
		switch(id) {
			case 1:
				BufferedImage sprite = this.getSpriteSheet().grabSprite(0, 0, 6, 18);
				BufferedImage bulletSprite = this.getSpriteSheet().grabSprite(7, 0, 3, 3);
				w = new Weapon(this.getMain(), id, "Machine gun", 6, 18, sprite, bulletSprite, 0, 14, 0, 0, 10, 30, 30, 150, 3000, 20, 100);
				break;
		}
		return w;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
}
