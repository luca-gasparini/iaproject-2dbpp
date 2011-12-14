package core.genetic;

import gui.GUIPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.Packet;
import logic.PacketConfiguration;

class Individual {
	
	private List<Packet> sequence;
	private float fitness;
	private final Random rand = new Random(System.currentTimeMillis());

	public Individual(List<PacketConfiguration> packetList) {
		// translate input from List<PacketConfigutation> to List<Packet>
		this.sequence = new ArrayList<Packet>();
		for( int i=0; i < packetList.size(); i++ ) {
			for( int j=0; j < packetList.get(i).getMolteplicity(); j++ ) {
				// istantiate GUIPacket because i need to save color
				this.sequence.add( new GUIPacket(
						i + j,
						packetList.get(i).getWidth(), 
						packetList.get(i).getHeight(),
						packetList.get(i).getColor()));
			}
		} 
		java.util.Collections.shuffle(sequence, rand);
	}

	// apply mutation to the individual
	protected Individual mutate() {
		return null;
	}

	// caluculate and return the fitness of the individual
	public float calculateFitness() {
		return 0;
	}
	
	// return the fitness of the individual.
	public float getFitness() {
		return 0;
	}

	// return the ordered packing sequence
	public List<Packet> getSequence() {
		return this.sequence;
	}
}
