package core.dummy;

import gui.OptimumPainter;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;
import logic.PacketDescriptor;
import logic.PlaceablePacket;
import logic.ProblemConfiguration;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class DummyCore extends AbstractCore<DummyConfiguration, List<Bin>> {
	
	private final Random rand = new Random(System.currentTimeMillis());
	private final int maxWaitPerTurn;
	private final int maxTurns;
	private final float publishProb;
	private final ProblemConfiguration problemConf;
	
	public DummyCore(CoreConfiguration<DummyConfiguration> conf, OptimumPainter painter) {
		super(conf, painter, Core2GuiTranslators.getBinListTranslator());
		this.problemConf = conf.getProblemConfiguration();
		this.maxWaitPerTurn = conf.getCoreConfiguration().getMaxWaitTime();
		this.maxTurns = conf.getCoreConfiguration().getMaxIterations();
		this.publishProb = conf.getCoreConfiguration().getPublishProb();
	}

	@Override
	protected void doWork() {
		
		int nBins = rand.nextInt(20) + 1;
		final List<Bin> bins = new ArrayList<Bin>(nBins+1);
		for (int i = 0; i < nBins; i++) {
			bins.add(createRandomBin(problemConf.getBin(), i));
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
			java.util.Collections.shuffle(bins);
			
			// ....
			
			// publish results
			float pub = rand.nextFloat();
			if (pub <= publishProb) {
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
				
				publishResult(cr);
			}
		}
		
	}
	
	private Bin createRandomBin(BinConfiguration binConf, int id) {
		int nPackets = rand.nextInt(10);
		
		Bin newBin = new Bin(binConf, id);
		for (int i = 0; i < nPackets; i++) {
			newBin.addPacket(createRandomPacket(i, binConf.getWidth(), binConf.getHeight()));
		}
		
		return newBin;
	}
	
	private PlaceablePacket createRandomPacket(int id, int binW, int binH) {
		int blpX = rand.nextInt(binW);
		int blpY = rand.nextInt(binH);
		Point point = new Point(blpX, blpY);
		
		int pW = rand.nextInt(binW - blpX) + 1;
		int pH = rand.nextInt(binH - blpY) + 1;
		
		Color color = new Color(rand.nextInt());
		
		// always rotatable for test use only
		return new PlaceablePacket(new Packet(new PacketDescriptor(id, pW, pH, color), false, true), point);
	}
	
	@Override
	protected boolean reachedStoppingCondition() {
		// 700K con stringa 0:0:15.4
		// 700K senza stringa 0:0:14.5
		return getNIterations() == maxTurns;
		
//		return false;
	}

}
