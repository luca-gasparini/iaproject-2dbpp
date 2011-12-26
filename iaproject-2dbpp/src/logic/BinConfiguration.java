package logic;

import java.awt.Dimension;
import java.io.Serializable;

/**
 * Immutable class holding bin width & height
 *
 */
public final class BinConfiguration implements Serializable {
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
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
		if (!(obj instanceof BinConfiguration)) {
			return false;
		}
		BinConfiguration other = (BinConfiguration) obj;
		if (height != other.height) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}
	
	
}
