package core.taboo;

import java.math.BigDecimal;
import java.util.LinkedList;

public class TabooList {
	
	private final int tenure;
	//private final LinkedList<Float> moves = new LinkedList<Float>();
	private final LinkedList<BigDecimal> moves = new LinkedList<BigDecimal>();
	private final int precision = 4;
	
	public TabooList(int tenure) {
		if (tenure <= 0) {
			throw new IllegalArgumentException("non-positive tenure");
		}
		
		this.tenure = tenure;
	}
	
	public boolean isTabu(float move) {
		//return moves.contains(Float.valueOf(move));
		return moves.contains(new BigDecimal(move).setScale(precision, BigDecimal.ROUND_HALF_UP));
	}
	
	public void addMove(float move) {
		assert !moves.contains(Float.valueOf(move)) : "already contained move";
		
		if (moves.size() == tenure) {
			moves.removeLast();
		}
		
		//moves.addFirst(Float.valueOf(move));
		moves.addFirst(new BigDecimal(move).setScale(precision, BigDecimal.ROUND_HALF_UP));
	}
	
	public void clear() {
		moves.clear();
	}

}
