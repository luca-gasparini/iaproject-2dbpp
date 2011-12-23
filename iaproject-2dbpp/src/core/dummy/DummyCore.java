package core.dummy;

import gui.OptimumPainter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.Packet;
import logic.PacketDescriptor;
import logic.PacketSolution;
import logic.ProblemConfiguration;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class DummyCore extends AbstractCore<Integer, List<Bin>> {
	
	private final Random rand = new Random(System.currentTimeMillis());
	private final int maxWaitPerTurn;
	private final ProblemConfiguration problemConf;
	
	public DummyCore(CoreConfiguration<Integer> conf, OptimumPainter painter) {
		super(conf, painter, Core2GuiTranslators.getBinListTranslator());
		this.problemConf = conf.getProblemConfiguration();
		this.maxWaitPerTurn = conf.getCoreConfiguration().intValue();
	}

	@Override
	protected void doWork() {
		
		int nBins = rand.nextInt(20) + 1;
		final List<Bin> bins = new ArrayList<Bin>(nBins+1);
		for (int i = 0; i < nBins; i++) {
			bins.add(createRandomBin(i, 
					problemConf.getBin().getWidth(), 
					problemConf.getBin().getHeight()));
		}
		
		// controlled cycling
		while (this.canContinue()) {
			
			// method processing... here you can insert your code
			try {
				Thread.sleep(rand.nextInt(maxWaitPerTurn));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			// ....
			java.util.Collections.shuffle(bins);
			
			// --- begin test --- 	
			CoreResult<List<Bin>> cr = new AbstractCoreResult<List<Bin>>() {
				private float fitness = rand.nextFloat();
				
				@Override
				public float getFitness() {
					return this.fitness;
				}
				
				@Override
				public List<Bin> getBins() {
					return bins;
				}
			};
			// --- end test ---
			
			
			// publish results
			publishResult(cr);
		}
		
	}
	
	private Bin createRandomBin(int id, int width, int height) {
		int nPackets = rand.nextInt(10);
		
		Bin newBin = new Bin(id, width, height);
		for (int i = 0; i < nPackets; i++) {
			newBin.addPacket(createRandomPacket(i, width, height));
		}
		
		return newBin;
	}
	
	private Packet createRandomPacket(int id, int binW, int binH) {
		int blpX = rand.nextInt(binW);
		int blpY = rand.nextInt(binH);
		
		int pW = rand.nextInt(binW - blpX) + 1;
		int pH = rand.nextInt(binH - blpY) + 1;
		
		Color color = new Color(rand.nextInt());
		
		return new PacketSolution(new PacketDescriptor(id, pW, pH, color), blpX, blpY);
	}
	
	@Override
	protected boolean reachedStoppingCondition() {
		// 700K con stringa 0:0:15.4
		// 700K senza stringa 0:0:14.5
		return getNIterations() == 700000;
		
//		return false;
	}

}
