package logic;

import logic.AbstractPacket;

public class Packet extends AbstractPacket {

	private final int id;
	private Point bottomLeftPoint;
	private boolean rotate;
	
	public Packet(int id, int width, int height) {
		this(id, width, height, 0, 0);
	}
	
	public Packet(int id, int width, int height, int x, int y) {
		super(width, height);
		this.id = id;
		this.bottomLeftPoint = new Point(x, y);
		this.rotate = false;
	}
	
	private class Point {
		private int x;
		private int y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
		
		public void setX(int xCord) {
			this.x = xCord;
		}
		
		public void setY(int yCord) {
			this.x = yCord;
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getPointX() {
		return this.bottomLeftPoint.getX();
	}
	
	public int getPointY() {
		return this.bottomLeftPoint.getY();
	}
	
	public void setPoint(int x, int y) {
		if (x<0 || y<0)
			throw new IllegalArgumentException();
		
		this.bottomLeftPoint.setX(x);
		this.bottomLeftPoint.setY(y);
	}
	
	public boolean isRotate() {
		return this.rotate;
	}
	
	public void setRotate(boolean rot) {
		this.rotate = rot;
	}

	public boolean equals(Object p) {
		if (!(p instanceof Packet))
			return false;
		
		Packet pac = (Packet) p;
		return this.id == pac.getId();
	}
}