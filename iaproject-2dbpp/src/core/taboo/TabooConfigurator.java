package core.taboo;

import gui.OptimumPainter;
import gui.common.JFloatTextField;
import gui.common.JIntegerTextField;

import javax.swing.JComponent;
import javax.swing.JPanel;

import core.AbstractConfigurator;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.DataParsingException;

public class TabooConfigurator extends AbstractConfigurator<TabooConfiguration> {
	
	public final JFloatTextField ALPHA = new JFloatTextField();
	public final JIntegerTextField DENSITY_FACTOR = new JIntegerTextField();
	public final JIntegerTextField HEIGHT_FACTOR = new JIntegerTextField();
	public final JIntegerTextField MAX_NEIGH_SIZE = new JIntegerTextField();
	public final JIntegerTextField D_MAX = new JIntegerTextField();
	public final JIntegerTextField FIRST_LIST_TENURE = new JIntegerTextField();
	public final JIntegerTextField OTHER_LIST_TENURE = new JIntegerTextField();
	
	public final JPanel configurationPanel;
	
	public TabooConfigurator() {
		configurationPanel = new JPanel();
		
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return configurationPanel;
	}

	@Override
	protected void setConfiguration(TabooConfiguration config) {
		ALPHA.setValue(Float.valueOf(config.ALPHA));
		DENSITY_FACTOR.setValue(Integer.valueOf(config.DENSITY_FACTOR));
		HEIGHT_FACTOR.setValue(Integer.valueOf(config.HEIGHT_FACTOR));
		MAX_NEIGH_SIZE.setValue(Integer.valueOf(config.MAX_NEIGH_SIZE));
		D_MAX.setValue(Integer.valueOf(config.D_MAX));
		FIRST_LIST_TENURE.setValue(Integer.valueOf(config.FIRST_LIST_TENURE));
		OTHER_LIST_TENURE.setValue(Integer.valueOf(config.OTHER_LIST_TENURE));
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
		
		int density_factor = DENSITY_FACTOR.getValue().intValue();
		if (density_factor < 0 || density_factor > 1) {
			throw new DataParsingException("Density factor must be [0, 1]");
		}
		
		int height_factor = HEIGHT_FACTOR.getValue().intValue();
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
		
		return new TabooConfiguration(alpha, density_factor, height_factor, 
				max_neigh_size, d_max, first_list_tenure, other_list_tenure);
	}

}
