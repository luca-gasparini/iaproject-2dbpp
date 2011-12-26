package logic;

import java.awt.Color;
import java.awt.Point;
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
	
	public PacketDescriptor getPacketDescriptor();
	
	public Point getPoint();
	
	public Packet clone(); 
}