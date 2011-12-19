package core;

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
	
}
