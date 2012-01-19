package core.taboo;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.DataParsingException;

public class TabooConfigurator extends AbstractConfigurator<TabooConfiguration> {
	
	public final JFloatTextField ALPHA = new JFloatTextField((float) 3.5);
	public final JFloatTextField DENSITY_FACTOR = new JFloatTextField((float) 0.9);
	public final JFloatTextField HEIGHT_FACTOR = new JFloatTextField(0);
	public final JIntegerTextField MAX_NEIGH_SIZE = new JIntegerTextField(4);
	public final JIntegerTextField D_MAX = new JIntegerTextField(4);
	public final JIntegerTextField FIRST_LIST_TENURE = new JIntegerTextField(30);
	public final JIntegerTextField OTHER_LIST_TENURE = new JIntegerTextField(30);
	public final JCheckBox IMPROVEBLF = new JCheckBox();
	
	public final JPanel configurationPanel;
	
	public TabooConfigurator() {
		configurationPanel = new JPanel();
		
		JLabel alphaLbl = new JLabel("<html>ALPHA<br/>(Filling func. parm [def 20])</html>");
		JLabel densityFactorLbl = new JLabel("<html>Density gain<br/>(Fitness func. parm)</html>");
		JLabel heightFactorLbl = new JLabel("<html>Height gain<br/>(Fitness function parm)</html>");
		JLabel maxNeighSizeLbl = new JLabel("<html>Max neighbours size</html>");
		JLabel dMaxLbl = new JLabel("<html>d_max</html>");
		JLabel firstListTenureLbl = new JLabel("<html>First list tenure</html>");
		JLabel otherListTenureLbl = new JLabel("<html>Other list tenure</html>");
		JLabel improveBLF = new JLabel("<html>Adatta meglio al BLF</html>");
		
		GroupLayout gp = new GroupLayout(configurationPanel);
		configurationPanel.setLayout(gp);
		gp.setAutoCreateContainerGaps(true);
		gp.setAutoCreateGaps(true);
		gp.setHorizontalGroup(gp.createSequentialGroup()
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(alphaLbl)
						.addComponent(densityFactorLbl)
						.addComponent(heightFactorLbl)
						.addComponent(maxNeighSizeLbl)
						.addComponent(dMaxLbl)
						.addComponent(firstListTenureLbl)
						.addComponent(otherListTenureLbl)
						.addComponent(improveBLF)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(ALPHA)
						.addComponent(DENSITY_FACTOR)
						.addComponent(HEIGHT_FACTOR)
						.addComponent(MAX_NEIGH_SIZE)
						.addComponent(D_MAX)
						.addComponent(FIRST_LIST_TENURE)
						.addComponent(OTHER_LIST_TENURE)
						.addComponent(IMPROVEBLF)
						)
		);
		gp.setVerticalGroup(gp.createSequentialGroup()
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(alphaLbl)
						.addComponent(ALPHA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(densityFactorLbl)
						.addComponent(DENSITY_FACTOR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(heightFactorLbl)
						.addComponent(HEIGHT_FACTOR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(maxNeighSizeLbl)
						.addComponent(MAX_NEIGH_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(dMaxLbl)
						.addComponent(D_MAX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(firstListTenureLbl)
						.addComponent(FIRST_LIST_TENURE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(otherListTenureLbl)
						.addComponent(OTHER_LIST_TENURE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				.addGroup(gp.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(improveBLF)
						.addComponent(IMPROVEBLF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						)
		);
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return configurationPanel;
	}

	@Override
	protected void setConfiguration(TabooConfiguration config) {
		ALPHA.setValue(Float.valueOf(config.ALPHA));
		DENSITY_FACTOR.setValue(Float.valueOf(config.DENSITY_FACTOR));
		HEIGHT_FACTOR.setValue(Float.valueOf(config.HEIGHT_FACTOR));
		MAX_NEIGH_SIZE.setValue(Integer.valueOf(config.MAX_NEIGH_SIZE));
		D_MAX.setValue(Integer.valueOf(config.D_MAX));
		FIRST_LIST_TENURE.setValue(Integer.valueOf(config.FIRST_LIST_TENURE));
		OTHER_LIST_TENURE.setValue(Integer.valueOf(config.OTHER_LIST_TENURE));
		IMPROVEBLF.setSelected(config.IMPROVEBLF);
	}

	@Override
	protected AbstractCore<TabooConfiguration, ?> getConfiguredCore(
			CoreConfiguration<TabooConfiguration> conf, OptimumPainter painter) {
		return new TabooCore(conf, painter, Core2GuiTranslators.getBinListTranslator());
	}

	@Override
	protected TabooConfiguration createCoreConfiguration()
			throws DataParsingException {
		
		float alpha = ALPHA.getValue().floatValue();
		if (Float.compare(alpha, 0) <= 0) {
			throw new DataParsingException("Alpha must be positive");
		}
		
		float density_factor = DENSITY_FACTOR.getValue().floatValue();
		if (density_factor < 0 || density_factor > 1) {
			throw new DataParsingException("Density factor must be [0, 1]");
		}
		
		float height_factor = HEIGHT_FACTOR.getValue().floatValue();
		if (height_factor < 0 || height_factor > 1) {
			throw new DataParsingException("Height factor must be [0, 1]");
		}
		
		int max_neigh_size = MAX_NEIGH_SIZE.getValue().intValue();
		if (max_neigh_size <= 0) {
			throw new DataParsingException("Neighbours size must be positive");
		}
		
		int d_max = D_MAX.getValue().intValue();
		if (d_max <= 0) {
			throw new DataParsingException("d_max must be positive");
		}
		
		int first_list_tenure = FIRST_LIST_TENURE.getValue().intValue();
		if (first_list_tenure <= 0) {
			throw new DataParsingException("First list tenure must be positive");
		}
		
		int other_list_tenure = OTHER_LIST_TENURE.getValue().intValue();
		if (other_list_tenure <= 0) {
			throw new DataParsingException("Other list tenure must be positive");
		}
		
		boolean improve_blf = IMPROVEBLF.isSelected();
		
		return new TabooConfiguration(alpha, density_factor, height_factor, 
				max_neigh_size, d_max, first_list_tenure, other_list_tenure, improve_blf);
	}

}
