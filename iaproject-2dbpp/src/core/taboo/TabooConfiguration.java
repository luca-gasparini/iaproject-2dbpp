package core.taboo;

import java.io.Serializable;

public class TabooConfiguration implements Serializable {

	private static final long serialVersionUID = -1688023669983960748L;
	
	public final float ALPHA;
	public final int DENSITY_FACTOR;
	public final int HEIGHT_FACTOR;
	public final int MAX_NEIGH_SIZE;
	public final int D_MAX;
	public final int FIRST_LIST_TENURE;
	public final int OTHER_LIST_TENURE;
	
	public TabooConfiguration(float aLPHA, int dENSITY_FACTOR,
			int hEIGHT_FACTOR, int mAX_NEIGH_SIZE, int d_MAX,
			int fIRST_LIST_TENURE, int oTHER_LIST_TENURE) {
		ALPHA = aLPHA;
		DENSITY_FACTOR = dENSITY_FACTOR;
		HEIGHT_FACTOR = hEIGHT_FACTOR;
		MAX_NEIGH_SIZE = mAX_NEIGH_SIZE;
		D_MAX = d_MAX;
		FIRST_LIST_TENURE = fIRST_LIST_TENURE;
		OTHER_LIST_TENURE = oTHER_LIST_TENURE;
	}
	
}
