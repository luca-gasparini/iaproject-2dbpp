package core.taboo;

import java.util.ArrayList;
import java.util.List;

public class TabooListsManager {
	
	private final int otherListTenure;
	
	private final TabooList firstList;
	private final List<TabooList> otherLists = new ArrayList<TabooList>();
	
	public TabooListsManager(int firstListTenure, int otherListTenure) {
		if (firstListTenure <= 0) {
			throw new IllegalArgumentException("non-positive first list tenure");
		}
		if (otherListTenure <= 0) {
			throw new IllegalArgumentException("non-positive other list tenure");
		}
		
		this.otherListTenure = otherListTenure;
		firstList = new TabooList(firstListTenure);
	}

	public boolean isTabu(int k, float move) {
		if (k <= 0) {
			throw new IllegalArgumentException("non-positive k");
		}
		
		if (k == 1) {
			return firstList.isTabu(move);
		} else {
			ensureCapacity(otherLists, otherListTenure, k - 2);
			return otherLists.get(k - 2).isTabu(move);
		}
	}
	
	/**
	 * Adds <tt>move</tt> to the <tt>k</tt>-th list of tabu moves.
	 * @param k the list index (1-based)
	 * @param move the move to be added. For the first list (<tt>k = 1</tt>) 
	 * move is intended as <i>filling function</i> value, for the other list
	 * (<tt>k > 1</tt>) move means <i>penalty</i>.
	 */
	public void addMove(int k, float move) {
		if (k <= 0) {
			throw new IllegalArgumentException("non-positive k");
		}
		
		if (k == 1) {
			firstList.addMove(move);
		} else {
			// ensure capacity of otherLists
			ensureCapacity(otherLists, otherListTenure, k - 2);
			otherLists.get(k - 2).addMove(move);
		}
	}
	
	private static void ensureCapacity(List<TabooList> tl, int tTenure, int index) {
		if (tl.size() <= index) {
			for (int i = tl.size(); i <= index; i++) {
				tl.add(new TabooList(tTenure));
			}
		}
	}

	public void clearAll() {
		firstList.clear();
		
		for (TabooList tlist : otherLists) {
			tlist.clear();
		}
	}
	
}
