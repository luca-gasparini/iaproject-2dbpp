package gui;

import java.awt.Color;
import java.awt.Rectangle;

import logic.Packet;

public class GUIPacket extends Packet {

	private final Color color; // BLUE default color (only for debug use)
	private final Rectangle rect;
		
	public GUIPacket(int id, int width, int height) {
		this(id, width, height, Color.BLUE, 0, 0);
	}
	
	public GUIPacket(int id, int width, int height, int x, int y) {
		this(id, width, height, Color.BLUE, x, y);
	}
	
	public GUIPacket(int id, int width, int height, Color color, int x, int y) {
		super(id, width, height, x, y);
		this.color = color;
		this.rect = new Rectangle(this.getWidth(), this.getHeight(), 0, this.getHeight());
	}
	
	@Override
	public void setPoint(int x, int y) {
		super.setPoint(x, y);
		this.rect.setLocation(x, y+this.getHeight());
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Rectangle getRectangle() {
		return this.rect;
	}
	
}
