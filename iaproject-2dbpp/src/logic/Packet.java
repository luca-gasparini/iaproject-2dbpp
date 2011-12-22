package logic;

import java.awt.Color;
import java.awt.Point;

public class Packet extends AbstractPacket {

	private static final long serialVersionUID = 5442423919170523032L;
	
	private final int id;
	private Point bottomLeftPoint;
	private boolean rotate;
	private final Color color;
	
	public Packet(int id, int width, int height, Color col) {
		this(id, width, height, 0, 0, col);
	}
	
	public Packet(int id, int width, int height, int x, int y, Color col) {
		this(id, width, height, x, y, col, false);
	}
	
	public Packet(int id, int width, int height, int x, int y, Color col, boolean rotate) {
		super(width, height);
		this.id = id;
		this.bottomLeftPoint = new Point(x, y);
		this.rotate = rotate;
		this.color = col;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getPointX() {
		return this.bottomLeftPoint.x;
	}
	
	public int getPointY() {
		return this.bottomLeftPoint.y;
	}
	
	public Point getPoint() {
		return new Point(this.bottomLeftPoint);
	}
	
	public void setPoint(int x, int y) {
		if (x<0 || y<0)
			throw new IllegalArgumentException();
		
		this.bottomLeftPoint.x = x;
		this.bottomLeftPoint.y = y;
	}
	
	public boolean isRotate() {
		return this.rotate;
	}
	
	public void setRotate(boolean rot) {
		this.rotate = rot;
	}

	@Override
	public boolean equals(Object p) {
		if (!(p instanceof Packet))
			return false;
		
		Packet pac = (Packet) p;
		return (
				this.id == pac.getId() &&
				this.color.equals(pac.getColor()) &&
				this.rotate == pac.isRotate() &&
				this.bottomLeftPoint.equals(pac.getPoint())
				);
	}
	
	public Color getColor() {
		return this.color;
	}
		
	public Packet clone() {
		Packet clo = new Packet(this.id, this.getWidth(), this.getHeight(), this.getPointX(), this.getPointY(), this.color);
		clo.setRotate(this.rotate);
		return clo;
	}

	public void rotate() {
		int newWidth = getHeight();
		int newHeight = getWidth();
		setWidth( newWidth );
		setHeight( newHeight );
		setRotate( !isRotate() ); 
		
	}
}