package core.genetic;

import java.io.Serializable;

public class GeneticConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8613812705544125287L;
	private Integer populationSize;
	private Integer rotationProbability;
	public GeneticConfiguration(Integer ps, Integer rp) {
		this.populationSize = ps;
		this.rotationProbability = rp;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getRotateProbability() {
		return rotationProbability;
	}

}
