package core;

public interface CoreResult<T> {
	
	public float getFitness();
	
	public T getBins();
	
	public int getNIterations();
	
	public long getElapsedTime();
}
