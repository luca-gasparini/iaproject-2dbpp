package core.taboo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.Packet;

public class TabooBin {
	
	private final List<Packet> pkts = new ArrayList<Packet>();
	
	public TabooBin() {
	}
	
	public TabooBin(Packet pkt) {
		addPacket(pkt);
	}
	
	public void addPacket(Packet pkt) {
		this.pkts.add(pkt);
	}
	
	public int size() {
		return pkts.size();
	}
	
	public List<Packet> getPackets() {
		return Collections.unmodifiableList(pkts);
	}
	
	public int sumPktAreas() {
		int area = 0;
		for (Packet pkt : pkts) {
			area += pkt.getWidth() * pkt.getHeight();
		}
		
		return area;
	}
	
}
