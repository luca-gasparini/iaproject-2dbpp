package core.dummy;

import gui.OptimumPainter;

import java.util.Random;

import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreResult;

public class DummyCore extends AbstractCore<Void> {
	
	private final Random rand = new Random(System.currentTimeMillis());
	private final int maxWaitPerTurn;
	
	public DummyCore(OptimumPainter painter, int maxWaitPerTurn) {
		super(painter, Core2GuiTranslators.getDummyTranslator());
		
		this.maxWaitPerTurn = maxWaitPerTurn;
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

}
