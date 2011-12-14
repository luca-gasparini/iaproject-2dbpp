package core.genetic;

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

public class GeneticConfigurator extends AbstractConfigurator<Integer> {
	
	private final JIntegerTextField populationField = new JIntegerTextField();
	private final JPanel completePane;
	
	public GeneticConfigurator() {
//		throw new IllegalArgumentException("Test Exception");
		
		completePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		completePane.add(new JLabel("Population size"));
		populationField.setColumns(10);
		completePane.add(populationField);
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return completePane;
	}

	@Override
	protected AbstractCore<Integer, ?> getConfiguredCore(CoreConfiguration<Integer> conf, OptimumPainter painter)
			throws DataParsingException {
		return new GeneticCore(conf, painter);
	}

	@Override
	protected Integer createCoreConfiguration() throws DataParsingException {
		// parse configuration panel in order to get desired input
		Integer ps = populationField.getValue();
		
		if (ps == null) {
			throw new DataParsingException("No pupulation size specified");
		}
		
		if (ps.intValue() <= 0) {
			throw new DataParsingException("Population size should be strictly positive");
		}
		
		return ps;
	}

	@Override
	protected void setConfiguration(Integer config) {
		populationField.setValue(config);
		
		
	}

}