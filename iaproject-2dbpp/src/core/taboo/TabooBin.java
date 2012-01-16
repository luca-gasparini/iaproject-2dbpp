package core.taboo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import logic.Packet;

public class TabooBin {
	
	private final List<Packet> pkts = new ArrayList<Packet>();
	private int area = 0;
	
	public TabooBin() {
		this(Collections.<Packet>emptyList());
	}
	
	public TabooBin(final Collection<? extends Packet> packets) {
		for (Packet packet : packets) {
			this.addPacket(packet);
		}
	}
	
	public TabooBin(Packet pkt) {
		this(Collections.singletonList(pkt));
	}
	
	public void addPacket(Packet pkt) {
		this.pkts.add(pkt);
		this.area += pkt.getWidth() * pkt.getHeight();
	}
	
	public int size() {
		return pkts.size();
	}
	
	public List<Packet> getPackets() {
		return Collections.unmodifiableList(pkts);
	}
	
	public int sumPktAreas() {
		return area;
	}
	
}
