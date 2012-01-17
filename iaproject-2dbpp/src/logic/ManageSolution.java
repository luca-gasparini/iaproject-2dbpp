package logic;

import java.util.ArrayList;
import java.util.List;
import core.DataParsingException;

public class ManageSolution {

	public static List<Packet> buildPacketList(List<PacketConfiguration> packetsInfo, BinConfiguration bin) {
		
		List<Packet> lps = new ArrayList<Packet>();
		
		int id=0;
		for(int i=0; i < packetsInfo.size(); i++ ) {
			int j=0;
			RotationInfo rotInfo = null;
			
			try {
				rotInfo = ManageSolution.canRotate(packetsInfo.get(i), bin);
			} catch (DataParsingException ex) {
				// jump to the next packet (exception unreachable)
				j = packetsInfo.get(i).getMolteplicity();
			}
			
			for(; j < packetsInfo.get(i).getMolteplicity(); j++ ) {
				PacketDescriptor pd = new PacketDescriptor(
						id++,
						packetsInfo.get(i).getWidth(),
						packetsInfo.get(i).getHeight(),
						packetsInfo.get(i).getColor()
						);
				
				lps.add(new Packet(pd, rotInfo.mustBeRotated, rotInfo.canRotate));
			}
		}
		return lps;
	}
	
	private static class RotationInfo {
		private boolean mustBeRotated;
		private boolean canRotate;
		
		public RotationInfo(boolean mustBeRotated, boolean canRotate) {
			this.mustBeRotated = mustBeRotated;
			this.canRotate = canRotate;
		}
	}
	
	private static RotationInfo canRotate(PacketConfiguration p, BinConfiguration bin) throws DataParsingException {
		
		int tot = 0;
		RotationInfo rotInf = null;
		
		if ((p.getWidth() <= bin.getWidth()) && (p.getHeight()<= bin.getHeight())) tot+=1;
		if ((p.getWidth() <= bin.getHeight()) && (p.getHeight()<= bin.getWidth())) tot+=2;
		
		switch (tot) {
			case 0:
				throw new DataParsingException("Packet too big");
			case 1:
				rotInf = new RotationInfo(false, false);
				break;
			case 2:
				rotInf = new RotationInfo(true, false);
				break;
			case 3:
				rotInf = new RotationInfo(false, true);
				break;
		}
		
		return rotInf;
	}
	
	public static boolean canInsert(PacketConfiguration p, BinConfiguration bin) {
		try {
			ManageSolution.canRotate(p, bin);
		} catch (DataParsingException e) {
			return false;
		}
		return true;
	}
	
	
	/*
	 * metodo per test su rotazione
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
				Packet ps = new Packet(pd, true, true);
				lps.add(ps.getRotated());
			}
		}
		return lps;
	}
	
		
//	public static List<Packet> getNewPacketList(List<Packet> packetList) {
//		
//		if (packetList.isEmpty())
//			return Collections.emptyList();
//		
//		List<Packet> lps = new ArrayList<Packet>(packetList.size());
//		
//		for (Packet p: packetList) {
//			lps.add(new PacketSolution(p.getPacketDescriptor()));
//		}
//		return lps;
//		
//	}
	
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