package core.genetic;

import java.io.Serializable;

public class GeneticConfiguration implements Serializable {

	private static final long serialVersionUID = 8613812705544125287L;
	private Integer populationSize;
	private Float pRotateMutation;
	private Float pOrderMutation;
	private Float pCrossover;

	
	public GeneticConfiguration(Integer ps, Float rp, Float op, Float cp) {
		this.populationSize = ps;
		this.pRotateMutation = rp;
		this.pOrderMutation = op;
		this.pCrossover = cp;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public float getRotateMutationProbability() {
		return pRotateMutation;
	}

	public float getOrderMutationProbability() {
		return pOrderMutation;
	}

	public float getCrossoverProbability() {
		return pCrossover;
	}

}
