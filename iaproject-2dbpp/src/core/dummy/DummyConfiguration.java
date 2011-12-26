package core.dummy;

import java.io.Serializable;

public class DummyConfiguration implements Serializable {
	
	private static final long serialVersionUID = -3810077444503348229L;
	
	private final int maxIterations;
	private final int maxWaitTime;
	private final float publishProb;
	
	public DummyConfiguration(int maxWaitTime, int maxIterations, float publishProb) {
		if (maxWaitTime <= 0) {
			throw new IllegalArgumentException("negative or zero wait time");
		}
		if (maxIterations < 0) {
			throw new IllegalArgumentException("negative max iterations");
		}
		if (publishProb < 0 || publishProb > 1) {
			throw new IllegalArgumentException("illegal publish probability");
		}
		
		this.maxIterations = maxIterations;
		this.maxWaitTime = maxWaitTime;
		this.publishProb = publishProb;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public float getPublishProb() {
		return publishProb;
	}
	
}
