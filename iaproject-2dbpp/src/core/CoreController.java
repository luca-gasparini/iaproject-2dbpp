package core;

import java.util.Observable;
import java.util.Observer;

import gui.GUISignaler;

/**
 * Each {@link AbstractCore} MUST have just ONE {@link CoreController}
 * instance: multiple instances for the same Core should be considered an
 * error.
 */
public interface CoreController {
	
	public void start();
	
	public void pause();
	
	public void resume();
	
	public boolean stop();
	
	/**
	 * Adds an observer of this Core's life, like the observer pattern.
	 * 
	 * @param s
	 * @see Observer
	 * @see Observable
	 */
	public void setSignaler(GUISignaler s);
}
