package server;

public class Entity {

	private int id;
	private int x;
	private int y;
	private double rotation;
	
	public Entity(int id, int x, int y, double rotation) {
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setRotation(rotation);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
}
