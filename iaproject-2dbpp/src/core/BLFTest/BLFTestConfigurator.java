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
import core.CoreConfiguration;
import core.DataParsingException;

public class BLFTestConfigurator extends AbstractConfigurator<BLFTestCoreConfiguration> {
	private static final JPanel confComp = new JPanel();
	
	private final JCheckBox rotateCheck;
	private List<Packet> packets;
	
	public BLFTestConfigurator() {

		rotateCheck = new JCheckBox("rotate 1 to 1");
		rotateCheck.setSelected(false);
		
		confComp.add(rotateCheck);
		
	}
	@Override
	public JComponent getConfigurationComponent() {
		return confComp;
	}

	@Override
	protected AbstractCore<BLFTestCoreConfiguration, ?> getConfiguredCore(CoreConfiguration<BLFTestCoreConfiguration> conf, OptimumPainter painter) {
		return new BLFTestCore(conf, painter);
	}
	
	@Override
	protected void setConfiguration(BLFTestCoreConfiguration config) {
		rotateCheck.setSelected(config.isSelected());
		this.packets = config.getPackets();
	}

	@Override
	protected BLFTestCoreConfiguration createCoreConfiguration() throws DataParsingException {
		if (this.packets == null) {
			packets = Collections.emptyList();
		}
		boolean rotate = rotateCheck.isSelected();
		
		return new BLFTestCoreConfiguration(rotate, packets);
	}
	
}
