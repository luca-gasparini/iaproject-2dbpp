package core.BLFTest;

import gui.OptimumPainter;

import javax.swing.JComponent;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.DataParsingException;

public class BLFTestConfigurator extends AbstractConfigurator<Void> {
	private static final JPanel confComp = new JPanel();
	
	@Override
	public JComponent getConfigurationComponent() {
		return confComp;
	}

	@Override
	protected void setConfiguration(Void config) {
		// do nothing
	}

	@Override
	protected AbstractCore<Void, ?> getConfiguredCore(CoreConfiguration<Void> conf,
			OptimumPainter painter) {
		return new BLFTestCore(conf, painter, Core2GuiTranslators.getGeneticTranslator());
	}

	@Override
	protected Void createCoreConfiguration() throws DataParsingException {
		return null;
	}

}
