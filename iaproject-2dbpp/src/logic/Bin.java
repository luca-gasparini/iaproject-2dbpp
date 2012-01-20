package logic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Bin { // il nome non è il massimo;

	private final BinConfiguration binConf;
	private int id;
	private final List<PlaceablePacket> placeablePktList;
	private final List<Packet> packetList;
	
	private int occupiedArea;
	
	public Bin(BinConfiguration binC, int id) {
		if (binC == null) {
			throw new NullPointerException();
		}
		
		this.binConf = binC;
		placeablePktList = new LinkedList<PlaceablePacket>();
		packetList = new LinkedList<Packet>();
		this.id = id;
		this.occupiedArea = 0;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addPacket(PlaceablePacket p) {
		placeablePktList.add(p);
		packetList.add(p.getPacket());
		this.occupiedArea += p.getArea();
	}
	
	public float getDensity() {
		return ((float) this.occupiedArea) / binConf.getArea();
	}
	
	public BinConfiguration getBinConfiguration() {
		return this.binConf;
	}
	
	public int getNPackets() {
		return this.placeablePktList.size();
	}
	
	public List<PlaceablePacket> getPlaceableList() {
		return Collections.unmodifiableList(this.placeablePktList);
	}
	
	public List<Packet> getPacketList() {
		return Collections.unmodifiableList(this.packetList);
	}

	public void setID(int i) {
		this.id = i;
	}
}