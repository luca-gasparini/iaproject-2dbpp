package core;

import gui.GUIBin;
import gui.GUIOptimum;
import gui.OptimumPainter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingWorker;

public abstract class AbstractCore<T> extends SwingWorker<Void, CoreResult<T>> {
	
	private class Controller implements CoreController {

		@Override
		public void execute() {
			AbstractCore.this.execute();
		}

		@Override
		public void pause() {
			synchronized (mutex) {
				isPaused = false;
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
	private volatile long startTime;
	
	public AbstractCore(OptimumPainter painter, Core2GuiTranslator<T> translator) {
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
			
			@Override
			public int getNIterations() {
				return this.getNIterations();
			}
			
			@Override
			public float getFitness() {
				return result.getFitness();
			}
			
			@Override
			public long getElapsedTime() {
				return this.getElapsedTime();
			}
			
			@Override
			public List<GUIBin> getBins() {
				return c2gt.translate(lastOptimum);
			}
		});
	}
	
	protected final boolean canContinue() {
		/* This is the place where we can add other constraints in Core
		 * execution, for example maximum # of iterations.
		 
		if (getNIterations() > SOME_MAX_VALUE) {
			return false;
		}
		
		*/
		
		/* check if it's paused and, if so, send thread to sleep,
		 * otherwise return true.
		 */
		synchronized (mutex) {
			while(isPaused) {
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
			}
		}
		
		if (this.isCancelled()) {
			return true;
		} else {
			/* this is the right place to put the iteration increment because, 
			 * here, worker is not in pause and it is already in the new processing
			 * iteration. In other words, if we had put the increment elsewhere
			 * in some situation we would have had the counter one
			 * step ahead.
			 */
			nIterations.incrementAndGet();
			return false;
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
	 */
	protected abstract void doWork();
	
	public final long getElapsedTime() {
		return System.currentTimeMillis() - this.startTime;
	}
	
	public final int getNIterations() {
		return nIterations.get();
	}
	
	public final CoreController getController() {
		return this.controller;
	}
}
