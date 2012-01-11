package core.taboo;

import gui.OptimumPainter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import logic.Bin;
import logic.BinConfiguration;
import logic.ManageSolution;
import logic.Packet;
import logic.ProblemConfiguration;
import BLFCore.BlfLayout;
import BLFCore.PackingProcedures;
import core.AbstractCore;
import core.Core2GuiTranslator;
import core.CoreConfiguration;
import core.CoreResult;

public class TabooCore extends AbstractCore<TabooConfiguration, List<Bin>> {
	
	private class SearchResult {
		private final boolean diversify;
		private final int neighSize;
		private final ArrayList<TabooBin> newStep;
		
		public SearchResult(boolean diversify, int neighSize,
				ArrayList<TabooBin> newStep) {
			this.diversify = diversify;
			this.neighSize = neighSize;
			this.newStep = newStep;
		}
		
	}
	
	private class DiversificationResult {
		private final int d;
		private final int targetBin;
		private final ArrayList<TabooBin> newBins;
		private final boolean shouldResetTabuLists;
		
		public DiversificationResult(int d, int targetBin,
				ArrayList<TabooBin> newBins, boolean shouldResetTabuLists) {
			this.d = d;
			this.targetBin = targetBin;
			this.newBins = newBins;
			this.shouldResetTabuLists = shouldResetTabuLists;
		}
	}
	
	private class Couple {
		private final float value;
		private final int index;
		
		public Couple(float value, int index) {
			this.value = value;
			this.index = index;
		}
	}
	
	private final ProblemConfiguration problemConf;
	private final BinConfiguration binConf;
	private final TabooConfiguration tabooConf;
	private final TabooListsManager tabuLists;
	
	public TabooCore(CoreConfiguration<TabooConfiguration> configuration,
			OptimumPainter painter, Core2GuiTranslator<List<Bin>> translator) {
		super(configuration, painter, translator);
		
		this.problemConf = configuration.getProblemConfiguration();
		this.binConf = problemConf.getBin();
		this.tabooConf = configuration.getCoreConfiguration();
		this.tabuLists = new TabooListsManager(tabooConf.FIRST_LIST_TENURE, 
				tabooConf.OTHER_LIST_TENURE);
	}

	@Override
	protected void doWork() {
		
		List<Packet> packets = Collections.unmodifiableList(ManageSolution.buildPacketList(
				problemConf.getPackets(), binConf));
		
		/* create an initial dummy solution:
		 * this step is not done using PackingProcedures because resulting
		 * fitness may be calculated in a different way than our one.
		 */
		CoreResult<List<Bin>> currentOptimum = new AbstractCoreResult<List<Bin>>() {
			@Override
			public float getFitness() {
				return Float.POSITIVE_INFINITY;
			}
			@Override
			public List<Bin> getBins() {
				return Collections.emptyList();
			}
		};
		
		// prepare taboo lists
		tabuLists.clearAll();
		// pack each item into a separate bin
		ArrayList<TabooBin> bins = new ArrayList<TabooBin>();
		Collections.shuffle(packets);
		for (Packet packet : packets) {
			TabooBin bin = new TabooBin(packet);
			bins.add(bin);
		}
		// search first target bin
		int targetBin = searchTargetBin(bins, packets.size(), 1);
		// prepare some needed variables
		int d = 1;
		
		while (canContinue()) {
			int neighSize = 1;
			
			SearchResult sr = SEARCH(bins, targetBin, neighSize);
			// check if we have a new move
			if (sr.newStep != null) {
				// check if fitness is better...
				CoreResult<List<Bin>> result = prepareResult(sr.newStep);
				if (Float.compare(result.getFitness(), currentOptimum.getFitness()) < 0) {
					// ... and publish & set new optimum
					currentOptimum = result;
					publishResult(currentOptimum);
				}
				// set bin list to the current move
				bins = sr.newStep;
			}
			
			if (sr.neighSize <= neighSize) {
				targetBin = searchTargetBin(bins, packets.size(), 1);
			}
			
			if (sr.diversify) {
				DiversificationResult dr = DIVERSIFICATION(bins, d, packets.size());
				d = dr.d;
				if (dr.targetBin >= 0) {
					targetBin = dr.targetBin;
				}
				if (dr.newBins != null) {
					bins = dr.newBins;
				}
				if (dr.shouldResetTabuLists) {
					tabuLists.clearAll();
				}
			}
		}
		

	}
	
