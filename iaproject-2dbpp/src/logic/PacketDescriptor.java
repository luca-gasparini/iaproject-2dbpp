package logic;

import java.awt.Color;

import logic.AbstractPacket;

public class PacketDescriptor {

	private final int id;
	private final AbstractPacket normal;
	private final AbstractPacket rotate;
	private final Color color;
	
	public PacketDescriptor(int id, int width, int height, Color col) {
		this.id = id;
		normal = new AbstractPacket(width, height){};
		rotate = new AbstractPacket(height, width){};
		this.color = col;
	}
	
	public int getWidth(boolean rot) {
		return (rot==false)?normal.getWidth():rotate.getWidth(); 
	}
	
	public int getHeight(boolean rot) {
		return (rot==false)?normal.getHeight():rotate.getHeight();
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