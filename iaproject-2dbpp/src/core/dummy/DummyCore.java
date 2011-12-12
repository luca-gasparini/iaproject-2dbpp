package core.dummy;

import gui.OptimumPainter;

import java.util.Random;

import logic.ProblemConfiguration;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class DummyCore extends AbstractCore<Integer, Void> {
	
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
			CoreResult<Void> cr = new CoreResult<Void>() {
				@Override
				public float getFitness() {
					return rand.nextFloat();
				}
				@Override
				public Void getBins() {
					return null;
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
