package core.tournament;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.CoreConfiguration;
import core.DataParsingException;

public class TournamentConfigurator extends AbstractConfigurator<TournamentConfiguration> {
	
	private static final int DEFAULT_POPULATION_SIZE = 100;
	private static final float DEFAULT_P_ROTATION = 0.3f;
	private static final float DEFAULT_P_SWAP = 0.8f;	
	private static final float DEFAULT_P_ORDER = 0.8f;
	private static final float DEFAULT_P_CROSSOVER = 0.9f;
	private static final float DEFAULT_ALPHA = 0.7f;
	private static final float DEFAULT_BETA = 0.3f;
	private static final int DEFAULT_ELITE_SIZE = 90;
	private static final int DEFAULT_TOURNAMENT_SIZE = 5;
	private static final boolean DEFAULT_START = true;
	
	private final JIntegerTextField populationField = new JIntegerTextField();
	private final JFloatTextField pRotationField = new JFloatTextField();
	private final JFloatTextField pSwapField = new JFloatTextField();
	private final JFloatTextField pOrderField = new JFloatTextField();
	private final JFloatTextField pCrossoverField = new JFloatTextField();
	private final JFloatTextField alphaField = new JFloatTextField();
	private final JFloatTextField betaField = new JFloatTextField();
	private final JIntegerTextField eliteSizeField = new JIntegerTextField();
	private final JIntegerTextField tournamentSizeField = new JIntegerTextField();
	private final JCheckBox startField = new JCheckBox();
	
	private final JPanel completePane;
	
