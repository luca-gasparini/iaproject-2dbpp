package logic;

import java.awt.Color;

import logic.AbstractPacket;

public class PacketDescriptor extends AbstractPacket {

	private static final long serialVersionUID = 8582320694102744871L;
	private final int id;
	private final Color color;
	
	public PacketDescriptor(int id, int width, int height, Color col) {
		super(width, height);
		this.id = id;
		this.color = col;
	}
	
	public int getWidth(boolean rot) {
		return (rot==false)?this.getWidth():this.getHeight(); 
	}
	
	public int getHeight(boolean rot) {
		return this.getWidth(!rot);
	}
	
	public int getId() {
		return this.id;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	@Override
	public boolean equals(Object p) {
		if (!(p instanceof PacketDescriptor))
			return false;
		
		PacketDescriptor pac = (PacketDescriptor) p;
		
		return (
				this.id == pac.getId() &&
				this.color == pac.getColor() &&
				this.getWidth(false) == pac.getWidth(false) &&
				this.getHeight(false) == pac.getHeight(false)
				);
	}
}