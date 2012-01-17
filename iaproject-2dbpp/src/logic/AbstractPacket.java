package logic;

import java.awt.Dimension;
import java.io.Serializable;

public abstract class AbstractPacket implements Serializable {

	private static final long serialVersionUID = -3719533933391235589L;
	private final int width;
	private final int height;
	
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
	
	public int getArea() {
		return this.height * this.width;
	}
	
	public Dimension getSize() {
		return new Dimension(this.width, this.getHeight());
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
		if (!(obj instanceof AbstractPacket)) {
			return false;
		}
		AbstractPacket other = (AbstractPacket) obj;
		if (height != other.height) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "W"+width+"_H"+height;
	}
	
}