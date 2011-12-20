package core.genetic;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.CoreConfiguration;
import core.DataParsingException;

public class GeneticConfigurator extends AbstractConfigurator<GeneticConfiguration> {
	
	private final JIntegerTextField populationField = new JIntegerTextField();
	private final JIntegerTextField rotationField = new JIntegerTextField();
	private final JPanel completePane;
	
	public GeneticConfigurator() {
//		throw new IllegalArgumentException("Test Exception");
		
		completePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		completePane.add(new JLabel("Population size"));
		populationField.setColumns(10);
		completePane.add(populationField);
		
		completePane.add(new JLabel("Rotation probability"));
		rotationField.setColumns(10);
		completePane.add(rotationField);

	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return completePane;
	}

	@Override
	protected AbstractCore<GeneticConfiguration, ?> getConfiguredCore(CoreConfiguration<GeneticConfiguration> conf, OptimumPainter painter) {
		return new GeneticCore(conf, painter);
	}

	@Override
	protected GeneticConfiguration createCoreConfiguration() throws DataParsingException {
		// parse configuration panel in order to get desired input
		Integer ps = populationField.getValue();
		
		if (ps == null) {
			throw new DataParsingException("No pupulation size specified");
		}
		
		if (ps.intValue() <= 0) {
			throw new DataParsingException("Population size should be strictly positive");
		}
		
		Integer rp = rotationField.getValue();
		
		if (rp == null) {
			throw new DataParsingException("No mutation probability (as rotation) specified");
		}
		
		if (rp.intValue() <= 0) { // *** ricordarsi di cambiare questa condizione
			throw new DataParsingException("Probability should be in [0,1)");
		}
		
		return new GeneticConfiguration(ps,rp);
	}

	@Override
	protected void setConfiguration(GeneticConfiguration config) {
		populationField.setValue(config.getPopulationSize());
		rotationField.setValue(config.getRotateProbability());
		
		
	}

}