package logic;

import java.awt.Color;

/**
 * Mutable class!!
 */
public class Packet {
	
	private final PacketDescriptor pac;
	private final boolean isRotatable;
	private final boolean rotate;
	
	private Packet rotatedPkt;
	
	public Packet(PacketDescriptor p, boolean rotated, boolean isRotatable) {
		if (p == null) {
			throw new NullPointerException("PacketDescriptor is null");
		}
		
		this.pac = p;
		this.rotate = rotated;
		this.isRotatable = isRotatable;
	}
	
	/**
	 * Create a rotated packet given the nonRotated one. NEVER flag as public.
	 * @param nonRotated
	 */
	private Packet(Packet nonRotated) {
		assert nonRotated.isRotatable() : "Given packet isn't rotatable";
		
		this.isRotatable = true;
		this.pac = nonRotated.pac;
		this.rotate = !nonRotated.rotate;
		this.rotatedPkt = nonRotated;
	}
	
	public Color getColor() {
		return pac.getColor();
	}
	
	public int getWidth() {
		return isRotate() ? pac.getHeight() : pac.getWidth();
	}
	
	public int getHeight() {
		return isRotate() ? pac.getWidth() : pac.getHeight();
	}
	
	public int getId() {
		return pac.getId();
	}
	
	public boolean isRotate() {
		return this.rotate;
	}
	
	public int getArea() {
		return pac.getArea();
	}

	public Packet getRotated() {
		if (!this.isRotatable) {
			throw new IllegalStateException("Packet not rotatable");
		}
		
		if (this.rotatedPkt == null) {
			this.rotatedPkt = new Packet(this);
		}
		
		return this.rotatedPkt;
	}
	
	public PacketDescriptor getPacketDescriptor() {
		return pac;
	}

	public boolean isRotatable() {
		return this.isRotatable;
	}
	
	public String toString() {
		return pac + (rotate ? "R" : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isRotatable ? 1231 : 1237);
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
		if (!(obj instanceof Packet)) {
			return false;
		}
		Packet other = (Packet) obj;
		if (isRotatable != other.isRotatable) {
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

	public int getPerimeter() {
		return 2 * (pac.getHeight() + pac.getWidth());
	}
	
	
}
