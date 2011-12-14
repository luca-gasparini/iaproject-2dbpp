package core;

import gui.GUIBin;

import java.util.Collections;
import java.util.List;


/**
 * Class made up by static methods each of them returning a specific
 * {@link Core2GuiTranslator}
 * 
 * QUI ANDRANNO TUTTI I TRADUTTORI DI CUI NICOLA C. SI OCCUPA
 */
public final class Core2GuiTranslators {
	
	public static final Core2GuiTranslator<Void> getDummyTranslator() {
		return new Core2GuiTranslator<Void>() {
			
			@Override
			public List<GUIBin> translate(Void coreOptimum) {
				return Collections.emptyList();
			}
			
		};
	}
	
	public static final Core2GuiTranslator<Void> getGeneticTranslator() {
		return new Core2GuiTranslator<Void>() {
			
			@Override
			public List<GUIBin> translate(Void coreOptimum) {
				return Collections.emptyList();
			}
			
		};
	}
}
