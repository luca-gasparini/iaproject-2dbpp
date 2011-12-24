package gui;

import core.CoreController;

public interface GUISignaler {
	
	public void signalIteration(CoreController cc, int nIteration, long elapsedTime);
	
	public void signalEnd(CoreController cc);
	
	public void signalError(CoreController cc, Throwable t);
}
