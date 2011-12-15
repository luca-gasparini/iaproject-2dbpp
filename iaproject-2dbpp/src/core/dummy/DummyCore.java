package core.dummy;

import gui.OptimumPainter;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import logic.Bin;
import logic.Packet;
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
		super(conf, painter, Core2GuiTranslators.getDummyTranslator());
		this.problemConf = conf.getProblemConfiguration();
		this.maxWaitPerTurn = conf.getCoreConfiguration().intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doWork() {
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
			
			// publish results
			CoreResult<List<Bin>> cr = new CoreResult<List<Bin>>() {
				
				@Override
				public float getFitness() {
					return rand.nextFloat();
				}
				@Override
				public List<Bin> getBins() {
					/* test */
					List<Bin> testList = new LinkedList<Bin>();
					testList.add(new Bin(1, 100, 100));
					testList.add(new Bin(2, 50, 50));
					Packet id0 = new Packet(10, 10, 10, 20, 20, Color.BLUE);
					id0.setRotate(true);
					testList.get(0).addPacket(id0);
					testList.get(0).addPacket(new Packet(11, 15, 20, 40, 40, Color.green));
					
					return testList;
				}
			};
			publish(cr);
		}
		
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return false;
	}

}
