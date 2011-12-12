package core;

import gui.OptimumPainter;

import javax.swing.JComponent;

import logic.ProblemConfiguration;

public interface CoreDescriptor {
	
	public JComponent getConfigurationComponent();
	
	public CoreController getConfiguredInstance(ProblemConfiguration conf, OptimumPainter painter) throws DataParsingException;
	
}
