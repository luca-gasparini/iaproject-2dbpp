package core.tournament;

import java.io.Serializable;

public class TournamentConfiguration implements Serializable {

	private static final long serialVersionUID = 8613812705544125287L;
	private Integer populationSize;
	private Float pRotateMutation;
	private Float pSwapMutation;
	private Float pOrderMutation;
	private Float pCrossover;
	private Float alpha;
	private Float beta;
	private Integer eliteSize;
	private Integer tournamentSize;
	private Boolean intelligentStart;

	
	public TournamentConfiguration(Integer ps, Float rp, Float sp, Float op, Float cp, Float a,Float b, Integer es, Integer ts, Boolean is) {
		this.populationSize = ps;
		this.pRotateMutation = rp;
		this.pSwapMutation = sp;
		this.pOrderMutation = op;
		this.pCrossover = cp;
		this.alpha = a;
		this.beta = b;
		this.eliteSize = es;
		this.tournamentSize = ts;
		this.intelligentStart = is;
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
	
	public int getEliteSize() {
		return eliteSize;
	}

	public int getTournamentSize() {
		return tournamentSize;
	}

	public boolean getIntelligentStart() {
		return intelligentStart;
	}
	
}
