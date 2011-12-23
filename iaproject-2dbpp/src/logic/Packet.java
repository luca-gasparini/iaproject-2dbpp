package logic;

import java.awt.Color;

public interface Packet {

	public Color getColor();
	
	public void setPoint(int x, int y);
	
	public int getPointX();
	
	public int getPointY();
	
	public int getWidth();
	
	public int getHeight();
	
	public int getId();
	
	public boolean isRotate();
	
	public void setRotate(boolean rot);
	
	public Object clone();
	
	@Override
	public boolean equals(Object p);
	
}