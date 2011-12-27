package BLFCore;

import java.util.ArrayList;
import logic.Packet;

public class CoreBin {
	ArrayList<Packet> packets;
	ArrayList<Hole> holes;
	double heigth;
	Point higherPoint;//higher Point occupied by a packet;

	CoreBin(double binWidth, double binHeigth) {
		packets = new ArrayList<Packet>();
		holes = new ArrayList<Hole>();
		heigth = binHeigth;
		
		// creo i lati del bin
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(new Point(0, 0), new Point(0, binHeigth)));
		edges.add(new Edge(new Point(0, binHeigth), new Point(binWidth,
				binHeigth)));
		edges.add(new Edge(new Point(binWidth, binHeigth), new Point(binWidth,
				0)));
		edges.add(new Edge(new Point(binWidth, 0), new Point(0, 0)));
		
		
		holes.add(new Hole(edges));
		higherPoint = new Point(0,0);
	}

	public ArrayList<Packet> getPackets() {
		return packets;
	}

	// tenta di inserire il pacchetto nel bin, se ci riesce torna true
	public boolean insertPacket(Packet packet) {
		ArrayList<Point> app;
		CoreRectangle rect = new CoreRectangle(new Point(0, 0),
				packet.getHeight(), packet.getWidth());
		int hole = -1;
		Point p = null;
		for (int i = 0; i < holes.size(); i++) {
			app = holes.get(i).getCandidates(rect);
			for (int j = 0; j < app.size(); j++) {
				// mi salvo la posizione pi� bassa tra i candidati
				// a parit� di altezza privilegio la leftmost
				if (p == null || app.get(j).y < p.y
						|| (app.get(j).y == p.y && app.get(j).x < p.x)) {
					hole = i;
					p = app.get(j);
				}
			}
		}
		if (p == null)
			return false;

		rect = new CoreRectangle(p, packet.getHeight(), packet.getWidth());
		packet.setPoint((int) p.x, (int) p.y);
		
		packets.add(packet);
		
		if(p.y + packet.getHeight() > higherPoint.y)
		{
			higherPoint = new Point(0,p.y + packet.getHeight());
		}

		// aggiorno gli hole
		ArrayList<Hole> newHoles = holes.get(hole).updateHoles(rect);
		holes.remove(hole);// rimuovo l'hole nel quale ho inserito il packet e
							// inserisco gli hole da esso creati
		holes.addAll(newHoles);

		return true;
	}
	
	public double getFitness()
	{
		return 100 * higherPoint.y/heigth;
	}
	
}
