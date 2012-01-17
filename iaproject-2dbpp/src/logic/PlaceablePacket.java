package logic;

import java.awt.Color;
import java.awt.Point;

public class PlaceablePacket {
	
	private final Packet p;
	private final Point point;
	
	
	public PlaceablePacket(Packet p, Point point) {
		this.p = p;
		this.point = new Point(point);
	}
	
	public PlaceablePacket(Packet p, int x, int y) {
		this(p, new Point(x, y));
	}
	
	public Packet getPacket() {
		return p;
	}
	
	public PacketDescriptor getPacketDescriptor() {
		return p.getPacketDescriptor();
	}
	
	public Color getColor() {
		return p.getColor();
	}


	public int getWidth() {
		return p.getWidth();
	}


	public int getHeight() {
		return p.getHeight();
	}


	public int getId() {
		return p.getId();
	}


	public boolean isRotate() {
		return p.isRotate();
	}


	public Packet getRotated() {
		return p.getRotated();
	}
	
	public int getArea() {
		return p.getArea();
	}

	public int getX() {
		return point.x;
	}
	
	public int getY() {
		return point.y;
	}
	
	public Point getPoint() {
		return new Point(point);
	}
}
