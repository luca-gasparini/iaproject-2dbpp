package core;

import gui.OptimumPainter;

import javax.swing.JComponent;

public interface CoreDescriptor {
	
	public JComponent getConfigurationComponent();
	
	public CoreController getConfiguredInstance(OptimumPainter painter) throws DataParsingException;
	
}
