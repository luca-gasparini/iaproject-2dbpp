package BLFCore;
import java.util.ArrayList;
import logic.Packet;

public class CoreBin {
	ArrayList<Packet> packets;
	ArrayList<Hole> holes;
	
	CoreBin(double binWidth,double binHeigth)
	{
		packets = new ArrayList<Packet>();
		holes = new ArrayList<Hole>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(new Point(0,0),new Point(0,binHeigth)));
		edges.add(new Edge(new Point(0,binHeigth),new Point(binWidth,binHeigth)));
		edges.add(new Edge(new Point(binWidth,binHeigth),new Point(binWidth,0)));
		edges.add(new Edge(new Point(binWidth,0),new Point(0,0)));
		holes.add(new Hole(edges));
	}
	
	public ArrayList<Packet> getPackets()
	{
		return packets;
	}
	
	public boolean insertPacket(Packet packet)
	{
		ArrayList<Point> app;
		CoreRectangle  rect = new CoreRectangle(new Point(0,0),packet.getHeight(),packet.getWidth());
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
		
		
		rect = new CoreRectangle(p,packet.getHeight(),packet.getWidth());
		packet.setPoint((int)p.x, (int)p.y);
		packets.add(packet);
		
		//aggiorno gli hole
		ArrayList<Hole> newHoles = holes.get(hole).updateHoles(rect);
		holes.remove(hole);
		holes.addAll(newHoles);
		
		return true;
	}
}
