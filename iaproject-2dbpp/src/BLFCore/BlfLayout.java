package BLFCore;

import java.util.List;

import logic.Bin;

public class BlfLayout {
	private List<Bin> binConfiguration;
	private float fitness;
	private float minBinFitness;

	BlfLayout(List<Bin> bc,float fit,float minB)
	{
		binConfiguration = bc;
		fitness = fit;
		minBinFitness = minB;
	}
	
	public List<Bin> getBins() {
		return binConfiguration;
	}

	public float getFitness() {
		return fitness;
	}
	
	public float getMinBinFitness()
	{
		return minBinFitness;
	}
	
	public int getNumberOfBins()
	{
		return binConfiguration.size();
	}
/*
	// debug method
	public void setFitness(int i) {
		this.fitness = i;
	}
*/
}
