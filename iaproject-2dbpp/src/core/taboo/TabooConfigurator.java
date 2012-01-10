package core.taboo;

import gui.OptimumPainter;

import javax.swing.JComponent;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.CoreConfiguration;
import core.DataParsingException;

public class TabooConfigurator extends AbstractConfigurator<TabooConfiguration> {

	@Override
	public JComponent getConfigurationComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setConfiguration(TabooConfiguration config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AbstractCore<TabooConfiguration, ?> getConfiguredCore(
			CoreConfiguration<TabooConfiguration> conf, OptimumPainter painter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TabooConfiguration createCoreConfiguration()
			throws DataParsingException {
		// TODO Auto-generated method stub
		return null;
	}

}
