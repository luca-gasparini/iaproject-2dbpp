package core.BLFTest;

import gui.OptimumPainter;

import java.util.ArrayList;
import java.util.List;

import logic.Bin;
import logic.BinConfiguration;
import logic.Packet;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;
import BLFCore.BlfLayout;
import core.AbstractCore;
import core.Core2GuiTranslator;
import core.CoreConfiguration;
import core.CoreResult;

public class BLFTestCore extends AbstractCore<List<Packet>, List<Bin>> {

	private final ProblemConfiguration problemConf;
	private final List<Packet> packets;

	public BLFTestCore(CoreConfiguration<List<Packet>> configuration,
			OptimumPainter painter, Core2GuiTranslator<List<Bin>> translator) {
		super(configuration, painter, translator);

		this.problemConf = configuration.getProblemConfiguration();
		this.packets = configuration.getCoreConfiguration();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doWork() {

		// PUT YOUR CODE HERE:
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
		
		List<Packet> packets;
		
		if (this.packets != null && !this.packets.isEmpty()) {
			packets = this.packets;
		} else {
			packets = new ArrayList<Packet>();
			List<PacketConfiguration> pkConf = problemConf.getPackets();
			int cont=0;
			for (PacketConfiguration pkc : pkConf) {
				for (int j=0; j<pkc.getMolteplicity(); j++) {
					packets.add(new Packet(cont++, pkc.getWidth(), pkc.getHeight(), 0, 0, pkc.getColor()));
				}
			}
		}
		
		BlfLayout layout = BLFCore.PackingProcedures.getLayout(packets, binConf);
			
		final List<Bin> bins = layout.getBins();
		final float fitness = layout.getFitness();
		
		// ------------------

		CoreResult<List<Bin>> result = new AbstractCoreResult<List<Bin>>() {
			@Override
			public float getFitness() {
				return fitness; 
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
