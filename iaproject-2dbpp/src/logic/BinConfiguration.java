package logic;

import java.awt.Dimension;
import java.io.Serializable;


public class BinConfiguration implements Serializable {
	
	private static final long serialVersionUID = -6405019301884712946L;
	
	private final int width;
	private final int height;
	
	public BinConfiguration(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Dimension getSize() {
		return new Dimension(this.width, this.height);
	}
}
