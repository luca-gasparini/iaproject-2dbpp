package core.dummy;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.CoreConfiguration;
import core.DataParsingException;

public class DummyConfigurator extends AbstractConfigurator<DummyConfiguration> {
	
	private final JIntegerTextField msTf = new JIntegerTextField();
	private final JIntegerTextField itTf = new JIntegerTextField();
	private final JFloatTextField ppTf = new JFloatTextField();
	
	private final JPanel completePane;
	
	public DummyConfigurator() {
//		throw new IllegalArgumentException("Test Exception");
		
		JLabel msLbl = new JLabel("Max ms to wait");
		msTf.setColumns(10);
		
		JLabel itLbl = new JLabel("Max iterations");
		itTf.setColumns(10);
		itTf.setToolTipText("A value < 0 means Integer.MAX_VALUE");
		itTf.setValue(Integer.valueOf(-1));
		
		JLabel ppLbl = new JLabel("Publish probability");
		ppTf.setColumns(10);
		ppTf.setValue(Float.valueOf(1));
		
		completePane = new JPanel();
		GroupLayout layout = new GroupLayout(completePane);
		completePane.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(msLbl)
						.addComponent(itLbl)
						.addComponent(ppLbl)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(msTf, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
						.addComponent(itTf, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
						.addComponent(ppTf, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE)
				)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(msLbl)
						.addComponent(msTf, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(itLbl)
						.addComponent(itTf, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(ppLbl)
						.addComponent(ppTf, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
		);
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return completePane;
	}

	@Override
	protected AbstractCore<DummyConfiguration, ?> getConfiguredCore(CoreConfiguration<DummyConfiguration> conf, OptimumPainter painter) {
		return new DummyCore(conf, painter);
	}

	@Override
	protected DummyConfiguration createCoreConfiguration() throws DataParsingException {
		// parse configuration panel in order to get desired input
		final Integer ms = msTf.getValue();
		if (ms == null) {
			throw new DataParsingException("No wait time specified");
		}
		if (ms.intValue() <= 0) {
			throw new DataParsingException("Wait time should be strictly positive");
		}
		
		final Integer it = itTf.getValue();
		if (it == null) {
			throw new DataParsingException("No iteration specified");
		}
		
		final Float pp = ppTf.getValue();
		if (pp == null) {
			throw new DataParsingException("No publish probability specified");
		}
		if (pp.floatValue() < 0 || pp.floatValue() > 1) {
			throw new DataParsingException("Publish probability is incorrect");
		}
		
		return new DummyConfiguration(ms.intValue(), 
				((it.intValue() < 0) ? Integer.MAX_VALUE : it.intValue()),
				pp.floatValue());
	}

	@Override
	protected void setConfiguration(DummyConfiguration config) {
		msTf.setValue(Integer.valueOf(config.getMaxWaitTime()));
		itTf.setValue(Integer.valueOf(config.getMaxIterations()));
		ppTf.setValue(Float.valueOf(config.getPublishProb()));
	}

}
