package core.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;

import BLFCore.BlfLayout;
import BLFCore.PackingProcedures;

// class that rapresent 
public class Individual implements Comparable<Individual>, Cloneable {
	

	private List<Packet> genome;
	private BlfLayout layout;
	private final Random rand = new Random(System.currentTimeMillis());

	public Individual(List<Packet> packetList) {
		this.genome = new ArrayList<Packet>(packetList.size());
		for (Packet gene: packetList) {
			this.genome.add(gene);
		}
		this.layout = null;
	}

	// apply mutation to the individual 
	public void mutate(float pRotate, float pSwap, float pOrder) {

		// rotation based mutation
		for (int i=0; i<genome.size(); i++) {
			if (rand.nextFloat() < pRotate && genome.get(i).isRotatable()) {
				genome.add(i, genome.remove(i).getRotated() );
			}
		}
		
		// swap based mutation
		for (int i=0; i<genome.size(); i++) {
			if (rand.nextFloat() < pSwap) {
				int swapIdx = rand.nextInt( genome.size() );
//				System.out.println("Swap " + i + " with "+ swapIdx + " : "+ this);
				Packet tempGene = genome.get(i);
				genome.set(i, genome.get(swapIdx));
				genome.set(swapIdx, tempGene);
//				System.out.println(" --> " + this);
			}
		}

		
		// order based mutation
		for (int i=0; i<genome.size(); i++) {
			if (rand.nextFloat() < pOrder) {
				int swapIdx = rand.nextInt( genome.size() - 1);
//				System.out.println("Order " + i + " at "+ swapIdx + " : "+ this);
				genome.add( swapIdx, genome.remove(i) );
//				System.out.println(" --> " + this);
			}
		}
		
 
		
		this.layout = null;
	}
	
	// return the fitness of the individual
	public float getFitness() {	
		return this.layout.getFitness();
	}

	// return the layout of the individual
	public List<Bin> getBins() {
		return this.layout.getBins();
	}

	/** 
	 * calculate the layout of the individual according to the BLF
	 * placing function
	 * @return fitness of the related layout
	 * @param binsDim the dimensions of the bins
	 * @param alpha weight of the height value of the worst bin in fitness function 
	 * @param beta weight of the density value of the worst bin in fitness function 
	 */
	public float calculateLayout(BinConfiguration binsDim, float alpha, float beta) {
		this.layout = PackingProcedures.getLayout( this.genome, binsDim, alpha, beta);
		return this.layout.getFitness();
	}

	// return the genome sequence
	public List<Packet> getGenome() {
		return this.genome;
	}

	// make a shuffle of the genome 
	public void shuffleGenome() {
		java.util.Collections.shuffle(this.genome, rand);
	}

	@Override
	public String toString() {
		String s = "";
		for (Packet gene: genome) {
			s = s + gene + " ";
		}
		s = s + "fitness= " + this.getFitness() + " pointer= " + Integer.toHexString(hashCode());
		return s;
	}

	@Override
	public int compareTo(Individual o) {
		if ( this.getFitness() < o.getFitness() ) return -1;
		if ( this.getFitness() > o.getFitness() ) return 1;
		return 0;
	}
	
	@Override
	public Individual clone() {
		Individual clone = new Individual(this.getGenome());
		clone.setLayout(this.getLayout());
		return clone;
	}
	
	// used only in clonation phase
	private void setLayout(BlfLayout newLayout) {
		this.layout = newLayout;
	}

	// used only in clonation phase
	private BlfLayout getLayout() {
		return this.layout;
	}

	@Override
	public boolean equals(Object obj) {
		List<Packet> objSequence = ((Individual)obj).getGenome();
		for (int i=0; i<objSequence.size(); i++) {
			if (!genome.get(i).equals(objSequence.get(i))) return false;
		}
		return true;
	}
}
