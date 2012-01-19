package core.taboo;

import gui.OptimumPainter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		//private final double valueSolution;
		
		public SearchResult(boolean diversify, int neighSize, ArrayList<TabooBin> newStep/*, double z*/) {
			this.diversify = diversify;
			this.neighSize = neighSize;
			this.newStep = newStep;
			//this.valueSolution = z;
		}		
	}
	
	private class DiversificationResult {
		private final int d;
		private final ArrayList<TabooBin> newBins;
		private final boolean shouldResetTabuLists;
		
		public DiversificationResult(int d,
				ArrayList<TabooBin> newBins, boolean shouldResetTabuLists) {
			this.d = d;
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
	private CoreResult<List<Bin>> currentOptimum;
	
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
		currentOptimum = new AbstractCoreResult<List<Bin>>() {
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
			/* use BLFLayout to pack in order to get the best placing (yes,
			 * even for one bin
			 */
			BlfLayout layout = giveBestLayout(Collections.<TabooBin>emptyList(), packet);
			
			assert layout.getBins().size() == 1 : "more than 1 bin for packing 1 packet";
			
			TabooBin bin = new TabooBin(layout.getBins().get(0).getPacketList());
			bins.add(bin);
		}
		// in order to prevent further modifications to packet list
		packets = Collections.unmodifiableList(packets);
		
		// prepare some needed variables
		int d = 1;
		int neighSize = 1;
		int nonChangingCounter = 0;
		boolean forceDiversification = false;
		boolean findNewTarget = false;
		
		int targetBin = searchTargetBin(bins, packets.size(), d);
		
		while (canContinue()) {
			
			SearchResult sr = SEARCH(bins, targetBin, neighSize);
			
			// check if it is a nonChanging move
			if (((sr.newStep == null) || (bins.size() == sr.newStep.size()) && 
					(neighSize == sr.neighSize)) ) {
				nonChangingCounter++;
			} else {
				nonChangingCounter = 0;
			}
			
			// check if we have a new move
			if (sr.newStep != null) {
				// check if fitness is better...
				CoreResult<List<Bin>> result = prepareResult(sr.newStep);
				
				if (Float.compare(result.getFitness(), currentOptimum.getFitness()) < 0) {
					// ... and publish & set new optimum
					currentOptimum = result;
					publishResult(currentOptimum);
					
					// reset variables
					nonChangingCounter = 0;
					d = neighSize = 1;
				} else {
					assert result.getBins().size() >= currentOptimum.getBins().size() : "more bins";
				}
				
				// set bin list to the current move
				bins = sr.newStep;
			}
			
			findNewTarget = false;
			if (sr.neighSize <= neighSize || targetBin >= bins.size()) {
				findNewTarget = true;
			}
			neighSize = sr.neighSize;
			
			// check if we got maximum nonChanging moves
			if (nonChangingCounter >= TabooConfiguration.MAX_NON_CHANGING_MOVES) {
				nonChangingCounter = 0;
				if (neighSize == tabooConf.MAX_NEIGH_SIZE) {
					forceDiversification = true;
				} else {
					neighSize++;
					findNewTarget = true;
				}
			}
			
			if (findNewTarget && !sr.diversify && !forceDiversification) {
				targetBin = searchTargetBin(bins, packets.size(), 1);
			}
			
			/* force diversification and check on neighSize mimic C code
			 * behavior
			 */
			if (sr.diversify || forceDiversification || neighSize >= bins.size()) {
				forceDiversification = false;
				neighSize = 1;
				
				DiversificationResult dr = DIVERSIFICATION(bins, d, packets.size());
				d = dr.d;
				if (dr.newBins != null) {
					bins = dr.newBins;
				}
				if (dr.shouldResetTabuLists) {
					tabuLists.clearAll();
				}
				
				targetBin = searchTargetBin(bins, packets.size(), d);
			}
		}
		
		//System.out.println("ciao");
	}
	
	private SearchResult SEARCH(ArrayList<TabooBin> bins, int targetBin, int neighSize) {
		// MUST NOT MODIFY bins: use SearchResult to return new bin configuration (good ;))
		
		// same variable names of SEARCH pseudocode 

		float penaltyStar = Float.POSITIVE_INFINITY;

		int k = neighSize;
		TabooBin target = bins.get(targetBin);
		List<Packet> packetsIntoTargetBin = target.getPackets();
		
		/* create the collection of all bins without target one (needed to
		 * build k-tuples)
		 */
		List<TabooBin> binsWOtarget = new LinkedList<TabooBin>(bins);
		binsWOtarget.remove(targetBin);
		Collections.shuffle(binsWOtarget);
		
		ArrayList<TabooBin> packetsMovePenaltyStar = new ArrayList<TabooBin>();
		ArrayList<TabooBin> packetsMovePenalty = new ArrayList<TabooBin>();
		
		for (Packet j: packetsIntoTargetBin) {
			
			TupleIterator<TabooBin> ktuple = new TupleIterator<TabooBin>(k, binsWOtarget);
			
			while (ktuple.hasNext()) {
				List<TabooBin> u = ktuple.next();
				
				float penalty = Float.POSITIVE_INFINITY;
				
				// call BLF Layout
				//BlfLayout layout = PackingProcedures.getLayout(s, binConf, tabooConf.HEIGHT_FACTOR, tabooConf.DENSITY_FACTOR);
				BlfLayout layout = this.giveBestLayout(u, j);
				List<Bin> binsLayout = layout.getBins();
				int as = binsLayout.size(); // numero bin necessari per as
				Couple move = this.getPenalty(binsLayout); // panalty associata alla mossa as
				
				// possible cases
				if (as < k) {
					ArrayList<TabooBin> newSolution = updateSolution(u, bins, binsLayout, targetBin, j);
					k = Math.max(1, k-1);
					return new SearchResult(false, k, newSolution);
				}
				
				if (as == k) { // unico caso in cui k può valere 1
					ArrayList<TabooBin> newSolution;
					if (target.size() == 1 || !tabuLists.isTabu(k, move.value)) {
						newSolution = updateSolution(u, bins, binsLayout, targetBin, j);
						
						if (target.size()==1) {
							/* the overall number of bins has been reduced by
							 * the empty of target bin
							 */
							k = Math.max(1, k-1);
						} else {
							/* the overall number of bins is still the same:
							 * the list isn't an improvement so should be added
							 * to the tabu list
							 */
							tabuLists.addMove(k, move.value);
						}
						
						return new SearchResult(false, k, newSolution);
					}
				}
				
				if (as == k+1 && k>1) { // mossa peggiorativa
					
					Couple tsig = argminFillingFunctionAmongBins(binsLayout);
					Bin tsigBin = binsLayout.get(tsig.index);
					List<Packet> t = buildT(target.getPackets(), j, tsigBin.getPacketList());
					
					// calcolo at
					BlfLayout layoutat = callBLFLayout(t);
					List<Bin> binsLayoutat = layoutat.getBins();
					int at = binsLayoutat.size();
					
					if (at==1) {
						float valueFFT = calculateFillingFunction(tabooConf.ALPHA, t, binConf, totPackets);
						
						List<Bin> binsForMinimize = new ArrayList<Bin>();
						for (int i=0; i<binsLayout.size(); i++) {
							if (i == tsig.index)
								continue;
							
							binsForMinimize.add(binsLayout.get(i));
						}
						
						Couple minFFIlessT = argminFillingFunctionAmongBins(binsForMinimize);
						
						float newPenalty = Math.min(valueFFT, minFFIlessT.value);
						
						if (!tabuLists.isTabu(k, newPenalty)) {
							penalty = newPenalty;
							
							/* siamo riusciti ad impaccare in un unico bin gli elementi
							 * di tsig con i pacchetti rimanenti del target (ovvero
							 * senza j) => la soluzione sarà composta da:
							 * - binsLayout senza tsig
							 * - binsLayoutat (ovvero t perchè nessuno è stato girato)
							 * - tutti gli altri bin
							 */
							// build newSolution:
							ArrayList<TabooBin> newSolution = new ArrayList<TabooBin>();
							
							// - binsLayout without tsig
							boolean found = false;
							for (Bin bin : binsLayout) {
								if (bin == tsigBin) {
									assert !found : "tsig already found";
									found = true;
									continue;
								}
								TabooBin newBin = new TabooBin(bin.getPacketList());
								newSolution.add(newBin);
							}
							assert found : "tsig not found";
							
							/* - binsLayoutat (we are already sure that this
							 * packet list packs into a single bin)
							 */
							newSolution.add(new TabooBin(t));
							
							/* - all other bins, that is bins that are not part
							 * of the k-uple 'u' and are not the target bin.
							 */
							LinkedList<TabooBin> writableU = new LinkedList<TabooBin>(u);
							for (TabooBin tabooBin : bins) {
								if (!writableU.remove(tabooBin) && tabooBin != target) {
									/* bin is not contained in 'u' and is not target one, 
									 * so it hasn't been modified and can be inserted 'as-is' into 
									 * the new solution
									 */
									newSolution.add(tabooBin);
								}
							}
							assert writableU.isEmpty() : "not all k-uple bins have been removed";
							
							packetsMovePenalty = newSolution;
						}
					}
				}
				
				// penaltyStar = min(penaltyStar, penalty);
				if (Float.compare(penalty, penaltyStar) < 0) {
					penaltyStar = penalty;
					packetsMovePenaltyStar = packetsMovePenalty;
				}
			}
		}
		
		if (Float.compare(penaltyStar, Float.POSITIVE_INFINITY) != 0) {
			tabuLists.addMove(k, penaltyStar);
			return new SearchResult(false, k, packetsMovePenaltyStar);
		} else {
			boolean diversify = false;
			
			if (k == this.tabooConf.MAX_NEIGH_SIZE)
				diversify = true;
			else
				k++;
			
			// lista vuota
			return new SearchResult(diversify, k, null);
		}
	}
	
	private DiversificationResult DIVERSIFICATION(final ArrayList<TabooBin> bins, int d, final int totPkts) {
		if ((d < bins.size()) && (d < tabooConf.D_MAX)) {
			d++;
			return new DiversificationResult(d, null, false);
		} else {
			/* PHASE1: remove from the solution the floor(bins.size()/2) bins 
			 * with smallest filling function value
			 */
			/* sort bins in descending order (bins with lower ff at the end):
			 * computational cost O(n*log(n)) due to merge sort usage
			 */
			ArrayList<TabooBin> newBins = new ArrayList<TabooBin>(bins);
			Collections.sort(newBins, new Comparator<TabooBin>() {
				@Override
				public int compare(TabooBin o1, TabooBin o2) {
					float o1ff = calculateFillingFunction(tabooConf.ALPHA, o1.getPackets(), binConf, totPkts);
					float o2ff = calculateFillingFunction(tabooConf.ALPHA, o2.getPackets(), binConf, totPkts);
					
					// reverse (desc) order is achieved here inverting o1 and o2
					return Float.compare(o2ff, o1ff);
				}
			});
			/* now remove floor(bins.size()/2) bins and save contained
			 * pkts in removedPkts: computational cost O(n) because JDK 
			 * specification assure that remotion from tail of an ArrayList 
			 * runs in constant time
			 */
			List<Packet> removedPkts = new ArrayList<Packet>();
			for (int i = 0; i < bins.size() / 2; i++) {
				TabooBin remBin = newBins.remove(newBins.size() - 1);
				removedPkts.addAll(remBin.getPackets());
			}
			
			/* PHASE2: pack into a separate bin each item currently packed 
			 * into a removed bin
			 */
			for (Packet packet : removedPkts) {
				TabooBin addedBin = new TabooBin(packet);
				newBins.add(addedBin);
			}
			
			return new DiversificationResult(1, newBins, true);
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
		
		// first element is inserted straightway
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
	
	private BlfLayout giveBestLayout (List<TabooBin> bins, Packet j) {
		/*
		 * Crea Lista Pacchetti S
		 * per ora, il pacchetto j viene inserito alla fine della lista
		 * -(la posizione del pacchetto varia la soluzione prodotta dall'algoritmo BLF)
		 * la rotazione del pacchetto viene valutata sul layout ottenuto dal BLF
		 * sui BINS presi in esame (quelli della kupla);
		 */
		List<Packet> s = getPacketsFromBins(bins);
		
		s.add(j);
		BlfLayout lsStd = callBLFLayout(s);
		
		if (j.isRotatable()) {
			// j is in the last position, so remove it and add rotated counterpart
			s.remove(s.size() - 1);
			s.add(j.getRotated());
			BlfLayout lsRotStd = callBLFLayout(s);
			
			if (Float.compare(lsRotStd.getFitness(), lsStd.getFitness()) < 0) {
				lsStd = lsRotStd;
			}
			
			// place non-rotated j for next steps
			s.remove(s.size() - 1);
			s.add(j);
		}
		
		if (!tabooConf.IMPROVEBLF) {
			return lsStd;
		}
		
		/* IMPROVEBLF is true: we try to improve BLF placing results by 
		 * sorting in a width-ascendent way all packets.
		 */
		
		// tells if p1 is wider than p2
		Comparator<Packet> pktComp = new Comparator<Packet>() {
			@Override
			public int compare(Packet p1, Packet p2) {
				int p1dim = p1.getWidth();
				int p2dim = p2.getWidth();
				
//				return -Integer.compare(p1dim, p2dim);
				return p2dim - p1dim;
			}
		};
		
		// sort s in descending width order
		Collections.sort(s, pktComp);
		BlfLayout lsImp = callBLFLayout(s);
		
		if (j.isRotatable()) {
			// find j, remove it and insert its rotation in the correct place
			/* we use such a long way in order to avoid another sort: following
			 * algorithm has O(s.size) complexity because it scans each element
			 * in the list at most once.
			 */
			boolean removed = false, insertionStop = false;
			Packet jRot = j.getRotated();
			ListIterator<Packet> pIt = s.listIterator();
			while(pIt.hasNext() && (!removed || !insertionStop)) {
				Packet p = pIt.next();
				if (p.getId() == j.getId()) {
					pIt.remove();
					removed = true;
				} else if (pktComp.compare(jRot, p) < 0) {
					insertionStop = true;
				}
			}
			if (insertionStop) { pIt.previous(); }
			pIt.add(jRot);
			while(pIt.hasNext() && (!removed)) {
				Packet p = pIt.next();
				if (p.getId() == j.getId()) {
					pIt.remove();
					removed = true;
				}
			}
			assert removed : "not removed";
			
			BlfLayout lsRotImp = callBLFLayout(s);
			if (Float.compare(lsRotImp.getFitness(), lsImp.getFitness()) < 0) {
				lsImp = lsRotImp;
			}
		}
		
		if (Float.compare(lsStd.getFitness(), lsImp.getFitness()) <= 0) {
			return lsStd;
		} else {
			return lsImp;
		}
	}
	
	private static float calculateFillingFunction(float ALPHA, List<Packet> pkts, BinConfiguration binConf, int totPkts) {
		int binArea = binConf.getWidth() * binConf.getHeight();
		
		return ALPHA * (sumPktAreas(pkts) / binArea) - (pkts.size() / totPkts);
	}
	
	private static float sumPktAreas(List<Packet> pkts) {
		int area = 0;
		for (Packet pkt : pkts) {
			area += pkt.getArea();
		}
		
		return area;
	}
	
	private CoreResult<List<Bin>> prepareResult(final List<TabooBin> tabooBins) {
		final List<Bin> binList = new LinkedList<Bin>();
		float minFitness = Float.POSITIVE_INFINITY;
		
		for (TabooBin bin : tabooBins) {
			BlfLayout binLayout = callBLFLayout(bin.getPackets());
			
			assert binLayout.getBins().size() == 1 : "A TabooBin pack in >1 bins: " + bin;
			
			binList.addAll(binLayout.getBins());
			
			if (Float.compare(binLayout.getFitness(), minFitness) < 0) {
				minFitness = binLayout.getFitness();
			}
		}
		
		final float totFitness = 100 * binList.size() + (minFitness - 100);
		
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
	
	private Couple argminFillingFunctionAmongBins(List<Bin> binsA) {
		
		float minFF = calculateFillingFunction(tabooConf.ALPHA, binsA.get(0).getPacketList(), binConf, this.totPackets);
		Couple fin = new Couple(minFF, 0);
		
		for (int i=1; i<binsA.size(); i++) {
			float currentFF = calculateFillingFunction(tabooConf.ALPHA, binsA.get(i).getPacketList(), binConf, this.totPackets);
			if (Float.compare(currentFF, fin.value) < 0)
				fin = new Couple(currentFF, i);
		}
		return fin;
	}
	
	private Couple getPenalty(List<Bin> lb) { // idem as argminFillingFunctionAmongBins
		Couple fin = argminFillingFunctionAmongBins(lb);
		return fin;
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return this.currentOptimum.getBins().size() == 1;
	}
	
	private List<Packet> getPacketsFromBins(List<TabooBin> bins) {
		
		List<Packet> s = new ArrayList<Packet>();
		
		for (TabooBin tabooBin : bins) {
			s.addAll(tabooBin.getPackets());
		}
		
		return s;
	}
	
	private List<Packet> buildT (List<Packet> s1, Packet j, List<Packet> s2) {
		
		List<Packet> lpn = new ArrayList<Packet>();
		
		boolean found = false;
		for(Packet p: s1) {
			if (p.getId() == j.getId()) {
				assert !found : "j already found";
				found = true;
				continue;
			}
			lpn.add(p);
		}
		assert found : "j not found";
			
		lpn.addAll(s2);
		
		assert lpn.size() == s1.size() + s2.size() - 1 : "wrong size on T";
		
		return lpn;
	}
	
	/*
	 * Crea la nuovaSoluzione trovata dall'algoritmo Search
	 */
	private ArrayList<TabooBin> updateSolution(
			List<TabooBin> u, // lista di bin presi che potrebbero essere stati modificati
			ArrayList<TabooBin> bins, // tutti i bin della soluzione NON aggiornata
			List<Bin> binsLayout, // lista di bin aggiornati
			int targetBin, // bin target
			Packet j) { // pacchetto j (ORIGINALE - non quello eventualmente ruotato) che è stato spostato
		
		ArrayList<TabooBin> newSolution = new ArrayList<TabooBin>();
		
		// copy bins NewLayout
		for (Bin b: binsLayout) {
			TabooBin newBin = new TabooBin(b.getPacketList());
			newSolution.add(newBin);
		}
		
		// create a modifiable view of 'u'
		List<TabooBin> writableU = new LinkedList<TabooBin>(u);
		// get targetBin reference
		TabooBin target = bins.get(targetBin);
		/* insert all bins that haven't been modified (i.e. are not part of u
		 * and are not the target one).
		 */
		for (TabooBin bin : bins) {
			if (!writableU.remove(bin) && bin != target) {
				/* bin is not contained in 'u' and is not target one, 
				 * so it hasn't been modified and can be inserted 'as-is' into 
				 * the new solution
				 */
				newSolution.add(bin);
			}
		}
		assert writableU.isEmpty() : "not all k-uple bins have been removed";
		
		// copy targetBin without Packet j iif targetBin contains other packets than packet j:
		// - first of all create the new packet list without j
		LinkedList<Packet> packTargetBin = new LinkedList<Packet>();
		
		// TODO debug line, remove
//		System.out.println("seq with j: " + target.getPackets());
		
		boolean found = false;
		for (Packet packet : target.getPackets()) {
			if (packet.getId() == j.getId()) {
				assert !found : "j has been found twice";
				found = true;
				continue;
			}
			packTargetBin.add(packet);
		}
		assert found : "j hasn't been found at all";
		
		if (packTargetBin.isEmpty()) {
			// target bin has been completely emptied: no more bin in the solution
			return newSolution;
		}
		
		/* - now we have to check that new target bin still packs into a single
		 * bin: this may not be true due to the remotion of one packet as
		 * below example where pkt2 remotion cause pkt4 to fall into another
		 * bin.
		 * 
		 *  _____          44444
		 * |44444|_|      |44444  |
		 * |44444|3|  -\  |   |3| |
		 * |___|2|3|  -/  |___|3| |
		 * |111|2|3|      |111|3| |
		 * ---------      ---------
		 */
		List<Bin> targetBins = callBLFLayout(packTargetBin).getBins();
		if (targetBins.size() > 1) {
			
			// TODO debug line, remove
			System.out.println("starting seq: " + packTargetBin);
			
			// TODO debug line -> remove
			assert targetBins.size() == 2 : "very bad target layout";
			
			/* get the biggest pkt of the second (aka last?) bin and try to move
			 * it backwards, step by step, in order to make the target bin
			 * placeable again
			 */
			List<Packet> overflowPkts = targetBins.get(targetBins.size() - 1).getPacketList();
			Packet overflowPkt = overflowPkts.get(0);
			for (Packet packet : overflowPkts) {
				if (packet.getArea() > overflowPkt.getArea()) {
					overflowPkt = packet;
				}
			}
			
			/* first find overflowPkt into the list and keep packIterator
			 * pointing to it
			 */
			found = false;
			ListIterator<Packet> packIterator = packTargetBin.listIterator(packTargetBin.size());
			while (packIterator.hasPrevious()) {
				Packet p = packIterator.previous();
				if (p == overflowPkt) {
					assert !found : "already found";
					found = true;
					break;
				}
			}
			assert found : "not found";
			
			int nIt = 0;
			do {
				nIt++;
				
				packIterator.remove();
				/* there's always a previous,: otherwise means that
				 * no matter where we place overflowPkt in the list, we
				 * always get a layout with >1 bins.
				 */
				assert packIterator.hasPrevious() : "KAAAAABBOOOOOOOOOOMMMM!!!!";
				packIterator.previous();
				packIterator.add(overflowPkt);
				// in order to keep packIterator pointing to overflowPkt
				packIterator.previous();
				
				// TODO debug line, remove
				System.out.println(nIt + "-> " + targetBins.size() + "bins -> " + packTargetBin);
				
				targetBins = callBLFLayout(packTargetBin).getBins();
			} while (targetBins.size() > 1);
			
			// TODO debug line, remove
			System.out.println(nIt + " iteration to revise target bin made of " + packTargetBin.size() + " pkts");
		}
			
		// - now add new target bin to the solution
		newSolution.add(new TabooBin(packTargetBin));
		
		return newSolution;
	}
	
	private BlfLayout callBLFLayout(List<Packet> packets) {
		return PackingProcedures.getLayout(packets, binConf, tabooConf.HEIGHT_FACTOR, tabooConf.DENSITY_FACTOR);
	}
	
}
