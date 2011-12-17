package core.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;
import logic.PacketConfiguration;

import BLFCore.BlfLayout;
import BLFCore.PackingProcedures;

class Individual {
	
	private List<Packet> sequence;
	private BlfLayout layout;
	private final Random rand = new Random(System.currentTimeMillis());

	public Individual(List<PacketConfiguration> packetsInfo, BinConfiguration binsInfo) {		
		// translate input from List<PacketConfigutation> to List<Packet>
		this.sequence = new ArrayList<Packet>();
		int id = 0; // forse qui si può migliorare
		for( int i=0; i < packetsInfo.size(); i++ ) {
			for( int j=0; j < packetsInfo.get(i).getMolteplicity(); j++ ) {
				this.sequence.add( new Packet(
						id++,
						packetsInfo.get(i).getWidth(), 
						packetsInfo.get(i).getHeight(),
						packetsInfo.get(i).getColor()));
			}
		
		} 
		java.util.Collections.shuffle(this.sequence, rand);
		
		// calculate blf layout and related fitness of the individual
		this.calculateLayout(binsInfo);
		
	}

	public Individual(List<Packet> sequence) {
		this.sequence = sequence;
		this.layout = null;
	}

	// apply mutation to the individual
	protected Individual mutate() {
		return null;
	}
	
	// return the fitness of the individual
	public float getFitness() {	
		return this.layout.getFitness();
	}	

	public List<Bin> getBins() {
		return this.layout.getBins();
	}

	public float calculateLayout(BinConfiguration binConfiguration) {

		this.layout = PackingProcedures.getLayout(this.sequence,binConfiguration);
		return this.layout.getFitness();
	}

	public List<Packet> getSequence() {
		return this.sequence;
	}
}
