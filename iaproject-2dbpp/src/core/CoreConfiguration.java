package core;

import logic.ProblemConfiguration;

public interface CoreConfiguration<T> {
	
	public ProblemConfiguration getProblemConfiguration();
	public T getCoreConfiguration();
	
}
