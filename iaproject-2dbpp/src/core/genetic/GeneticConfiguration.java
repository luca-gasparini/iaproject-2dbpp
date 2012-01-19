package core.genetic;

import java.io.Serializable;

public class GeneticConfiguration implements Serializable {

	private static final long serialVersionUID = 8613812705544125287L;
	private Integer populationSize;
	private Float pRotateMutation;
	private Float pSwapMutation;
	private Float pOrderMutation;
	private Float pCrossover;
	private Float alpha;
	private Float beta;
	

	
	public GeneticConfiguration(Integer ps, Float rp, Float sp, Float op, Float cp,Float a,Float b) {
		this.populationSize = ps;
		this.pRotateMutation = rp;
		this.pSwapMutation = sp;
		this.pOrderMutation = op;
		this.pCrossover = cp;
		this.alpha = a;
		this.beta = b;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public float getRotateMutationProbability() {
		return pRotateMutation;
	}
	
	public Float getSwapMutationProbability() {
		return pSwapMutation;
	}

	public float getOrderMutationProbability() {
		return pOrderMutation;
	}

	public float getCrossoverProbability() {
		return pCrossover;
	}

	public float getAlpha() {
		return alpha;
	}
	
	public float getBeta() {
		return beta;
	}
}
