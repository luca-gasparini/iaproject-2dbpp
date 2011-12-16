package core.genetic;

import gui.OptimumPainter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.*;

import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class GeneticCore extends AbstractCore<Integer, List<Bin>> {
	
	private final int populationSize; // per ora immagino sia questo l'unico parametro
	private final ProblemConfiguration problemConf;
	
	private float currentFitness;
	private Individual bestIndividual;
	private Individual[] population;
	
	private final Random rand = new Random(System.currentTimeMillis());
	
	public GeneticCore(CoreConfiguration<Integer> conf, OptimumPainter painter) {
		super(conf, painter, Core2GuiTranslators.getGeneticTranslator());
		this.problemConf = conf.getProblemConfiguration();
		this.populationSize = conf.getCoreConfiguration().intValue();
		this.population = new Individual[this.populationSize];
		
		// initialize population from problem configuration
		for( int i=0 ; i < this.populationSize; i++ ) {
			this.population[i] = new Individual(this.problemConf.getPackets(), this.problemConf.getBin());
		}
		// arrivato qui sono sicuro che ogni individuo della popolazione ha un suo layout blf e una fitness associata
		this.bestIndividual = findBest();
		this.currentFitness = bestIndividual.getFitness();

	}


	@SuppressWarnings("unchecked")
	@Override
	protected void doWork() {
		
		// controlled cycling
		while (this.canContinue()) {
		
			Individual father = selectIndividual();
			Individual mother = selectIndividual();
			Individual child = crossover(father, mother);
			child.mutate();
			float newFitness = child.calculateLayout(problemConf.getBin()); 
			bestIndividual = replaceWorstIndividual(child);

						
			// publish results only if better
			if ( bestIndividual.getFitness() < currentFitness) {
				currentFitness = bestIndividual.getFitness();
				CoreResult<List<Bin>> cr = new CoreResult<List<Bin>>() {
					@Override
					public float getFitness() {
						return bestIndividual.getFitness();
					}
					@Override
					public List<Bin> getBins() {
						return bestIndividual.getBins();
					}
				};
				publish(cr);
			}
		}
		
	}

	// PMX crossover. Select a random breaking point p for the genome and
	// copy q gene to 
	private Individual crossover(Individual father, Individual mother) {
		List<Packet> childGenome = new ArrayList<Packet>();
		
		// set up father genome breaking point
		int p = rand.nextInt() % problemConf.getPackets().size();
		// set up number of gene to copy from father
		int q = rand.nextInt() % ( problemConf.getPackets().size() + 1 ) - p;
		
		// extract the genome portion of the father and add it to the child genome
		for (Packet fatherGene: father.getSequence().subList(p, q)) {
			childGenome.add(new Packet( fatherGene.getId(),
										fatherGene.getWidth(),
										fatherGene.getHeight(),
										fatherGene.getPointX(),
										fatherGene.getPointY(),
										fatherGene.getColor()	));
		}
		
		// complete with the genome of the mother
/*		for (Packet motherGene: mother.getSequence()) {
			
			childGenome.add(new Packet( fatherGene.getId(),
										fatherGene.getWidth(),
										fatherGene.getHeight(),
										fatherGene.getPointX(),
										fatherGene.getPointY(),
										fatherGene.getColor()	));
		}
		
		Individual child = new Individual(father.getSequence());*/
		
		return father; // per ora torno il padre senza fare crossover;
	}

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
	}
	
	private Individual replaceWorstIndividual(Individual child) {
		return null;
	}

	private Individual selectIndividual() {
		return null;
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return false;
	}

}
