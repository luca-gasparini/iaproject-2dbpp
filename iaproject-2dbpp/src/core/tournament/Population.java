package core.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import logic.BinConfiguration;
import logic.Packet;

import core.genetic.Individual;

public class Population {
	private List<Individual> population;
	private final int populationSize;
	private final BinConfiguration binsDim;
	private final float alpha;
	private final float beta;
	private final Random rand = new Random(System.currentTimeMillis());

	public Population(int populationSize, List<Packet> packetList,
			BinConfiguration binsDim, float alpha, float beta, boolean intelligentInit) {
		
		this.binsDim = binsDim;
		this.alpha = alpha;
		this.beta = beta;
		this.populationSize = populationSize;
		this.population = new ArrayList<Individual>(populationSize);
		
		Individual newIndividual;
		for( int i=0 ; i < populationSize; i++ ) {
			// make an individual as the sequence comes
			newIndividual = new Individual(packetList);
			if (intelligentInit && i<Individual.N_SORTING_TYPES) {
				// apply some sorting if intelligent init is setted
				newIndividual.orderGenome(i);
			} else {
				// make a shuffle of the packet
				newIndividual.shuffleGenome();				
			}
			newIndividual.calculateLayout(binsDim, alpha, beta);
			this.add(newIndividual);
		}
		//System.out.println(this);
	}

	public void add(Individual newIndividual) {
/*		newIndividual.calculateLayout(binsDim, alpha, beta);
		System.out.println(population.size());
		int i=0;
		while (i<population.size()) {
			if (newIndividual.getFitness() <  population.get(i).getFitness()) {
				population.add(i,newIndividual);
				return;
			}
			i++;
		}*/
		population.add(newIndividual);
//		System.out.println("added at index " + population.indexOf(newIndividual) + ": " + newIndividual);
//		System.out.println(this);
	}


	public Individual getBest() {
		// initialize the first individual as best 
		Individual best = population.get(0);
		// if i find an individual with better fitness i mark it as best
		for ( int i = 1; i < populationSize; i++ ) {
			if ( population.get(i).getFitness() < best.getFitness() ) {
				best = population.get(i);
			}
		}
		return best;
	}

	public List<Individual> tournamentSelection(int tournamentSize,
			int tournamentsNumber) {
		
		List<Individual> selectedIndividuals =
				new ArrayList<Individual>(tournamentsNumber);
		for(int i=0; i<tournamentsNumber; i++) {
			// make a tournament
			int winner = Integer.MAX_VALUE;
			int player;
			for(int j=0; j<tournamentSize; j++) {
				player = rand.nextInt(population.size());
				if (player < winner) winner = player;
			}
			selectedIndividuals.add( population.get(winner) );
		}
		return selectedIndividuals;
	}
	
	@Override
	public String toString() {
		String s = "";
		for (int i=0; i<populationSize; i++) {
			s = s + i + ": " + population.get(i) + "\n";
		}
		return s;
	}

	public void replace(List<Individual> offspringPool, int eliteSize) {
		Collections.sort(population);
		Collections.sort(offspringPool);
		
		for (int i=eliteSize; i<populationSize; i++) {
			population.set(i, offspringPool.get(i-eliteSize) );
		}

	}

	public boolean reachedConvergence(float percToStop) {
/*		int nEqualsIndividual = 1;
		int convergenceBound = (int)(populationSize * percToStop);
*/
		for (int i=1; i<populationSize; i++) {
			if ( !population.get(i-1).equals( population.get(i) ) )
				return false;
		}
		return true;
	}

	// debug method
	public Individual get(int i) {
		return population.get(i);
	}
}
