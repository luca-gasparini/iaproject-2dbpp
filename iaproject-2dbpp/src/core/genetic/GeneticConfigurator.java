package core.genetic;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import java.awt.Dimension;

import javax.swing.GroupLayout;
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
	private final JFloatTextField alphaField = new JFloatTextField();
	private final JFloatTextField betaField = new JFloatTextField();
	
	private final JPanel completePane;
	
	public GeneticConfigurator() {
//		throw new IllegalArgumentException("Test Exception");

		Dimension textFieldDim = new Dimension(100,30);
		populationField.setPreferredSize(textFieldDim);
		populationField.setMinimumSize(textFieldDim);
		populationField.setMaximumSize(textFieldDim);
		pRotationField.setPreferredSize(textFieldDim);
		pRotationField.setMinimumSize(textFieldDim);
		pRotationField.setMaximumSize(textFieldDim);
		pOrderField.setPreferredSize(textFieldDim);
		pOrderField.setMinimumSize(textFieldDim);
		pOrderField.setMaximumSize(textFieldDim);
		pCrossoverField.setPreferredSize(textFieldDim);
		pCrossoverField.setMinimumSize(textFieldDim);
		pCrossoverField.setMaximumSize(textFieldDim);
		alphaField.setPreferredSize(textFieldDim);
		alphaField.setMinimumSize(textFieldDim);
		alphaField.setMaximumSize(textFieldDim);
		betaField.setPreferredSize(textFieldDim);
		betaField.setMinimumSize(textFieldDim);
		betaField.setMaximumSize(textFieldDim);

		JLabel populationLbl = new JLabel("Population size");
		JLabel pRotationLbl = new JLabel("<html>Mutation probability<br/>(rotation based)</html>");
		JLabel pOrderLbl = new JLabel("<html>Mutation probability<br/>(order based)</html>");
		JLabel pCrossoverLbl = new JLabel("Crossover probability");
		JLabel alphaLbl = new JLabel("Fitness I param (alpha)");
		JLabel betaLbl = new JLabel("Fitness II param (beta)");

		completePane = new JPanel();
		GroupLayout layout = new GroupLayout(completePane);
		completePane.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(populationLbl)
						.addComponent(pRotationLbl)
						.addComponent(pOrderLbl)
						.addComponent(pCrossoverLbl)
						.addComponent(alphaLbl)
						.addComponent(betaLbl)

				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(populationField)
						.addComponent(pRotationField)
						.addComponent(pOrderField)
						.addComponent(pCrossoverField)
						.addComponent(alphaField)
						.addComponent(betaField)

				)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(populationLbl)
						.addComponent(populationField)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(pRotationLbl)
						.addComponent(pRotationField)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(pOrderLbl)
						.addComponent(pOrderField)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(pCrossoverLbl)
						.addComponent(pCrossoverField)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(alphaLbl)
						.addComponent(alphaField)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(betaLbl)
						.addComponent(betaField)
				)
		);
		
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
		
		
		Float a = alphaField.getValue();
		if (a == null) {
			throw new DataParsingException("No Alpha value specified ");
		}
		if (a.floatValue() < 0 || a.floatValue() > 1) {
			throw new DataParsingException("Alpha should be in [0,1]");
		}
		
		
		Float b = betaField.getValue();
		if (b == null) {
			throw new DataParsingException("No Beta value specified");
		}
		if (b.floatValue() < 0 || b.floatValue() > 1) {
			throw new DataParsingException("Beta should be in [0,1]");
		}
		
		
		return new GeneticConfiguration(ps,rp,op,cp,a,b);
	}

	@Override
	protected void setConfiguration(GeneticConfiguration config) {
		populationField.setValue(config.getPopulationSize());
		pRotationField.setValue(config.getRotateMutationProbability());
		pOrderField.setValue(config.getOrderMutationProbability());
		pCrossoverField.setValue(config.getCrossoverProbability());
		alphaField.setValue(config.getAlpha());
		alphaField.setValue(config.getBeta());
	}

}