package BLFCore;
import java.util.ArrayList;
import java.util.LinkedList;

public class CoreBin {
	ArrayList<CoreRectangle> packets;
	LinkedList<Hole> holes;
	
	CoreBin(double binWidth,double binHeigth)
	{
		packets = new ArrayList<CoreRectangle>();
		holes = new LinkedList<Hole>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(new Point(0,0),new Point(0,binHeigth)));
		edges.add(new Edge(new Point(0,binHeigth),new Point(binWidth,binHeigth)));
		edges.add(new Edge(new Point(binWidth,binHeigth),new Point(binWidth,0)));
		edges.add(new Edge(new Point(binWidth,0),new Point(0,0)));
		holes.add(new Hole(edges));
	}
	

	
	public ArrayList<CoreRectangle> getPackets()
	{
		return packets;
	}
	
	public boolean insertPacket(double width,double heigth)
	{
		ArrayList<Point> app;
		CoreRectangle  rect = new CoreRectangle(new Point(0,0),heigth,width);
		int hole = -1;
		Point p= null;
		for(int i = 0;i< holes.size();i++)
		{
			app = holes.get(i).getCandidates(rect);
			for(int j = 0;j<app.size();j++)
			{
				if(p == null || app.get(j).y < p.y || (app.get(j).y == p.y && app.get(j).x < p.x))
				{
					hole = i;
					p = app.get(j);
				}
			}
		}
		if(p == null)
			return false;
		rect = new CoreRectangle(p,heigth,width);
		packets.add(rect);
		holes.get(hole).updateHoles(rect);
		return true;
	}
}
