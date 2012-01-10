package core.taboo;

import java.io.Serializable;

public class TabooConfiguration implements Serializable {

	private static final long serialVersionUID = -1688023669983960748L;
	
	public final float ALPHA;
	public final int MAX_NEIGH_SIZE;
	public final int D_MAX;
	public final int FIRST_LIST_TENURE;
	public final int OTHER_LIST_TENURE;
	
	public TabooConfiguration(float alpha, int maxNeighSize, int dMax,
			int firstListTenure, int otherListTenure) {
		
		ALPHA = alpha;
		MAX_NEIGH_SIZE = maxNeighSize;
		D_MAX = dMax;
		FIRST_LIST_TENURE = firstListTenure;
		OTHER_LIST_TENURE = otherListTenure;
	}
	
	
	
}
