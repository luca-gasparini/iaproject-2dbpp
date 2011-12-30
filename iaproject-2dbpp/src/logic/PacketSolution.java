package logic;

import java.awt.Color;
import java.awt.Point;

/**
 * Mutable class!!
 */
public class PacketSolution implements Packet {

	private static final long serialVersionUID = -5591654086672849675L;
	
	private final PacketDescriptor pac;
	private final boolean isRotatable;
	
	private Point bottomLeftPoint;
	private boolean rotate;
	
	public PacketSolution(PacketDescriptor p, boolean rotated, boolean isRotatable) {
		this(p, 0, 0, rotated, isRotatable);
	}
	
	public PacketSolution(PacketDescriptor p, int x, int y, boolean rotated, boolean isRotatable) {
		if (p == null) {
			throw new NullPointerException("PacketDescriptor is null");
		}
		
		this.pac = p;
		
		assert x >= 0 : "Negative X coordinate of BottomLeftPoint";
		assert y >= 0 : "Negative Y coordinate of BottomLeftPoint";
		
		this.bottomLeftPoint = new Point(x, y);
		this.rotate = rotated;
		this.isRotatable = isRotatable;
	}

	@Override
	public int getPointX() {
		return this.bottomLeftPoint.x;
	}
	
	@Override
	public int getPointY() {
		return this.bottomLeftPoint.y;
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
	
	@Override
	public int getWidth() {
		return isRotate() ? pac.getHeight() : pac.getWidth();
	}
	
	@Override
	public int getHeight() {
		return isRotate() ? pac.getWidth() : pac.getHeight();
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
		if (!this.isRotatable()) {
			throw new IllegalStateException("This packet is non-rotatable");
		}
		
		this.rotate = rot;
	}
	
	@Override
	public PacketDescriptor getPacketDescriptor() {
		return pac;
	}

	@Override
	public Point getPoint() {
		return new Point(this.bottomLeftPoint);
	}
	
	public Packet clone() {
		PacketSolution ps = new PacketSolution(this.pac, 
				this.getPointX(), this.getPointY(),
				this.rotate, this.isRotatable);
		return ps;
	}

	@Override
	public boolean isRotatable() {
		return this.isRotatable;
	}
}
