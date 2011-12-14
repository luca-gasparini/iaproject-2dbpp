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

public class GeneticCore extends AbstractCore<Integer, Void> {
	
	private final int populationSize; // per ora immagino sia questo l'unico parametro
	private final ProblemConfiguration problemConf;
	
	private Individual bestIndividual;
	private Individual[] population;
	
	public GeneticCore(CoreConfiguration<Integer> conf, OptimumPainter painter) {
		super(conf, painter, Core2GuiTranslators.getGeneticTranslator());
		this.problemConf = conf.getProblemConfiguration();
		this.populationSize = conf.getCoreConfiguration().intValue();
		this.bestIndividual = null;
		this.population = new Individual[this.populationSize];
		
		// initialize population from the list of PacketConfiguration
		for( int i=0 ; i < this.populationSize; i++ ) {
			population[i] = new Individual(this.problemConf.getPackets());
//	***		ClasseStaticaUrban.blfPlacement(population[i]);
			population[i].calculateFitness();
		}
		

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
//	***		child.setBLFConf(ClasseStaticaUrban.blfPlacement(child.getSequence());
			child.calculateFitness();
			bestIndividual = replaceWorstIndividual(child);

						
			// publish results
			CoreResult<Void> cr = new CoreResult<Void>() {
				@Override
				public float getFitness() {
					return 0;
				}
				@Override
				public Void getBins() {
					return null;
				}
			};
			publish(cr);
		}
		
	}

	private Individual crossover(Individual father, Individual mother) {
		// TODO Auto-generated method stub
		return null;
	}

	private Individual replaceWorstIndividual(Individual child) {
		// TODO Auto-generated method stub
		return null;
	}

	private Individual selectIndividual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return false;
	}

}
