package factory;
import java.awt.image.BufferedImage;
import java.io.IOException;

import client.BufferedImageLoader;
import client.Main;
import client.Projectile;
import client.SpriteSheet;
import client.Weapon;
import client.Character;

public class ProjectileFactory extends Factory {
	
	private SpriteSheet spriteSheet = null;
	
	public ProjectileFactory(Main main) {
		super(main);
		
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage loadedSS = null;
		
		try	{
			loadedSS = loader.loadImage("weaponSpriteSheet.png");
		} catch(IOException ex) {
			
		}
		this.setSpriteSheet(new SpriteSheet(loadedSS));
	}
	
	public Projectile generate(int id, Character owner, int x, int y, int rotation) {
		
		Weapon w = this.getMain().getGame().getWeaponFactory().generate(id);
		Projectile p = new Projectile(this.getMain(), id, owner, w.getName() + " bullet", x, y, w.getBulletSprite(), rotation, w.getSpeed(), w.getDamage());
		
		return p;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public void setSpriteSheet(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
}
