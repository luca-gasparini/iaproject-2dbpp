package gui;

import java.awt.Color;
import java.awt.Rectangle;
//import java.awt.Point;
import java.awt.Dimension;

import logic.Packet;

public class GUIPacket extends Packet {

	private final Color color; // BLUE default color (solo per debug)
	private Rectangle rect;
	
	public GUIPacket(int id, int width, int height) {
		this(id, width, height, Color.BLUE, 0, 0);
	}
	
	public GUIPacket(int id, int width, int height, Color color) {
		this(id, width, height, color, 0, 0);
	}
	
	public GUIPacket(int id, int width, int height, int x, int y) {
		this(id, width, height, Color.BLUE, x, y);
	}
	
	public GUIPacket(int id, int width, int height, Color color, int x, int y) {
		super(id, width, height, x, y);
		this.color = color;
		this.rect = null;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Rectangle getRectangle() {
		
		//Point p = new Point(this.getPointX(), getPointY()+this.getHeight());
		Dimension d = null;
		
		if (!(this.isRotate()))
			d = new Dimension(this.getWidth(), this.getHeight());
		else
			d = new Dimension(this.getHeight(), this.getWidth());
		
		rect = new Rectangle(d);
		return rect;
	}
	
}
