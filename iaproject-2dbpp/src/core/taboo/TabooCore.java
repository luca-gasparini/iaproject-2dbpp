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
		private final double valueSolution;
		
		public SearchResult(boolean diversify, int neighSize, ArrayList<TabooBin> newStep, double z) {
			this.diversify = diversify;
			this.neighSize = neighSize;
			this.newStep = newStep;
			this.valueSolution = z;
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
	private int totPackets;
	
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
			
		List<Packet> packets = ManageSolution.buildPacketList(
				problemConf.getPackets(), binConf);
		this.totPackets = packets.size();
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
		// in order to prevent further modifications to packet list
		packets = Collections.unmodifiableList(packets);
		// search first target bin
		int targetBin = searchTargetBin(bins, packets.size(), 1);
		// prepare some needed variables
		int d = 1;
		
		SearchResult sr = SEARCH(bins, targetBin, 1);
		/*
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
		*/

	}
	
	private SearchResult SEARCH(ArrayList<TabooBin> bins, int targetBin, int neighSize) {
		// MUST NOT MODIFY bins: use SearchResult to return new bin configuration (good ;))
		
		// same variable names of SEARCH pseudocode 

		float penaltyStar = Float.POSITIVE_INFINITY;

		int k = neighSize;
		TabooBin target = bins.get(targetBin);
		List<Packet> packetsIntoTargetBin = target.getPackets();
		
		for (Packet j: packetsIntoTargetBin) {
			List<Kupla> ktuple = buildKupleSetWithoutTargetBin(k, targetBin, bins).getList();
			
			for (Kupla u: ktuple) {
				
				List<Packet> s = buildS(getPacketsFromBins(u, bins), j);
				float penalty = Float.POSITIVE_INFINITY;
				
				// call BLF Layout
				BlfLayout layout = PackingProcedures.getLayout(s, binConf, tabooConf.HEIGHT_FACTOR, tabooConf.DENSITY_FACTOR);
				List<Bin> binsLayout = layout.getBins();
				int as = binsLayout.size();
				
				if (as < k) {
					ArrayList<TabooBin> newSolution = updateSolution(u, bins, binsLayout, targetBin, j);
					k = Math.max(1, k-1);
					/*float move = prepareResult(newSolution).getFitness();
					tabuLists.addMove(k, move);*/
					return new SearchResult(false, k, newSolution, newSolution.size());
				}
				
				if (as == k) { // unico caso in cui k può valere 1
					/*
					ArrayList<TabooBin> newSolution = updateSolution(u, bins, binsLayout, targetBin, j);
					float move = prepareResult(newSolution).getFitness();
					if (!tabuLists.isTabu(k, move) || // mossa non in tabu
							(target.size()==1 && target.getPackets().get(0).getId()==j.getId())) { // targetBin={j}
						if (target.size()==1 && target.getPackets().get(0).getId()==j.getId())
							k = Math.max(1, k-1);
						
						return new SearchResult(false, k, newSolution, move);	
					}*/
				}
				
				if (as == k+1 && k>1) {
					Integer tsig = argminFillingFunctionAmongBins(binsLayout);
					List<Packet> t = buildT(target.getPackets(), j, binsLayout.get(tsig).getList());
					
					// calcolo at
					BlfLayout layoutat = PackingProcedures.getLayout(t, binConf, tabooConf.HEIGHT_FACTOR, tabooConf.DENSITY_FACTOR);
					List<Bin> binsLayoutat = layoutat.getBins();
					int at = binsLayoutat.size();
					
					if (at==1) {
						Float valueFF = calculateFillingFunction(tabooConf.ALPHA, binsLayout.get(0).getList(), binConf, totPkts);
						/* if (???????????) */
					}
					
				}
				penaltyStar = Math.min(penaltyStar, penalty); 
			}
		}
	
		// TODO
		return new SearchResult(false, -1, null, -1);
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
		float value = calculateFillingFunction(tabooConf.ALPHA, bins.get(0).getPackets(), binConf, totPkts);
		mins.add(new Couple(value, 0));
		
		for (int i = 1; i < bins.size(); i++) {
			float ff = calculateFillingFunction(tabooConf.ALPHA, bins.get(i).getPackets(), binConf, totPkts);
			
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
	
	private static float calculateFillingFunction(float ALPHA, List<Packet> pkts, BinConfiguration binConf, int totPkts) {
		int binArea = binConf.getWidth() * binConf.getHeight();
		
		return ALPHA * (sumPktAreas(pkts) / binArea) - (pkts.size() / totPkts);
	}
	
	private static float sumPktAreas(List<Packet> pkts) {
		int area = 0;
		for (Packet pkt : pkts) {
			area += pkt.getWidth() * pkt.getHeight();
		}
		
		return area;
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
	
	private Integer argminFillingFunctionAmongBins(List<Bin> binsA) {
		
		Float minFF = calculateFillingFunction(tabooConf.ALPHA, binsA.get(0).getList(), binConf, this.totPackets);
		int minInd = 0;
		
		for (int i=1; i<binsA.size(); i++) {
			Float currentFF = calculateFillingFunction(tabooConf.ALPHA, binsA.get(i).getList(), binConf, this.totPackets);
			if (currentFF < minFF) {
				minFF = currentFF;
				minInd=i;
			}
		}
		return minInd;
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return false;
	}
	
	private class Kupla {
		private List<Integer> singleUpla;
		
		public Kupla() {
			this.singleUpla = new ArrayList<Integer>();
		}
		
		public Kupla(List<Integer> l) {
			List<Integer> newList = new ArrayList<Integer>();
			
			for (Integer num: l) {
				newList.add(num);
			}
			this.singleUpla = newList;
		}
		
		public List<Integer> getList() {
			return this.singleUpla;
		}
		
		public void add(Integer num) {
			singleUpla.add(num);
		}
		
		public int getSize() {
			return singleUpla.size();
		}
		
		public Integer getIntegerAtIndex(int i) {
			if (singleUpla.isEmpty() || i>=this.getSize())
				throw new IllegalStateException();
			
			return singleUpla.get(i);
		}
		
		public Integer getLast() {
			return this.getIntegerAtIndex(singleUpla.size()-1);
		}
		
		public Kupla getPrefix() {
			
			if (this.getSize()==0 || this.getSize()==1)
				return new Kupla();
						
			return new Kupla(singleUpla.subList(0, this.getSize()-1));
		}
		
		public boolean equals(Kupla compareUpla) {
			if (singleUpla.size() != compareUpla.getSize())
				return false;
			
			for (int i=0; i<this.getSize(); i++) {
				if (this.getIntegerAtIndex(i) != compareUpla.getIntegerAtIndex(i))
					return false;
			}
			return true;
		}
		
		public boolean containsInteger(int value) {
			
			for (Integer num: this.singleUpla) {
				if (num==value)
					return true;
			}
			return false;
		}
		
		public String toString() {
			String str = new String();
			
			for (int i=0; i<singleUpla.size(); i++)
				str = str.concat(singleUpla.get(i).toString());
			
			return str;
		}
	}
	
	private class SetKupla {
	
		private List<Kupla> set;
		
		public SetKupla() {
			set = new ArrayList<Kupla>();
		}
		
		public void add(Kupla u) {
			set.add(u);
		}
		
		public int size() {
			return set.size();
		}
		
		public Kupla get(int index) {
			return set.get(index);
		}
		
		public List<Kupla> getList() {
			return this.set;
		}
		
		public String toString() {
			
			String printSet = new String();
			
			for (Kupla upla: set) {
				printSet = printSet.concat(upla.toString());
				printSet = printSet.concat("\n");
			}
			
			return printSet;
		}
	}
	
	private class ManageListUple {
		
		private List<SetKupla> subSets;
		
		public ManageListUple() {
			this.subSets = new ArrayList<SetKupla>();
		}
		
		public void addSubSets(SetKupla subset) {
			subSets.add(subset);
		}
		
		public SetKupla getKSubSet(int k) {
			return subSets.get(k);
		}
		
		public void printSets() {
			for (int k=0; k<subSets.size(); k++) {
				System.out.println("k = "+k);
				System.out.println(subSets.get(k).toString());
			}
		}
	}
	
	private SetKupla buildKupleSetWithoutTargetBin(int k, int target, ArrayList<TabooBin> bins) {
		if (k==0)
			return new SetKupla();
		
		if (target<0)
			return new SetKupla();
		
		ManageListUple mlp = new ManageListUple();
		
		// singleton TabooBin
		SetKupla singleTons = new SetKupla();
		
		for (int j=0; j<bins.size(); j++) {
			if (j!=target) {
				Kupla u = new Kupla();
				u.add(j);
				singleTons.add(u);
			}
		}
		mlp.addSubSets(singleTons);
		
		// nosingleton TabooBin
		for (int i=2; i<=k; i++) {
			mlp.addSubSets(buildSetKUple(i, mlp.getKSubSet(i-2)));
		}
		//mlp.printSets();
		return mlp.getKSubSet(k-1);
	}
	
	private SetKupla buildSetKUple (int k, SetKupla listSetKLessOne) {
		
		int dimList = listSetKLessOne.size();
		SetKupla setK = new SetKupla();
		
		for (int i=0; i<dimList-1; i++) {
			Kupla pre1 = listSetKLessOne.get(i).getPrefix();
			
			for (int j=i+1; j<dimList; j++) {
				Kupla pre2 = listSetKLessOne.get(j).getPrefix();
				
				if (pre1.equals(pre2)) {
					Kupla newUpla = new Kupla(pre1.getList());
					newUpla.add(listSetKLessOne.get(i).getLast());
					newUpla.add(listSetKLessOne.get(j).getLast());
					setK.add(newUpla);
				}
			}
		}
		return setK;		
	}
	
	private List<Packet> getPacketsFromBins(Kupla si, ArrayList<TabooBin> bins) {
		
		List<Packet> s = new ArrayList<Packet>();
		
		for (int i=0; i<si.getSize(); i++) {
			TabooBin kbin = bins.get(si.getIntegerAtIndex(i));
			List<Packet> kbinPackets = kbin.getPackets();
			
			for(Packet p: kbinPackets) {
				s.add(p);
			}
		}
		
		return s;
	}
	
	/*
	 * Crea Lista Pacchetti S
	 * per ora, il pacchetto j viene inserito alla fine della lista
	 * (la posizione del pacchetto varia la soluzione prodotta dall'algoritmo BLF)
	 */
	private List<Packet> buildS (List<Packet> lp, Packet j) {
		List<Packet> lpn = new ArrayList<Packet>();
		
		for(Packet p: lp)
			lpn.add(p);
		
		lpn.add(j);
		return lpn;
	}
	
	private List<Packet> buildT (List<Packet> s1, Packet j, List<Packet> s2) {
		
		List<Packet> lpn = new ArrayList<Packet>();
		
		for(Packet p: s1) {
			if (p.getId() == j.getId())
				continue;
			lpn.add(p);
		}
			
		for(Packet p: s2)
			lpn.add(p);
		
		return lpn;
	}
	
	/*
	 * Crea la nuovaSoluzione trovata dall'algoritmo Search
	 */
	private ArrayList<TabooBin> updateSolution(
			Kupla u,
			ArrayList<TabooBin> bins,
			List<Bin> binsLayout,
			int targetBin,
			Packet j) {
	
		ArrayList<TabooBin> newSolution = new ArrayList<TabooBin>();
		
		// copy inhalterated bins
		for (int i=0; i<bins.size(); i++) {
			if (!u.containsInteger(i) && i!=targetBin) {
				newSolution.add(bins.get(i));
			}
		}
			
		// copy targetBin without Packet j
		List<Packet> packTargetBin = bins.get(targetBin).getPackets();
		TabooBin newTargetBin = new TabooBin();
		
		for (Packet p: packTargetBin) {
			if (p.getId() == j.getId())
				continue;
					
			newTargetBin.addPacket(p);
		}
		newSolution.add(newTargetBin);
			
		// copy bins NewLayout
		for (Bin b: binsLayout) {
			TabooBin newBin = new TabooBin();
			
			for (Packet p: b.getList()) {
				newBin.addPacket(p);
			}
			newSolution.add(newBin);
		}
			
		return newSolution;
	}
	
	/* evaluate binomial (n,k) - unused till now */
	private static int doBinomial(int n, int k) {
		if (n<=0 || k<0)
			throw new java.lang.IllegalArgumentException();
		
		if (k==0)
			return 1;
		
		int num=1;
		int den=1;
				
		for (int i=0; i<k; i++) {
			num = num*(n--);
			den = den*(k--);
		}
		return num/den;
	}
	
}
