package core.BLFTest;

import gui.OptimumPainter;

import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import logic.Packet;
import core.AbstractConfigurator;
import core.AbstractCore;
import core.Core2GuiTranslators;
import core.CoreConfiguration;
import core.DataParsingException;
import core.genetic.GeneticConfiguration;
import core.genetic.GeneticCore;

public class BLFTestConfigurator extends AbstractConfigurator<BLFTestCoreConfiguration> {
	private static final JPanel confComp = new JPanel();
	private JCheckBox rotateCheck;
	
	private List<Packet> packets;
	
	@Override
	public JComponent getConfigurationComponent() {
		
		rotateCheck = new JCheckBox("rotate 1 to 1");
		rotateCheck.setSelected(false);
		
		confComp.add(rotateCheck);
		return confComp;
	}

	@Override
	protected void setConfiguration(BLFTestCoreConfiguration config) {
		rotateCheck.setSelected(config.getSelected());
	}

	@Override
	protected BLFTestCoreConfiguration createCoreConfiguration() throws DataParsingException {
		/*if (this.packets == null) {
			return Collections.emptyList();
		} else {
			return this.packets;
		}*/
		boolean rotate = rotateCheck.isSelected();
		return new BLFTestCoreConfiguration(rotate);
	}
	
	@Override
	protected AbstractCore<BLFTestCoreConfiguration, ?> getConfiguredCore(CoreConfiguration<BLFTestCoreConfiguration> conf, OptimumPainter painter) {
		return new BLFTestCore(conf, painter);
	}


}
