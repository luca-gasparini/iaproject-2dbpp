package core.taboo;

import java.util.LinkedList;

public class TabooList {
	
	private final int tenure;
	private final LinkedList<Float> moves = new LinkedList<Float>();
	
	public TabooList(int tenure) {
		if (tenure <= 0) {
			throw new IllegalArgumentException("non-positive tenure");
		}
		
		this.tenure = tenure;
	}
	
	public boolean isTabu(float move) {
		return moves.contains(Float.valueOf(move));
	}
	
	public void addMove(float move) {
		assert !moves.contains(Float.valueOf(move)) : "already contained move";
		
		if (moves.size() == tenure) {
			moves.removeLast();
		}
		
		moves.addFirst(Float.valueOf(move));
	}
	
	public void clear() {
		moves.clear();
	}

}
