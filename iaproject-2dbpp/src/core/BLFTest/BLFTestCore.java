package core.BLFTest;

import gui.OptimumPainter;

import java.util.LinkedList;
import java.util.List;

import logic.Bin;
import logic.BinConfiguration;
import logic.ProblemConfiguration;
import core.AbstractCore;
import core.Core2GuiTranslator;
import core.CoreConfiguration;
import core.CoreResult;

public class BLFTestCore extends AbstractCore<Void, List<Bin>> {
	
	private final ProblemConfiguration problemConf;
	
	public BLFTestCore(CoreConfiguration<Void> configuration,
			OptimumPainter painter, Core2GuiTranslator<List<Bin>> translator) {
		super(configuration, painter, translator);
		
		this.problemConf = configuration.getProblemConfiguration();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doWork() {
		
		// PUT YOUR CODE HERE: 
		final List<Bin> bins = new LinkedList<Bin>();
		
		/* puoi usare come input la lista di bin passata con assieme alla
		 * variabile pc, oppure creartene una tu. Quello che importa è che,
		 * alla fine, ciò che verrà stampato nell'interfaccia grafica sarà 
		 * la variabile bins (opportunamente convertita in GUIBin)
		 * 
		 * PS: non so se la stampa dei pacchetti dentro ai GUIBin sia corretta,
		 * per quello devi parlare con Chessa! :)
		 */
		
		// example code;
		BinConfiguration binConf = problemConf.getBin();
		Bin newBin = new Bin(0, binConf.getWidth(), binConf.getHeight());
		
		// riempi newBin come vuoi, magari usando problemConf.getPackets();
		
		bins.add(newBin);
		
		
		// ------------------
		
		
		CoreResult<List<Bin>> result = new AbstractCoreResult<List<Bin>>() {
			@Override
			public float getFitness() {
				return 0;
			}
			@Override
			public List<Bin> getBins() {
				return bins;
			}
		};
		publish(result);
		
	}

	@Override
	protected boolean reachedStoppingCondition() {
		return true;
	}

}
