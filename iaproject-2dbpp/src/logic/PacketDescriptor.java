package logic;

import java.awt.Color;

import logic.AbstractPacket;

public class PacketDescriptor extends AbstractPacket {

	private static final long serialVersionUID = 8582320694102744871L;
	private final int id;
	private final Color color;
	
	public PacketDescriptor(int id, int width, int height, Color col) {
		super(width, height);
		
		if (col == null) {
			throw new NullPointerException("Null color");
		}
		
		this.id = id;
		this.color = col;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Color getColor() {
		return this.color;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PacketDescriptor)) {
			return false;
		}
		PacketDescriptor other = (PacketDescriptor) obj;
		if (!color.equals(other.color)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}
	
}