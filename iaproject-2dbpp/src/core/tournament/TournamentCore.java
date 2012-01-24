package core.tournament;

import gui.OptimumPainter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import logic.Bin;
import logic.BinConfiguration;
import logic.ManageSolution;
import logic.Packet;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;
import core.genetic.Individual;

public class TournamentCore extends AbstractCore<TournamentConfiguration, List<Bin>> {
	
	private static final int N_TO_STOP = 1000; // stopping cond on iteration
	private static final float PERC_TO_STOP = 0.95f; // stopping cond on equals individual
	// core configuration fields
	private final int populationSize;
	private final float pRotateMutation;
	private final float pSwapMutation;
	private final float pOrderMutation;
	private final float pCrossover;
	private final float alpha;
	private final float beta;
	private final int eliteSize;
	private final int tournamentSize;
	private final int tournamentsNumber;
	private final boolean intelligentStart;
	
	// problem fields
	private final BinConfiguration binsDim;
	private final List<Packet> packetList;
	
	// core vars
	private Population population;
	private Individual bestIndividual;
	private float currentFitness;
	private final Random rand = new Random(System.currentTimeMillis());
	
	public TournamentCore(CoreConfiguration<TournamentConfiguration> conf, OptimumPainter painter) {
		
		super(conf, painter, Core2GuiTranslators.getBinListTranslator());
		
		// get core configuration
		this.populationSize = conf.getCoreConfiguration().getPopulationSize();
		this.pRotateMutation = conf.getCoreConfiguration().getRotateMutationProbability();
		this.pSwapMutation = conf.getCoreConfiguration().getSwapMutationProbability();
		this.pOrderMutation = conf.getCoreConfiguration().getOrderMutationProbability();
		this.pCrossover = conf.getCoreConfiguration().getCrossoverProbability();
		this.alpha = conf.getCoreConfiguration().getAlpha();
		this.beta = conf.getCoreConfiguration().getBeta();
		this.eliteSize = conf.getCoreConfiguration().getEliteSize();
		this.tournamentSize = conf.getCoreConfiguration().getTournamentSize();
		this.tournamentsNumber = populationSize - eliteSize;
		this.intelligentStart = conf.getCoreConfiguration().getIntelligentStart();

		
		// get problem configuration
		this.binsDim = conf.getProblemConfiguration().getBin();
		this.packetList = ManageSolution.buildPacketList(
				conf.getProblemConfiguration().getPackets() , binsDim );
		
		// initialize core
		population = new Population(populationSize,packetList,binsDim,alpha,beta,intelligentStart);
		this.bestIndividual = population.getBest();
		this.currentFitness = Float.MAX_VALUE;
	}


