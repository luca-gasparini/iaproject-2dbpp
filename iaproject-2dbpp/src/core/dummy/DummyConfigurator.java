package core.dummy;

import gui.OptimumPainter;
import gui.common.JIntegerTextField;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.CoreConfiguration;
import core.DataParsingException;

public class DummyConfigurator extends AbstractConfigurator<Integer> {
	
	private final JIntegerTextField msTf = new JIntegerTextField();
	private final JPanel completePane;
	
	public DummyConfigurator() {
//		throw new IllegalArgumentException("Test Exception");
		
		completePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		completePane.add(new JLabel("Max ms to wait"));
		msTf.setColumns(10);
		completePane.add(msTf);
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return completePane;
	}

	@Override
	protected AbstractCore<Integer, ?> getConfiguredCore(CoreConfiguration<Integer> conf, OptimumPainter painter) {
		return new DummyCore(conf, painter);
	}

	@Override
	protected Integer createCoreConfiguration() throws DataParsingException {
		// parse configuration panel in order to get desired input
		Integer ms = msTf.getValue();
		
		if (ms == null) {
			throw new DataParsingException("No wait time specified");
		}
		
		if (ms.intValue() <= 0) {
			throw new DataParsingException("Wait time should be strictly positive");
		}
		
		return ms;
	}

	@Override
	protected void setConfiguration(Integer config) {
		msTf.setValue(config);
	}

}
