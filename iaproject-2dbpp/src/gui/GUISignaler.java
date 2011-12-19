package gui;

import core.CoreController;

public interface GUISignaler {
	
	public void signalIteration(CoreController cc, int nIteration);
	
	public void signalEnd(CoreController cc);
	
}
