package logic;

import java.awt.Color;
import java.awt.Point;

public class PacketSolution implements Packet {

	private final PacketDescriptor pac;
	private Point bottomLeftPoint;
	private boolean rotate;
	
	public PacketSolution(PacketDescriptor p) {
		this(p, 0, 0);
	}
	
	public PacketSolution(PacketDescriptor p, int x, int y) {
		this.pac = p;
		this.bottomLeftPoint = new Point(x, y);
		this.rotate = false;
	}

	@Override
	public int getPointX() {
		return this.bottomLeftPoint.x;
	}
	
	@Override
	public int getPointY() {
		return this.bottomLeftPoint.y;
	}
	
	private Point getPoint() {
		return this.bottomLeftPoint;
	}
	
	@Override
	public Color getColor() {
		return pac.getColor();
	}
	
	@Override
	public void setPoint(int x, int y) {
		if (x<0 || y<0)
			throw new IllegalArgumentException();
		
		this.bottomLeftPoint.x = x;
		this.bottomLeftPoint.y = y;
	}
	
	private void setPoint(Point p) {
		this.bottomLeftPoint = p;
	}
	
	@Override
	public int getWidth() {
		return pac.getWidth(rotate);
	}
	
	@Override
	public int getHeight() {
		return pac.getHeight(rotate);
	}
	
	@Override
	public int getId() {
		return pac.getId();
	}
	
	@Override
	public boolean isRotate() {
		return this.rotate;
	}
	
	@Override
	public void setRotate(boolean rot) {
		this.rotate = rot;
	}
	
	@Override
	public PacketSolution clone() {
		PacketSolution n = new PacketSolution(this.pac);
		n.setPoint(this.getPoint());
		n.setRotate(this.isRotate());
		
		return n;
	}
	
	@Override
	public boolean equals(Object p) {
		if (!(p instanceof PacketSolution))
			return false;
   
		PacketSolution app = (PacketSolution) p;
		return (
				this.pac.equals(app) &&
				this.bottomLeftPoint.equals(app.getPoint()) &&
				this.rotate == app.isRotate()
				);
	}
}
