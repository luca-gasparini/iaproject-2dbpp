package core.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;

import BLFCore.BlfLayout;
import BLFCore.PackingProcedures;

class Individual {
	
	private List<Packet> sequence;
	private BlfLayout layout;
	private final Random rand = new Random(System.currentTimeMillis());

/*	public Individual(List<PacketConfiguration> packetsInfo, BinConfiguration binsInfo) {		
		// translate input from List<PacketConfigutation> to List<Packet>
		this.sequence = ManageSolution.buildPacketList(packetsInfo);
		java.util.Collections.shuffle(this.sequence, rand);
		// calculate blf layout and related fitness of the individual
		this.calculateLayout(binsInfo);
		
	}*/

	public Individual(List<Packet> packetList) {
		this.sequence = new ArrayList<Packet>(packetList.size());
		for (Packet gene: packetList) {
			this.sequence.add(gene.clone());
		}
		this.layout = null;
	}

	// apply mutation to the individual
	public void mutate(float pRotate, float pOrderMutation) {

		// rotation based mutation
		for (Packet gene: sequence) {
			if (rand.nextFloat() < pRotate && gene.isRotatable()) {
				gene.setRotate( !gene.isRotate() );
			}
		}
		
		// order based mutation
		if (rand.nextFloat() < pOrderMutation) {
			int geneIndex1 = rand.nextInt( sequence.size() );
			int geneIndex2 = rand.nextInt( sequence.size() );
			Packet tempGene = sequence.get(geneIndex1);
			sequence.set(geneIndex1, sequence.get(geneIndex2));
			sequence.set(geneIndex2, tempGene);
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

	public float calculateLayout(BinConfiguration binsDim) {
		this.layout = PackingProcedures.getLayout( this.sequence, binsDim);
		return this.layout.getFitness();
	}

	public List<Packet> getSequence() {
		return this.sequence;
	}

	public void shuffleGenome() {
		java.util.Collections.shuffle(this.sequence, rand);
	}
}
