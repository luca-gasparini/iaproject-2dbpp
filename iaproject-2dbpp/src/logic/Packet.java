package logic;

import java.awt.Color;
import java.io.Serializable;

public interface Packet extends Cloneable, Serializable {

	public Color getColor();
	
	public void setPoint(int x, int y);
	
	public int getPointX();
	
	public int getPointY();
	
	public int getWidth();
	
	public int getHeight();
	
	public int getId();
	
	public boolean isRotate();
	
	public void setRotate(boolean rot);
	
	public Packet clone();
	
	@Override
	public boolean equals(Object p);
	
}