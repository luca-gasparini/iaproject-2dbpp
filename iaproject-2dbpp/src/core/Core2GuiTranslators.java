package core;

import gui.GUIBin;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import logic.Bin;

/**
 * Class made up by static methods each of them returning a specific
 * {@link Core2GuiTranslator}
 * 
 * QUI ANDRANNO TUTTI I TRADUTTORI DI CUI NICOLA C. SI OCCUPA
 */
public final class Core2GuiTranslators {
	
	public static final Core2GuiTranslator<List<Bin>> getDummyTranslator() {
		return new Core2GuiTranslator<List<Bin>>() {
			// modificato per fare test sul disegno con core Dummy
			@Override
			public List<GUIBin> translate(List<Bin> coreOptimum) {
				
				List<GUIBin> guiBinList = new LinkedList<GUIBin>();
				Iterator<Bin> itBin = coreOptimum.iterator();
				
				while (itBin.hasNext()) {
					guiBinList.add(new GUIBin(itBin.next()));
				}
				
				return guiBinList;
			}
			
		};
	}
	
	public static final Core2GuiTranslator<List<Bin>> getGeneticTranslator() {
		return new Core2GuiTranslator<List<Bin>>() {
			
			@Override
			public List<GUIBin> translate(List<Bin> coreOptimum) {
				
				List<GUIBin> guiBinList = new LinkedList<GUIBin>();
				Iterator<Bin> itBin = coreOptimum.iterator();
				
				while (itBin.hasNext()) {
					guiBinList.add(new GUIBin(itBin.next()));
				}
				
				return guiBinList;
			}
			
		};
		
	}
}
