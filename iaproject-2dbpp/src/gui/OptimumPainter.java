package gui;

import core.CoreController;

public interface OptimumPainter {
	
	public void paint(GUIOptimum newOptimum);
	
	public void signalFinish(CoreController cc);
}
