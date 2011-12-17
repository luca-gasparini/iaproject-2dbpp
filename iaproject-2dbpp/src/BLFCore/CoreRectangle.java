package BLFCore;

import java.util.ArrayList;

public class CoreRectangle {
	Point bottomLeft;
	double width;
	double heigth;
	
	private ArrayList<Edge> rectEdges;
	
	CoreRectangle(double x,double y,double heigth,double width)
	{
		this(new Point(x,y),heigth,width);
	}
	
	CoreRectangle(Point p,double heigth,double width)
	{
		bottomLeft = p;
		this.heigth = heigth;
		this.width = width;
		
		rectEdges = new ArrayList<Edge>();
		
		Point upLeft = new Point(bottomLeft.x,bottomLeft.y + heigth);
		Point upRigth = new Point(bottomLeft.x + width,bottomLeft.y + heigth);
		Point bottomRigth = new Point(bottomLeft.x + width,bottomLeft.y);
		
		rectEdges.add(new Edge(bottomLeft,upLeft));
		rectEdges.add(new Edge(upLeft,upRigth));
		rectEdges.add(new Edge(upRigth,bottomRigth));
		rectEdges.add(new Edge(bottomRigth,bottomLeft));
	}
	
	public Edge getEdge(int index)
	{
		while(index < 0)
			index = index+rectEdges.size();
		return rectEdges.get(index % rectEdges.size());
	}
}
