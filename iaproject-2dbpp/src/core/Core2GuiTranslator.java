package core;

import gui.GUIBin;

import java.util.List;

public interface Core2GuiTranslator<T> {
	
	public List<GUIBin> translate(T coreOptimum);
	
}
