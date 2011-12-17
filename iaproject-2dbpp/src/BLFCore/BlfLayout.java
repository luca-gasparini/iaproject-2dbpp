package BLFCore;

import java.util.List;

import logic.Bin;

public class BlfLayout {
	private List<Bin> binConfiguration;
	private float fitness;

	BlfLayout(List<Bin> bc,float fit)
	{
		binConfiguration = bc;
		fitness = fit;
	}
	
	public List<Bin> getBins() {
		return binConfiguration;
	}

	public float getFitness() {
		return fitness;
	}
}
