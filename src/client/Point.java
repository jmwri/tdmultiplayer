package client;

@SuppressWarnings("serial")
public class Point extends java.awt.Point {

	public Point() {
		super();
	}
	
	public Point(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}
	
	public void setX(double x) {
		this.x = (int) x;
	}
	
	public void setY(double y) {
		this.x = (int) y;
	}

}
