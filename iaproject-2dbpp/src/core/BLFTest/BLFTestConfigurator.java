package core.BLFTest;

import gui.OptimumPainter;

import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import logic.Packet;
import core.AbstractConfigurator;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.DataParsingException;

public class BLFTestConfigurator extends AbstractConfigurator<List<Packet>> {
	private static final JPanel confComp = new JPanel();
	
	private List<Packet> packets;
	
	@Override
	public JComponent getConfigurationComponent() {
		return confComp;
	}

	@Override
	protected void setConfiguration(List<Packet> config) {
		this.packets = config;
	}

	@Override
	protected AbstractCore<List<Packet>, ?> getConfiguredCore(CoreConfiguration<List<Packet>> conf,
			OptimumPainter painter) {
		return new BLFTestCore(conf, painter, Core2GuiTranslators.getGeneticTranslator());
	}

	@Override
	protected List<Packet> createCoreConfiguration() throws DataParsingException {
		if (this.packets == null) {
			return Collections.emptyList();
		} else {
			return this.packets;
		}
	}

}
