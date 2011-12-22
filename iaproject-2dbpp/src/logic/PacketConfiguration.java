package logic;

import java.awt.Color;

import logic.AbstractPacket;

public class PacketConfiguration extends AbstractPacket {
	
	private static final long serialVersionUID = -5887885664050237441L;
	
	private final int molteplicity;
	private final Color color;
	
	public PacketConfiguration(int width, int height, int molteplicity, Color color) {
		super(width, height);
		if (color == null) {
			throw new NullPointerException();
		}
		
		this.molteplicity = molteplicity;
		this.color = color;
	}

	public int getMolteplicity() {
		return molteplicity;
	}
	
	public Color getColor() {
		return this.color;
	}
	
}