	private SearchResult SEARCH(ArrayList<TabooBin> bins, int targetBin, int neighSize) {
		// MUST NOT MODIFY bins: use SearchResult to return new bin configuration
		
		float penalty = Float.POSITIVE_INFINITY;
		// TODO
		
		return new SearchResult(false, -1, null);
	}
	
	private DiversificationResult DIVERSIFICATION(ArrayList<TabooBin> bins, int d, int totPkts) {
		if ((d <= bins.size()) && (d < tabooConf.D_MAX)) {
			d++;
			// let t be the bin with d-th smallest value of filling function
			int newTarget = searchTargetBin(bins, totPkts, d);
			return new DiversificationResult(d, newTarget, null, false);
		} else {
			/* remove from the solution the floor(bins.size()/2) bins with
			 * smallest filling function value
			 */
			// TODO
			
			/* pack into a separate bin each item currently packed into a
			 * removed bin
			 */
			// TODO
			ArrayList<TabooBin> newBins = null;
			
			return new DiversificationResult(1, -1, newBins, true);
		}
	}
	
	/**
	 * Search the <tt>i-th</tt> bin that minimize <i>filling function</i>.
	 * @param bins
	 * @param totPkts
	 * @param i_th the <i>i-th</i> bin to search for (1-based). If you search
	 * for THE bin that minimize filling function this parameter should be set
	 * to 1.
	 * @return
	 */
	private int searchTargetBin(ArrayList<TabooBin> bins, int totPkts, int i_th) {
		assert i_th >= 1 && i_th <= bins.size() : "non-existent " + i_th + " bin: " + bins.size() + " total";
		assert !bins.isEmpty() : "there's no bins";
		assert totPkts >= 0 : "wrong number of packets " + totPkts;
		
		LinkedList<Couple> mins = new LinkedList<TabooCore.Couple>();
		float value = calculateFillingFunction(tabooConf.ALPHA, bins.get(0), binConf, totPkts);
		mins.add(new Couple(value, 0));
		
		for (int i = 1; i < bins.size(); i++) {
			float ff = calculateFillingFunction(tabooConf.ALPHA, bins.get(i), binConf, totPkts);
			
			// we want the bin that minimize filling function
			if (Float.compare(ff, mins.getLast().value) < 0) {
				if (mins.size() == i_th) {
					mins.removeLast();
				}
				// ordered insertion
				ListIterator<Couple> lit = mins.listIterator();
				while (lit.hasNext()) {
					if (Float.compare(lit.next().value, ff) > 0) {
						lit.previous();
						break;
					}
				}
				lit.add(new Couple(ff, i));
			}
		}
		
		assert !mins.isEmpty() : "mins is still empty";
		return mins.getLast().index;
	}
	
	private static float calculateFillingFunction(float ALPHA, TabooBin bin, BinConfiguration binConf, int totPkts) {
		int binArea = binConf.getWidth() * binConf.getHeight();
		
		return ALPHA * (bin.sumPktAreas() / binArea) - (bin.size() / totPkts);
	}
	
	
	private CoreResult<List<Bin>> prepareResult(final List<TabooBin> tabooBins) {
		final List<Bin> binList = new LinkedList<Bin>();
		float fitness = 0;
		
		for (TabooBin bin : tabooBins) {
			BlfLayout binLayout = PackingProcedures.getLayout(bin.getPackets(), 
					binConf, tabooConf.HEIGHT_FACTOR, tabooConf.DENSITY_FACTOR);
			assert binLayout.getBins().size() == 1 : "A TabooBin pack in >1 bins";
			
			binList.addAll(binLayout.getBins());
			fitness += binLayout.getFitness();
		}
		
		final float totFitness = fitness;
		
		return new AbstractCoreResult<List<Bin>>() {
			@Override
			public float getFitness() {
				return totFitness;
			}

			@Override
			public List<Bin> getBins() {
				return binList;
			}
		};
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return false;
	}

}
