package core.dummy;

import java.awt.FlowLayout;

import gui.OptimumPainter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.CoreController;
import core.CoreDescriptor;
import core.DataParsingException;

public class DummyConfigurator implements CoreDescriptor {
	
	private final JTextField tf = new JTextField(10);
	private final JPanel completePane;
	
	public DummyConfigurator() {
//		throw new IllegalArgumentException("Test Exception");
		completePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		completePane.add(new JLabel("Max ms to wait"));
		completePane.add(tf);
	}
	
	@Override
	public JComponent getConfigurationComponent() {
		return completePane;
	}

	@Override
	public CoreController getConfiguredInstance(OptimumPainter painter) throws DataParsingException {
		// parse configuration panel in order to get desired input
		int ms;
		try {
			ms = Integer.parseInt(tf.getText());
		} catch (NumberFormatException nfe) {
			throw new DataParsingException(nfe.getMessage());
		}
		
		// create configured Core
		DummyCore core = new DummyCore(painter, ms);
		return core.getController();
	}

}
