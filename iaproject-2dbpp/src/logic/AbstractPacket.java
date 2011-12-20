package logic;

import java.awt.Dimension;

public abstract class AbstractPacket {

	private int width;
	private int height;
	
	public AbstractPacket (int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public Dimension getSize() {
		return new Dimension(this.width, this.getHeight());
	}
}
