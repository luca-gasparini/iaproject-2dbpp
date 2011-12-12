package logic;


public class BinConfiguration {
	
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
	
}
