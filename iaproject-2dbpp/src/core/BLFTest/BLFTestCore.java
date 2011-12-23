package core.BLFTest;

import gui.OptimumPainter;

import java.util.List;

import logic.Bin;
import logic.BinConfiguration;
import logic.ManageSolution;
import logic.Packet;
import logic.PacketConfiguration;
import logic.PacketDescriptor;
import logic.ProblemConfiguration;
import BLFCore.BlfLayout;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.CoreResult;

public class BLFTestCore extends AbstractCore<BLFTestCoreConfiguration, List<Bin>> {

	private final ProblemConfiguration problemConf;
	List<PacketConfiguration> pkConf;
	private boolean rotate;
	
	public BLFTestCore(CoreConfiguration<BLFTestCoreConfiguration> configuration, OptimumPainter painter) {
		super(configuration, painter, Core2GuiTranslators.getBinListTranslator());

		this.problemConf = configuration.getProblemConfiguration();
		this.pkConf = problemConf.getPackets();
		this.rotate = configuration.getCoreConfiguration().getSelected();
	}

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
		
		List<PacketDescriptor> pacs = ManageSolution.buildPacketList(pkConf);
		packets = ManageSolution.buildPacketSolutionTestRotate(pacs, rotate);
		
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
		publishResult(result);

	}

	@Override
	protected boolean reachedStoppingCondition() {
		return true;
	}

}
