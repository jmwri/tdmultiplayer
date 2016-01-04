package factory;

import client.Main;

public class AbstractFactory {
	public Factory getFactory(String type, Main main) {
		Factory f = null;
		
		if(type == "weapon") {
			f = new WeaponFactory(main);
		} else if(type == "character") {
			f = new CharacterFactory(main);
		} else if(type == "player") {
			f = new PlayerFactory(main);
		} else if(type == "projectile") {
			f = new ProjectileFactory(main);
		}
		
		return f;
	}
}
