package core.genetic;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.CoreConfiguration;
import core.DataParsingException;

public class GeneticConfigurator extends AbstractConfigurator<GeneticConfiguration> {
	
	private final JIntegerTextField populationField = new JIntegerTextField();
	private final JFloatTextField pRotationField = new JFloatTextField();
	private final JFloatTextField pOrderField = new JFloatTextField();
	private final JFloatTextField pCrossoverField = new JFloatTextField();
	
	private final JPanel completePane;
	
	public GeneticConfigurator() {
//		throw new IllegalArgumentException("Test Exception");

		completePane = new JPanel(new GridLayout( 0, 2 ));
		
		completePane.add(new JLabel("Population size"));
		populationField.setColumns(10);
		completePane.add(populationField);
		
		completePane.add(new JLabel("<html>Mutation probability<br/>(rotation based)</html>"));
		pRotationField.setColumns(10);
		completePane.add(pRotationField);
		
		completePane.add(new JLabel("<html>Mutation probability<br/>(order based)</html>"));
		pOrderField.setColumns(10);
		completePane.add(pOrderField);
		
		completePane.add(new JLabel("Crossover probability"));
		pCrossoverField.setColumns(10);
		completePane.add(pCrossoverField);
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
		
		
		Float rp = pRotationField.getValue();
		if (rp == null) {
			throw new DataParsingException("No rotation probability specified");
		}
		if (rp.floatValue() < 0 || rp.floatValue() > 1) {
			throw new DataParsingException("Probability should be in [0,1]");
		}
		
		
		Float op = pOrderField.getValue();
		if (op == null) {
			throw new DataParsingException("No order mutation probability specified");
		}
		if (op.floatValue() < 0 || op.floatValue() > 1) {
			throw new DataParsingException("Probability should be in [0,1]");
		}
		
		
		Float cp = pCrossoverField.getValue();
		if (cp == null) {
			throw new DataParsingException("No crossover probability specified");
		}
		if (cp.floatValue() < 0 || cp.floatValue() > 1) {
			throw new DataParsingException("Probability should be in [0,1]");
		}
		
		return new GeneticConfiguration(ps,rp,op,cp);
	}

	@Override
	protected void setConfiguration(GeneticConfiguration config) {
		populationField.setValue(config.getPopulationSize());
		pRotationField.setValue(config.getRotateMutationProbability());
		pOrderField.setValue(config.getOrderMutationProbability());
		pCrossoverField.setValue(config.getCrossoverProbability());
		
	}

}