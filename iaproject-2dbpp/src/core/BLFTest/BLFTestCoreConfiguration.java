package core.BLFTest;

import java.io.Serializable;
import java.util.List;

import logic.Packet;

public class BLFTestCoreConfiguration implements Serializable {

	private static final long serialVersionUID = 1064916421235126471L;

	private final boolean isSelected;
	private final List<Packet> packets;
	
	public BLFTestCoreConfiguration(boolean isSelected, List<Packet> packets) {
		this.isSelected = isSelected;
		this.packets = packets;
	}
	
	public BLFTestCoreConfiguration(List<Packet> packets) {
		this(false, packets);
	}

	public boolean isSelected() {
		return isSelected;
	}

	public List<Packet> getPackets() {
		return packets;
	}
	
}
