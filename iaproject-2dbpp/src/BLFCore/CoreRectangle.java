package BLFCore;

public class CoreRectangle {
	Point bottomLeft;
	double width;
	double heigth;
	
	CoreRectangle(double x,double y,double heigth,double width)
	{
		this(new Point(x,y),heigth,width);
	}

	CoreRectangle(Point p,double heigth,double width)
	{
		bottomLeft = p;
		this.heigth = heigth;
		this.width = width;
	}
}
