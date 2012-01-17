package core.genetic;

import gui.OptimumPainter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.BinConfiguration;
import logic.ManageSolution;
import logic.Packet;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class GeneticCore extends AbstractCore<GeneticConfiguration, List<Bin>> {
	
	// core configuration fields
	private final int populationSize;
	private final float pRotateMutation;
	private final float pOrderMutation;
	private final float pCrossover;
	private final float alpha;
	private final float beta;
	// problem fields
	private final BinConfiguration binsDim;
	private final List<Packet> packetList;
	// core vars
	private Individual[] population;
	private Individual bestIndividual;
	private float currentFitness;
	private final Random rand = new Random(System.currentTimeMillis());
	
	public GeneticCore(CoreConfiguration<GeneticConfiguration> conf, OptimumPainter painter) {
		
		super(conf, painter, Core2GuiTranslators.getBinListTranslator());
		
		// get core configuration
		this.populationSize = conf.getCoreConfiguration().getPopulationSize();
		this.pRotateMutation = conf.getCoreConfiguration().getRotateMutationProbability();
		this.pOrderMutation = conf.getCoreConfiguration().getOrderMutationProbability();
		this.pCrossover = conf.getCoreConfiguration().getCrossoverProbability();
		this.alpha = conf.getCoreConfiguration().getAlpha();
		this.beta = conf.getCoreConfiguration().getBeta();
		
		// get problem configuration
		this.binsDim = conf.getProblemConfiguration().getBin();
		this.packetList = ManageSolution.buildPacketList(
				conf.getProblemConfiguration().getPackets() , binsDim );
		
		// initialize core 
		this.population = new Individual[this.populationSize];
		// the first individual is initialized as the sequence of packets comes
		this.population[0] = new Individual(packetList);
		this.population[0].calculateLayout(binsDim, alpha, beta);
		for( int i=1 ; i < this.populationSize; i++ ) {
			this.population[i] = new Individual(packetList);
			this.population[i].shuffleGenome();
			this.population[i].calculateLayout(binsDim, alpha, beta);
		}
		this.bestIndividual = null;
		this.currentFitness = Float.MAX_VALUE;
	}


	@Override
	protected void doWork() {
		
		// controlled cycling
		while (this.canContinue()) {
		
			Individual father = selectIndividual();
			Individual mother = selectIndividual();
			Individual child = crossover(father, mother, pCrossover);
			child.mutate(pRotateMutation, pOrderMutation);
			child.calculateLayout(binsDim, alpha, beta);
			bestIndividual = replaceWorstIndividual(child);
						
			// publish results only if better
			if ( bestIndividual.getFitness() < currentFitness) {
				currentFitness = bestIndividual.getFitness();
				CoreResult<List<Bin>> cr = new AbstractCoreResult<List<Bin>>() {
					@Override
					public float getFitness() {
						return bestIndividual.getFitness();
					}
					@Override
					public List<Bin> getBins() {
						return bestIndividual.getBins();
					}
				};
				publishResult(cr);
			}
		}
		
	}

	
	private Individual crossover(Individual father, Individual mother, float pCrossover) {
		
		if (rand.nextFloat() < pCrossover) {
	
			int genomeSize = father.getSequence().size();
			List<Packet> childGenome = new ArrayList<Packet>(genomeSize);
			boolean[] isGeneCopied = new boolean[genomeSize];
			for ( int i = 0; i < genomeSize; i++ ) {
				isGeneCopied[i] = false;
			}
			
			
			// set up father genome breaking point
			int p = rand.nextInt( genomeSize );
			// set up number of gene to copy from father
			int q = rand.nextInt( genomeSize + 1 - p);
			
			// extract the genome portion of the father and add it to the child genome
			for (Packet fatherGene: father.getSequence().subList(p, p + q )) {
				childGenome.add( fatherGene );
				isGeneCopied[fatherGene.getId()] = true;
			}
			
			// complete with the genome of the mother
			for (Packet motherGene: mother.getSequence()) {
				if ( !isGeneCopied[ motherGene.getId() ] ) {
					childGenome.add( motherGene );
				}
			}
	
			return new Individual(childGenome);
		}
		return father; 
	}

	/*
	private Individual findBest() {
		// initialize the first individual as best 
		Individual best = population[0];
		// if i find an individual with better fitness i mark it as best
		for ( int i = 1; i < populationSize; i++ ) {
			if ( population[i].getFitness() < best.getFitness() ) {
				best = population[i];
			}
		}
		return best;
	}*/
	
	private Individual replaceWorstIndividual(Individual child) {
		// initialize the first individual as worst 
		int worstIndex = 0;
		int bestIndex = 0;
		// if i find an individual with worse fitness i mark it as worst
		for ( int i = 1; i < populationSize; i++ ) {
			if ( population[i].getFitness() > population[worstIndex].getFitness() ) {
				worstIndex = i;
			}
			if ( population[i].getFitness() < population[bestIndex].getFitness() ) {
				bestIndex = i;
			}
		}
		// if child fitness is better than the worst then replace the worst individual ...
		if (child.getFitness() < population[worstIndex].getFitness()) {
			population[worstIndex] = child;
			// ... and if child fitness is better than the current best update the best
			if (population[worstIndex].getFitness() < population[bestIndex].getFitness()) {
				bestIndex = worstIndex;
			}
		}
		return population[bestIndex];
	}

	// return an individual from the population selecting it
	// according with roulette mechanism
	private Individual selectIndividual() {
		float fitnessSum = 0;
		float[] probSelection = new float[populationSize];
		
		for ( int i = 0; i < populationSize; i++ ) {
			fitnessSum += population[i].getFitness();
		}

		for ( int i = 0; i < populationSize; i++ ) {
			// pi = fi / fitnessSum
			probSelection[i] = population[i].getFitness() / fitnessSum;
		}
		
		// divide [0,1) in subintervals [0,p0),[p0,p1),...,[p(m-1),1)
		// each subinterval represent one of the m individuals.
		// extraxt a random value and select the related individual
		int i = 0; // candidate individual
		float randValue = rand.nextFloat();
		float rightBound = 0;
		
		while ( i < populationSize ) {
			rightBound += probSelection[i];
			if (randValue < rightBound) return population[i];
			i++;
		}
	//	JOptionPane.showMessageDialog(null, "L'ultimo rightBound = " + rightBound + " mentre randValue = " + randValue);
		// rightBound should be < 1 due to approximations. So if randValue is
		// greater than the last rightBound finded, choose the last individual
		return population[populationSize - 1];
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return false;
	}

}
