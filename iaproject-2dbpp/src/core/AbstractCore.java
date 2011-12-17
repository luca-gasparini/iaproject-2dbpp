package core;

import gui.GUIBin;
import gui.GUIOptimum;
import gui.OptimumPainter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingWorker;

import core.dummy.DummyCore;

/**
 * 
 * @author socket
 *
 * @param <K> Core-specific configuration class
 * @param <T> Core-specific result class
 */
public abstract class AbstractCore<K, T> extends SwingWorker<Void, CoreResult<T>> {
	
	private class Controller implements CoreController {

		@Override
		public void start() {
			AbstractCore.this.execute();
		}

		@Override
		public void pause() {
			synchronized (mutex) {
				isPaused = true;
			}
		}

		@Override
		public void resume() {
			synchronized (mutex) {
				isPaused = false;
				mutex.notifyAll();
			}
		}

		@Override
		public boolean stop() {
			boolean toReturn = AbstractCore.this.cancel(false);
			
			/* remember to resume probably-waiting worker thread (this
			 * happens when, after a #pause() you call #stop())
			 */
			this.resume();
			
			return toReturn;
		}
	}
	
	/**
	 * MUST BE CREATED JUST A MOMENT BEFORE PUBLISHING IT
	 *
	 * @param <R>
	 */
	public abstract class AbstractCoreResult<R> implements CoreResult<R> {
		private final int nIterations;
		private final long elapsedTime;
		
		public AbstractCoreResult() {
			this.nIterations = AbstractCore.this.getNIterations();
			this.elapsedTime = AbstractCore.this.getElapsedTime();
		}
		
		@Override
		public int getNIterations() {
			return this.nIterations;
		}
		
		@Override
		public long getElapsedTime() {
			return this.elapsedTime;
		}
	}
	
	private final Object mutex = new Object();
	
	/**
	 * Guarded-by mutex
	 * This variable needs a mutex because it is accessed both (and 
	 * concurrently) by EventDispatcher thread and SwingerWorker thread.
	 */
	private boolean isPaused;
	
	// used an atomic integer just because I'm lazy
	private final AtomicInteger nIterations = new AtomicInteger(0);
	private final OptimumPainter displayer;
	private final CoreController controller;
	private final Core2GuiTranslator<T> c2gt;
	
	// volatile because can be accessed by different threads
	private volatile long startTime = -1;
	private volatile long lastPauseStart = -1;
	private volatile long totPauseTime = 0;
	
	public AbstractCore(CoreConfiguration<K> configuration,
			OptimumPainter painter, 
			Core2GuiTranslator<T> translator) {
		
		this.displayer = painter;
		this.c2gt = translator;
		/* it's OK to instantiate the Controller here because the class is
		 * private and can be returned only via #getController() (that is
		 * when 'this' is correctly referenced).
		 */
		this.controller = new Controller();
	}
	
	@Override
	protected final void process(List<CoreResult<T>> chunks) {
		
		/* we are interested only in painting the last optimum (this
		 * increases performance)
		 */
		final CoreResult<T> result = chunks.get(chunks.size() - 1);
		final T lastOptimum = result.getBins();
		displayer.paint(new GUIOptimum() {
			private final int nIterations = result.getNIterations();
			private final long elapsedTime = result.getElapsedTime();
			private final float fitness = result.getFitness();
			private final List<GUIBin> bins = c2gt.translate(lastOptimum);
			
			@Override
			public int getNIterations() {
				return this.nIterations;
			}
			
			@Override
			public float getFitness() {
				return this.fitness;
			}
			
			@Override
			public long getElapsedTime() {
				return this.elapsedTime;
			}
			
			@Override
			public List<GUIBin> getBins() {
				return this.bins;
			}
		});
	}
	
	protected final boolean canContinue() {
		if (reachedStoppingCondition()) {
			return false;
		}
		
		/* check if it's paused and, if so, send thread to sleep,
		 * otherwise return true.
		 */
		synchronized (mutex) {
			while(isPaused) {
				this.lastPauseStart = System.currentTimeMillis();
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					/* always re-interrupt an interrupted thread
					 * because catching InterruptedException clears the
					 * interrupted status
					 */
					Thread.currentThread().interrupt();
					// tells worker to exit
					return false;
				}
				this.totPauseTime += System.currentTimeMillis() - this.lastPauseStart;
			}
		}
		
		if (this.isCancelled()) {
			return false;
		} else {
			/* this is the right place to put the iteration increment because, 
			 * here, worker is not in pause and it is already in the new processing
			 * iteration. In other words, if we had put the increment elsewhere
			 * in some situation we would have had the counter one
			 * step ahead.
			 */
			nIterations.incrementAndGet();
			return true;
		}
	}
	
	@Override
	protected final Void doInBackground() {
		this.startTime = System.currentTimeMillis();
		doWork();
		return null;
	}
	
	/**
	 * TODO: write an explanation of how this method should be implemented
	 * by now see {@link DummyCore}
	 */
	protected abstract void doWork();
	protected abstract boolean reachedStoppingCondition();
	
	public final long getElapsedTime() {
		return System.currentTimeMillis() - this.startTime - this.totPauseTime;
	}
	
	public final int getNIterations() {
		return nIterations.get();
	}
	
	public final CoreController getController() {
		return this.controller;
	}
	
}
