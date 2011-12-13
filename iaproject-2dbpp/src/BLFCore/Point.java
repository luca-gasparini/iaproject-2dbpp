package BLFCore;

public class Point {
	public double x;
	public double y;
	
	Point(double x,double y)
	{
		this.x = x;
		this.y = y;
	}
	
	Point(Point p)
	{
		x = p.x;
		y = p.y;
	}
	
	public static boolean equals(Point p1, Point p2)
	{
		return p1.x == p2.x && p1.y == p2.y;
	}
}
