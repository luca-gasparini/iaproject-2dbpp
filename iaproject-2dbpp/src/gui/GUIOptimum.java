package gui;

import java.util.List;


public interface GUIOptimum {
	
	public int getNIterations();
	
	public float getFitness();
	
	public long getElapsedTime();
	
	public List<GUIBin> getBins();
	
}
