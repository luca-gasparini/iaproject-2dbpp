package core.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;

import BLFCore.BlfLayout;
import BLFCore.PackingProcedures;

public class Individual implements Comparable<Individual>, Cloneable {
	

	private List<Packet> sequence;
	private BlfLayout layout;
	private final Random rand = new Random(System.currentTimeMillis());

	public Individual(List<Packet> packetList) {
		this.sequence = new ArrayList<Packet>(packetList.size());
		for (Packet gene: packetList) {
			this.sequence.add(gene);
		}
		this.layout = null;
	}

	// apply mutation to the individual 
	public void mutate(float pRotate, float pOrder) {

		// rotation based mutation
		for (int i=0; i<sequence.size(); i++) {
			if (rand.nextFloat() < pRotate && sequence.get(i).isRotatable()) {
				sequence.add(i, sequence.remove(i).getRotated() );
			}
		}
		
		// order based mutation
		for (int i=0; i<sequence.size(); i++) {
			if (rand.nextFloat() < pOrder) {
				int geneIndex1 = i;
				int geneIndex2 = rand.nextInt( sequence.size() );
				Packet tempGene = sequence.get(geneIndex1);
				sequence.set(geneIndex1, sequence.get(geneIndex2));
				sequence.set(geneIndex2, tempGene);
			}
		}
		this.layout = null;
	}
	
	// return the fitness of the individual
	public float getFitness() {	
		return this.layout.getFitness();
	}

	public List<Bin> getBins() {
		return this.layout.getBins();
	}

	public float calculateLayout(BinConfiguration binsDim, float alpha, float beta) {
		this.layout = PackingProcedures.getLayout( this.sequence, binsDim, alpha, beta);
		return this.layout.getFitness();
	}

	public List<Packet> getSequence() {
		return this.sequence;
	}

	public void shuffleGenome() {
		java.util.Collections.shuffle(this.sequence, rand);
	}

	@Override
	public String toString() {
		String s = "";
		for (Packet gene: sequence) {
			s = s + gene.getId() + " ";
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
		Individual clone = new Individual(this.getSequence());
		clone.setLayout(this.getLayout());
		return clone;
	}

	// metodo usato solo per la clonazione
	private void setLayout(BlfLayout newLayout) {
		this.layout = newLayout;
	}

	// metodo usato solo per la clonazione
	private BlfLayout getLayout() {
		return this.layout;
	}

	@Override
	public boolean equals(Object obj) {
		List<Packet> objSequence = ((Individual)obj).getSequence();
		for (int i=0; i<objSequence.size(); i++) {
			if (sequence.get(i)!=objSequence.get(i)) return false;
		}
		return false;
	}
}
