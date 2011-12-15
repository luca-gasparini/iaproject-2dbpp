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
	
	public Iterator<Packet> getIteratorList() {
		return this.packetList.iterator();
	}
}