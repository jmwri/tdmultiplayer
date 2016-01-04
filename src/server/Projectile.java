package server;

public class Projectile extends Entity {

	private int ownerId;
	
	public Projectile(int id, int ownerId, int x, int y, double rotation) {
		super(id, x, y, rotation);
		this.setOwnerId(ownerId);
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public int getOwnerId() {
		return ownerId;
	}
	
}
