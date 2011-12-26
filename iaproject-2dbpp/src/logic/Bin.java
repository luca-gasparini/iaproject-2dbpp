package logic;

import java.util.LinkedList;
import java.util.List;

public class Bin { // il nome non è il massimo;

	private final BinConfiguration binConf;
	private final int id;
	
	private List<Packet> packetList;
	private float density;
	
	public Bin(BinConfiguration binC, int id) {
		if (binC == null) {
			throw new NullPointerException();
		}
		
		this.binConf = binC;
		packetList = new LinkedList<Packet>();
		this.id = id;
		this.density = 0;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addPacket(Packet p) {
		if (containsPacket(p))
			throw new IllegalArgumentException();
		
		packetList.add(p);
	}
	
	public float getDensity() {
		return this.density;
	}
	
	public void setDensity(float d) {
		this.density = d;
	}
	
	public void deletePacket(Packet p) {
		if (!packetList.remove(p)) {
			throw new IllegalArgumentException("Given packte is not present");
		}
	}
	
	public BinConfiguration getBinConfiguration() {
		return this.binConf;
	}
	
	public boolean containsPacket(Packet p) {
		return packetList.contains(p);
	}
	
	public int getNPackets() {
		return this.packetList.size();
	}
	
	public List<Packet> getList() {
		return this.packetList;
	}
}