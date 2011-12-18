package logic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//import gui.GUIPacket;
import logic.Packet;

import logic.BinConfiguration;

public class Bin extends BinConfiguration { // il nome non è il massimo;

	private List<Packet> packetList;
	private final int id;
	
	public Bin(int id, int width, int height) {
		super(width, height);
		packetList = new LinkedList<Packet>();
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addPacket(Packet p) {
		if (packetList.contains(p))
			throw new IllegalArgumentException();
		
		packetList.add(p);
	}
	
	public Packet deletePacket(Packet p) {
		int ind;
		if ((ind=packetList.indexOf(p))==-1)
			throw new IllegalArgumentException();
		
		Packet pac = packetList.get(ind);
		packetList.remove(p);
		return pac;
	}
	
	public boolean containsPacket(Packet p) {
		return packetList.contains(p);
	}
	
	public List<Packet> getList() {
		return this.packetList;
	}
	
	@Override
	public boolean equals(Object b) {
		if (!(b instanceof Bin))
			return false;
		
		Bin binCompare = (Bin) b;
		List<Packet> itPacket = binCompare.getList();
		
		for (Packet p: itPacket) {
			if (!this.containsPacket(p))
				return false;
		}
		return true;
	}
}