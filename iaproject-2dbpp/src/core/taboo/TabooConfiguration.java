package core.taboo;

import java.io.Serializable;

public class TabooConfiguration implements Serializable {

	private static final long serialVersionUID = -1688023669983960748L;

	public static final int MAX_NON_CHANGING_MOVES = 50;
	
	public final float ALPHA;
	public final float DENSITY_FACTOR;
	public final float HEIGHT_FACTOR;
	public final int MAX_NEIGH_SIZE;
	public final int D_MAX;
	public final int FIRST_LIST_TENURE;
	public final int OTHER_LIST_TENURE;
	public final boolean IMPROVEBLF;
	
	public TabooConfiguration(float aLPHA, float dENSITY_FACTOR,
			float hEIGHT_FACTOR, int mAX_NEIGH_SIZE, int d_MAX,
			int fIRST_LIST_TENURE, int oTHER_LIST_TENURE, boolean iMPROBEBLF) {
		ALPHA = aLPHA;
		DENSITY_FACTOR = dENSITY_FACTOR;
		HEIGHT_FACTOR = hEIGHT_FACTOR;
		MAX_NEIGH_SIZE = mAX_NEIGH_SIZE;
		D_MAX = d_MAX;
		FIRST_LIST_TENURE = fIRST_LIST_TENURE;
		OTHER_LIST_TENURE = oTHER_LIST_TENURE;
		IMPROVEBLF = iMPROBEBLF;
	}
	
}
