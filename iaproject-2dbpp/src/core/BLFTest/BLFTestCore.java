package core.BLFTest;

import gui.OptimumPainter;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import BLFCore.BlfLayout;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;
import logic.PacketConfiguration;
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

		/*
		 * puoi usare come input la lista di bin passata con assieme alla
		 * variabile pc, oppure creartene una tu. Quello che importa � che, alla
		 * fine, ci� che verr� stampato nell'interfaccia grafica sar� la
		 * variabile bins (opportunamente convertita in GUIBin)
		 * 
		 * PS: non so se la stampa dei pacchetti dentro ai GUIBin sia corretta,
		 * per quello devi parlare con Chessa! :)
		 */

		// example code;
		BinConfiguration binConf = problemConf.getBin();
		List<PacketConfiguration> pkConf = problemConf.getPackets();
		ArrayList<Packet> packets = new ArrayList<Packet>();
		int cont=0;
		for (int i = 0; i < pkConf.size(); i++) {
			PacketConfiguration pkc = pkConf.get(i);
			
			for (int j=0; j<pkc.getMolteplicity(); j++) {
				packets.add(new Packet(cont++, pkc.getWidth(), pkc.getHeight(), 0, 0, pkc.getColor()));
			}
		}

		BlfLayout layout = BLFCore.PackingProcedures
				.getLayout(packets, binConf);

		List<Bin> newBins = layout.getBins();

		final Float fitness = new Float(layout.getFitness());
		
		bins.addAll(newBins);

		// ------------------

		CoreResult<List<Bin>> result = new AbstractCoreResult<List<Bin>>() {
			@Override
			public float getFitness() {
				//return fitness.floatValue(); 
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
