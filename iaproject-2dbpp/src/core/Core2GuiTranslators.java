package core;

import gui.GUIBin;
import gui.GUIPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import logic.Bin;
import logic.PlaceablePacket;

/**
 * Class made up by static methods each of them returning a specific
 * {@link Core2GuiTranslator}
 * 
 * QUI ANDRANNO TUTTI I TRADUTTORI DI CUI NICOLA C. SI OCCUPA
 */
public final class Core2GuiTranslators {
	
	public static final Core2GuiTranslator<List<Bin>> getBinListTranslator() {
		
		return new Core2GuiTranslator<List<Bin>>() {
			
			@Override
			public List<GUIBin> copyAndTranslate(List<Bin> coreOptimum) {
				
				List<GUIBin> guiBinList = new LinkedList<GUIBin>();
				for (Bin itBin: coreOptimum) {
					
					// creates the list of GUIPackets
					List<GUIPacket> guiPkts = new ArrayList<GUIPacket>();
					for (PlaceablePacket pkt : itBin.getPlaceableList()) {
						guiPkts.add(new GUIPacket(pkt.getPacketDescriptor(), 
								pkt.getPoint(), pkt.isRotate()));
					}
					
					guiBinList.add(new GUIBin(itBin.getBinConfiguration(),
							itBin.getID(),
							itBin.getDensity(),
							guiPkts)
					);
				}
				
				return Collections.unmodifiableList(guiBinList);
			}
			
		};
	}

}
