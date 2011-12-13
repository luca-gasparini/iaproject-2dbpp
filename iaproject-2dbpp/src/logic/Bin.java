package logic;

import java.util.Iterator;
import java.util.LinkedList;

import gui.GUIPacket;

import logic.BinConfiguration;

public class Bin extends BinConfiguration { // il nome non è il massimo;

	private LinkedList<GUIPacket> packetList;
	private final int id;
	
	public Bin(int id, int width, int height) {
		super(width, height);
		packetList = new LinkedList<GUIPacket>();
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addPacket(GUIPacket p) {
		if (packetList.contains(p))
			throw new IllegalArgumentException();
		
		packetList.add(p);
	}
	
	public GUIPacket deletePacket(GUIPacket p) {
		int ind;
		if ((ind=packetList.indexOf(p))==-1)
			throw new IllegalArgumentException();
		
		GUIPacket pac = packetList.get(ind);
		packetList.remove(p);
		return pac;
	}
	
	public boolean containsPacket(GUIPacket p) {
		return packetList.contains(p);
	}
	
	public Iterator<GUIPacket> getIteratorList() {
		return this.packetList.iterator();
	}
	

}