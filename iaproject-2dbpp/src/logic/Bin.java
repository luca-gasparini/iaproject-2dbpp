package logic;

import java.util.LinkedList;
import java.util.List;

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
	
	public int getNPackets() {
		return this.packetList.size();
	}
	
	public List<Packet> getList() {
		return this.packetList;
	}
	
	@Override
	public boolean equals(Object b) {
		if (b == null) {
			return false;
		}
		
		if (this == b) {
			return true;
		}
		
		if (!(b instanceof Bin))
			return false;
		
		Bin binCompare = (Bin) b;
		
		if (this.id != binCompare.id) {
			return false;
		}
		
		List<Packet> itPacket = binCompare.packetList;
		
		// to check if both list are equals, first check their size...
		if (this.packetList.size() != itPacket.size()) {
			return false;
		}
		
		// ...then check if both contain the same packets
		for (Packet p: itPacket) {
			if (!this.containsPacket(p))
				return false;
		}
		return true;
	}
}