package logic;

import java.util.List;

public class ProblemConfiguration {
	
	private final BinConfiguration bin;
	private final List<PacketConfiguration> packets;
	
	public ProblemConfiguration(BinConfiguration bin,
			List<PacketConfiguration> packets) {
		if (bin == null || packets == null) {
			throw new NullPointerException();
		}
		
		this.bin = bin;
		this.packets = packets;
	}

	public BinConfiguration getBin() {
		return bin;
	}

	public List<PacketConfiguration> getPackets() {
		return packets;
	}

}