	@Override
	protected void doWork() {
		
		// controlled cycling
		while (this.canContinue()) {
/*			puro shuffle		
			for( int i=0; i< populationSize; i++) {
				population.get(i).shuffleGenome();
				population.get(i).calculateLayout(binsDim, alpha, beta);
			}
*/
		
			// selection
			List<Individual> matingPool =
					population.tournamentSelection(tournamentSize,tournamentsNumber);
/*			System.out.println("SELECTION... MATING POOL = ");
			for (int i=0; i<matingPool.size(); i++) {
				System.out.println( i + ": " + matingPool.get(i) );
			}
*/			
			// crossover
			List<Individual> offspringPool = crossover( matingPool );
/*			System.out.println("CROSSOVER... OFFSPRING POOL = ");
			for (int i=0; i<offspringPool.size(); i++) {
				System.out.println( i + ": " + offspringPool.get(i) );
			}
*/			
			// mutation
			mutate( offspringPool );
/*			System.out.println("MUTATION... MUTATED OFFSPRING POOL = ");
			for (int i=0; i<offspringPool.size(); i++) {
				System.out.println( i + ": " + offspringPool.get(i) );
			}
*/			
			// make new generation	
			population.replace( offspringPool, eliteSize );
/*			System.out.println("MAKING NEW GENERATION... NEW POPULATION = ");
			System.out.println(population);
*/					
			// find best individual in new population
			bestIndividual = population.getBest();
/*			System.out.println("BEST INDIVIDUAL");
			System.out.println(bestIndividual);
*/			
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



	// modified Partially Matched Crossover to make a crossover in O(n)
	private List<Individual> crossover( List<Individual> matingPool ) {
		
		List<Individual> offspringPool = new ArrayList<Individual>();
//		System.out.println("mating pool size=" + matingPool.size());
		int i = 0;
		// repeat crossover until the offspringPool has >= 
		// matingPool.size() individual adding 2 at each iteration
		while (i < matingPool.size()) {
			
			int dadIdx = rand.nextInt(matingPool.size());
			int momIdx = rand.nextInt(matingPool.size());
			Individual dad = matingPool.get(dadIdx);
			Individual mom = matingPool.get(momIdx);
			
			if (rand.nextFloat() < pCrossover) {
				
				int genomeSize = dad.getGenome().size();
				// set up father genome breaking point
				int p = rand.nextInt( genomeSize );
				// set up number of gene to copy from father: at least 1
				int q = rand.nextInt( genomeSize - p) + 1;
				
//				System.out.println("Dad: " + dad + "\nMom: " + mom);
//				System.out.println("p=" + p + "  q=" + q + "\n");
				
//				System.out.println("1° Offspring:");
				offspringPool.add( makeOffspring(dad,mom,p,q) );
//				System.out.println("2° Offspring:");
				offspringPool.add( makeOffspring(mom,dad,p,q) );
//				System.out.println();

			} else {
				offspringPool.add( dad.clone() );
				offspringPool.add( mom.clone() );
			}			
			i += 2;
		}
		return offspringPool;
	}
		
	private Individual makeOffspring(Individual dad, Individual mom, int p,
			int q) {
		
		int genomeSize = dad.getGenome().size();
		List<Packet> childGenome = new ArrayList<Packet>(genomeSize);
		boolean[] isGeneCopied = new boolean[genomeSize];
		for ( int j = 0; j < genomeSize; j++ ) {
			isGeneCopied[j] = false;
		}		
		
		// extract the genome portion of the dad and add it to the head of child genome
		for (Packet dadGene: dad.getGenome().subList(p, p + q )) {
			childGenome.add( dadGene );
			isGeneCopied[dadGene.getId()] = true;
		}

/*		int j=0;
		for (Packet momGene: mom.getGenome()) {
			if ( !isGeneCopied[ momGene.getId() ] ) {
				if (j < p) { // add p genes to the head
					childGenome.add( j, momGene );
					j++;
				} else { // add the remainder to the tail
					childGenome.add( momGene );
				}
			}
		}
*/		
		// improved crossover
		// complete with the genome of the mother
		int j=0;
		// k is the number of gene to add before the copied pattern
		int k = (genomeSize==q)? 0: rand.nextInt(genomeSize - q);
		for (Packet momGene: mom.getGenome()) {
			if ( !isGeneCopied[ momGene.getId() ] ) {
				if (j < k) { // add k genes to the head
					childGenome.add( j, momGene );
					j++;
				} else { // add the others to the tail
					childGenome.add( momGene );
				}
			}
		}
		
		Individual newOffspring = new Individual(childGenome);
		newOffspring.calculateLayout(binsDim, alpha, beta);
//		System.out.println( newOffspring );
		return newOffspring;
	}
	
	private void mutate(List<Individual> offspringPool) {
		
		int offspringPoolSize = offspringPool.size();
		for(int i=0; i<offspringPoolSize; i++){
			// clone the individual...
			Individual mutatedOffspring = offspringPool.get(i).clone();
			// mutate it ...
			mutatedOffspring.mutate(pRotateMutation, pSwapMutation, pOrderMutation);
			mutatedOffspring.calculateLayout(binsDim, alpha, beta);
			// finally append it to the offspringPool
			offspringPool.add(mutatedOffspring);	
		}
		
	}
	
	@Override
	protected boolean reachedStoppingCondition() {
		if (getNIterations()==N_TO_STOP) {
			JOptionPane.showMessageDialog(null, "Core stopped: " + N_TO_STOP + " reached!");
			return true;
		}
		if (population.reachedConvergence(PERC_TO_STOP)) { // PERC_TO_STOP not used now
			JOptionPane.showMessageDialog(null, "Core stopped: convergence reached!");
			return false;
		}
		return false;
	}

}