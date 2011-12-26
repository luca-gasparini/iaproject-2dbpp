package gui;

import java.awt.Color;
import java.awt.Point;

import logic.PacketDescriptor;

/**
 * Immutable class
 *
 */
public class GUIPacket {
	
	private final PacketDescriptor pktDesc;
	
	/**
	 * Bottom Left Point
	 */
	private final Point blPoint;
	private final boolean rotate;
	
	public GUIPacket(PacketDescriptor desc, Point blPoint, boolean rotate) {
		if (desc == null) {
			throw new NullPointerException("null descriptor");
		}
		if (blPoint == null) {
			throw new NullPointerException("null point");
		}
		
		this.pktDesc = desc;
		this.blPoint = new Point(blPoint);
		this.rotate = rotate;
	}
	
	public int getID() {
		return pktDesc.getId();
	}
	
	public Color getColor() {
		return pktDesc.getColor();
	}
	
	public int getPointX() {
		return this.blPoint.x;
	}
	
	public int getPointY() {
		return this.blPoint.y;
	}
	
	public int getWidth() {
		return (rotate) ? pktDesc.getHeight() : pktDesc.getWidth();
	}
	
	public int getHeight() {
		return (rotate) ? pktDesc.getWidth() : pktDesc.getHeight();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blPoint == null) ? 0 : blPoint.hashCode());
		result = prime * result + ((pktDesc == null) ? 0 : pktDesc.hashCode());
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
		if (!(obj instanceof GUIPacket)) {
			return false;
		}
		GUIPacket other = (GUIPacket) obj;
		if (!blPoint.equals(other.blPoint)) {
			return false;
		}
		if (!pktDesc.equals(other.pktDesc)) {
			return false;
		}
		if (rotate != other.rotate) {
			return false;
		}
		return true;
	}
	
	
}
