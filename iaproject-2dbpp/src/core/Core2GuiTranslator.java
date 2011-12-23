package core;

import gui.GUIBin;

import java.util.List;

public interface Core2GuiTranslator<T> {
	
	/**
	 * Perform an immutable (that's why COPY) translation (that's why TRANSLATE) 
	 * of the given parameter to a list of {@link GUIBin}.
	 * 
	 * @param coreOptimum
	 * @return
	 */
	public List<GUIBin> copyAndTranslate(T coreOptimum);
	
}
