package logic;

import java.util.ArrayList;
import java.util.List;

public class ManageSolution {

	public static List<PacketDescriptor> buildPacketList(List<PacketConfiguration> packetsInfo) {
		
		List<PacketDescriptor> lp = new ArrayList<PacketDescriptor>();
		
		int id=0;
		for(int i=0; i < packetsInfo.size(); i++ ) {
			for( int j=0; j < packetsInfo.get(i).getMolteplicity(); j++ ) {
				lp.add(new PacketDescriptor(
						id++,
						packetsInfo.get(i).getWidth(),
						packetsInfo.get(i).getHeight(),
						packetsInfo.get(i).getColor())
						);
			}
		}
		return lp;
	}
	
	public static List<Packet> buildPacketSolution(List<PacketDescriptor> packets) {
		
		List<Packet> lps = new ArrayList<Packet>();
		
		for (PacketDescriptor p: packets) {
			lps.add(new PacketSolution(p));
		}
		
		return lps;
	}
	
	public static List<Packet> buildPacketSolutionTestRotate(List<PacketDescriptor> packets, boolean torotate) {
		
		List<Packet> lps = new ArrayList<Packet>();
		boolean rotate = false;
		for (PacketDescriptor p: packets) {
			PacketSolution app = new PacketSolution(p);
			app.setRotate(rotate);
			lps.add(app);
			if (torotate)
				rotate = !rotate;
		}
		
		return lps;
	}
}
