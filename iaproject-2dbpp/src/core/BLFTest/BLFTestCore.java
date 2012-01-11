package core.BLFTest;

import gui.OptimumPainter;

import java.util.List;

import logic.Bin;
import logic.BinConfiguration;
import logic.ManageSolution;
import logic.Packet;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;
import BLFCore.BlfLayout;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class BLFTestCore extends AbstractCore<BLFTestCoreConfiguration, List<Bin>> {

	private final ProblemConfiguration problemConf;
	private final BLFTestCoreConfiguration coreConf;
	
	public BLFTestCore(CoreConfiguration<BLFTestCoreConfiguration> configuration, OptimumPainter painter) {
		super(configuration, painter, Core2GuiTranslators.getBinListTranslator());

		this.problemConf = configuration.getProblemConfiguration();
		this.coreConf = configuration.getCoreConfiguration();
	}

	@Override
	protected void doWork() {

		// PUT YOUR CODE HERE:
		/*
		 * puoi usare come input la lista di bin passata con assieme alla
		 * variabile pc, oppure creartene una tu. Quello che importa è che, alla
		 * fine, ciò che verrà stampato nell'interfaccia grafica sarà la
		 * variabile bins (opportunamente convertita in GUIBin)
		 * 
		 * PS: non so se la stampa dei pacchetti dentro ai GUIBin sia corretta,
		 * per quello devi parlare con Chessa! :)
		 */

		// example code;
		BinConfiguration binConf = problemConf.getBin();
		
		List<Packet> packets = coreConf.getPackets();
		
		if (packets == null || packets.isEmpty()) {
			boolean rotate = coreConf.isSelected();
			List<PacketConfiguration> pkConf = problemConf.getPackets();
			packets = ManageSolution.buildPacketSolutionTestRotate(pkConf, rotate);
		}
				
		BlfLayout layout = BLFCore.PackingProcedures.getLayout(packets, binConf,1,1);
			
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
		publishResult(result);

	}

	@Override
	protected boolean reachedStoppingCondition() {
		return true;
	}

}
