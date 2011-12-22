package logic;

import java.io.Serializable;
import java.util.List;

public class ProblemConfiguration implements Serializable {
	
	private static final long serialVersionUID = -9102540924261089702L;
	
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
