package logic;

import java.awt.Color;
import java.awt.Point;

public class PacketSolution implements Packet {

	private static final long serialVersionUID = -5591654086672849675L;
	
	private final PacketDescriptor pac;
	private Point bottomLeftPoint;
	private boolean rotate;
	
	public PacketSolution(PacketDescriptor p) {
		this(p, 0, 0);
	}
	
	public PacketSolution(PacketDescriptor p, int x, int y) {
		if (p == null) {
			throw new NullPointerException("PacketDescriptor is null");
		}
		
		this.pac = p;
		
		assert x >= 0 : "Negative X coordinate of BottomLeftPoint";
		assert y >= 0 : "Negative Y coordinate of BottomLeftPoint";
		
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
		this.rotate = rot;
	}
	
	@Override
	public PacketSolution clone() {
		PacketSolution n = new PacketSolution(this.pac);
		
		n.bottomLeftPoint = this.bottomLeftPoint;
		n.rotate = this.rotate;
		
		return n;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bottomLeftPoint == null) ? 0 : bottomLeftPoint.hashCode());
		result = prime * result + ((pac == null) ? 0 : pac.hashCode());
		result = prime * result + (rotate ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PacketSolution)) {
			return false;
		}
		PacketSolution other = (PacketSolution) obj;
		if (!bottomLeftPoint.equals(other.bottomLeftPoint)) {
			return false;
		}
		if (!pac.equals(other.pac)) {
			return false;
		}
		if (rotate != other.rotate) {
			return false;
		}
		return true;
	}
	
	
}
