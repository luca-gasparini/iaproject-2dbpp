package logic;

import java.awt.Color;


public class PacketConfiguration {
	
	private final int width;
	private final int height;
	private final int molteplicity;
	private final Color color;
	
	public PacketConfiguration(int width, int height, int molteplicity, Color color) {
		if (width <= 0 || height <= 0 || molteplicity < 0) {
			throw new IllegalArgumentException();
		}
		if (color == null) {
			throw new NullPointerException();
		}
		
		this.width = width;
		this.height = height;
		this.molteplicity = molteplicity;
		this.color = color;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMolteplicity() {
		return molteplicity;
	}
	
	public Color getColor() {
		return this.color;
	}
	
}