	public TournamentConfigurator() {
//		throw new IllegalArgumentException("Test Exception");

		Dimension textFieldDim = new Dimension(100,30);
		populationField.setPreferredSize(textFieldDim);
		populationField.setMinimumSize(textFieldDim);
		populationField.setMaximumSize(textFieldDim);
		populationField.setValue(DEFAULT_POPULATION_SIZE);
		pRotationField.setPreferredSize(textFieldDim);
		pRotationField.setMinimumSize(textFieldDim);
		pRotationField.setMaximumSize(textFieldDim);
		pRotationField.setValue(DEFAULT_P_ROTATION);
		pSwapField.setPreferredSize(textFieldDim);
		pSwapField.setMinimumSize(textFieldDim);
		pSwapField.setMaximumSize(textFieldDim);
		pSwapField.setValue(DEFAULT_P_SWAP);
		pOrderField.setPreferredSize(textFieldDim);
		pOrderField.setMinimumSize(textFieldDim);
		pOrderField.setMaximumSize(textFieldDim);
		pOrderField.setValue(DEFAULT_P_ORDER);
		pCrossoverField.setPreferredSize(textFieldDim);
		pCrossoverField.setMinimumSize(textFieldDim);
		pCrossoverField.setMaximumSize(textFieldDim);
		pCrossoverField.setValue(DEFAULT_P_CROSSOVER);
		alphaField.setPreferredSize(textFieldDim);
		alphaField.setMinimumSize(textFieldDim);
		alphaField.setMaximumSize(textFieldDim);
		alphaField.setValue(DEFAULT_ALPHA);
		betaField.setPreferredSize(textFieldDim);
		betaField.setMinimumSize(textFieldDim);
		betaField.setMaximumSize(textFieldDim);
		betaField.setValue(DEFAULT_BETA);
		eliteSizeField.setPreferredSize(textFieldDim);
		eliteSizeField.setMinimumSize(textFieldDim);
		eliteSizeField.setMaximumSize(textFieldDim);
		eliteSizeField.setValue(DEFAULT_ELITE_SIZE);
		tournamentSizeField.setPreferredSize(textFieldDim);
		tournamentSizeField.setMinimumSize(textFieldDim);
		tournamentSizeField.setMaximumSize(textFieldDim);
		tournamentSizeField.setValue(DEFAULT_TOURNAMENT_SIZE);
		startField.setSelected(DEFAULT_START);

		JLabel populationLbl = new JLabel("Population size");
		JLabel pRotationLbl = new JLabel("<html>I Mutation probability<br/>(rotation based)</html>");
		JLabel pSwapLbl = new JLabel("<html>II Mutation probability<br/>(swap based)</html>");
		JLabel pOrderLbl = new JLabel("<html>III Mutation probability<br/>(order based)</html>");
		JLabel pCrossoverLbl = new JLabel("Crossover probability");
		JLabel alphaLbl = new JLabel("Fitness height weight (alpha)");
		JLabel betaLbl = new JLabel("Fitness density weight (beta)");
		JLabel eliteLbl = new JLabel("<html>Number of elite individual to<br/>save at each iteration</html>");
		JLabel tournamentLbl = new JLabel("Tournament size");
		JLabel startLbl = new JLabel("Intelligent start");

		
		completePane = new JPanel();
		GroupLayout layout = new GroupLayout(completePane);
		completePane.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(populationLbl)
						.addComponent(pRotationLbl)
						.addComponent(pSwapLbl)
						.addComponent(pOrderLbl)
						.addComponent(pCrossoverLbl)
						.addComponent(alphaLbl)
						.addComponent(betaLbl)
						.addComponent(eliteLbl)
						.addComponent(tournamentLbl)
						.addComponent(startLbl)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(populationField)
						.addComponent(pRotationField)
						.addComponent(pSwapField)
						.addComponent(pOrderField)
						.addComponent(pCrossoverField)
						.addComponent(alphaField)
						.addComponent(betaField)
						.addComponent(eliteSizeField)
						.addComponent(tournamentSizeField)
						.addComponent(startField)
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
						.addComponent(pSwapLbl)
						.addComponent(pSwapField)
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
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(eliteLbl)
						.addComponent(eliteSizeField)
				)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(tournamentLbl)
						.addComponent(tournamentSizeField)
				)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(startLbl)
						.addComponent(startField)
				)
		);
		
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return completePane;
	}

	@Override
	protected AbstractCore<TournamentConfiguration, ?> getConfiguredCore(CoreConfiguration<TournamentConfiguration> conf, OptimumPainter painter) {
		return new TournamentCore(conf, painter);
	}

	@Override
	protected TournamentConfiguration createCoreConfiguration() throws DataParsingException {
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
			throw new DataParsingException("Rotation probability should be in [0,1]");
		}
		
		
		Float sp = pSwapField.getValue();
		if (sp == null) {
			throw new DataParsingException("No swap probability specified");
		}
		if (sp.floatValue() < 0 || sp.floatValue() > 1) {
			throw new DataParsingException("Swap probability should be in [0,1]");
		}
		
		
		Float op = pOrderField.getValue();
		if (op == null) {
			throw new DataParsingException("No order mutation probability specified");
		}
		if (op.floatValue() < 0 || op.floatValue() > 1) {
			throw new DataParsingException("Order probability should be in [0,1]");
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

		
		Integer es = eliteSizeField.getValue();
		if (es == null) {
			throw new DataParsingException("You must specify the number of elite individual");
		}
		if (es.intValue() < 0 || es.intValue() > ps.intValue()) {
			throw new DataParsingException("The number of elite individual must be in [0,populationSize]");
		}
		
		
		Integer ts = tournamentSizeField.getValue();
		if (ts == null) {
			throw new DataParsingException("No tournament size specified");
		}
		if (ts.intValue() < 1 || ts.intValue() > ps.intValue()) {
			throw new DataParsingException("The tournament size must be in [1,populationSize]");
		}
		
		Boolean is = startField.isSelected();
		
		return new TournamentConfiguration(ps,rp,sp,op,cp,a,b,es,ts,is);
	}

	@Override
	protected void setConfiguration(TournamentConfiguration config) {
		populationField.setValue(config.getPopulationSize());
		pRotationField.setValue(config.getRotateMutationProbability());
		pSwapField.setValue(config.getSwapMutationProbability());
		pOrderField.setValue(config.getOrderMutationProbability());
		pCrossoverField.setValue(config.getCrossoverProbability());
		alphaField.setValue(config.getAlpha());
		betaField.setValue(config.getBeta());
		eliteSizeField.setValue(config.getEliteSize());
		tournamentSizeField.setValue(config.getTournamentSize());
	}

}