package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageSolution {

	public static List<Packet> buildPacketList(List<PacketConfiguration> packetsInfo) {
		
		List<Packet> lps = new ArrayList<Packet>();
		
		int id=0;
		for(int i=0; i < packetsInfo.size(); i++ ) {
			for( int j=0; j < packetsInfo.get(i).getMolteplicity(); j++ ) {
				PacketDescriptor pd = new PacketDescriptor(
						id++,
						packetsInfo.get(i).getWidth(),
						packetsInfo.get(i).getHeight(),
						packetsInfo.get(i).getColor()
						);
				lps.add(new PacketSolution(pd));
			}
		}
		return lps;
	}
	
	/*
	 * METODO MIO (NICOLA C.) PER TEST SU ROTAZIONE 
	 *                   |
	 *                   |      (freccetta xD)
	 *                   \/
	 * 
	 * */
	
	public static List<Packet> buildPacketSolutionTestRotate(List<PacketConfiguration> packetsInfo, boolean torotate) {

		List<Packet> lps = new ArrayList<Packet>();
		boolean rotate = false;
		int id=0;
		for(int i=0; i < packetsInfo.size(); i++ ) {
			for( int j=0; j < packetsInfo.get(i).getMolteplicity(); j++ ) {
				PacketDescriptor pd = new PacketDescriptor(
						id++,
						packetsInfo.get(i).getWidth(),
						packetsInfo.get(i).getHeight(),
						packetsInfo.get(i).getColor()
						);
				if (torotate)
					rotate = !rotate;
				PacketSolution ps = new PacketSolution(pd);
				ps.setRotate(rotate);
				lps.add(ps);
			}
		}
		return lps;
	}
	
		
	public static List<Packet> getNewPacketList(List<Packet> packetList) {
		
		if (packetList.isEmpty())
			return Collections.emptyList();
		
		List<Packet> lps = new ArrayList<Packet>(packetList.size());
		
		for (Packet p: packetList) {
			lps.add(new PacketSolution(p.getPacketDescriptor()));
		}
		return lps;
		
	}
	
	/*
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
	*/
	
}