package core;

import gui.OptimumPainter;

import javax.swing.JComponent;

import logic.ProblemConfiguration;

public interface CoreDescriptor {
	
	public JComponent getConfigurationComponent();
	
	public CoreController getConfiguredInstance(ProblemConfiguration conf, OptimumPainter painter) throws DataParsingException;
	
	/**
	 * 
	 * @return can be null if core has no configuration
	 */
	public Object getCoreConfiguration() throws DataParsingException ;
	
	/**
	 * 
	 * @param conf can be null that means don't configure
	 */
	public void setCoreConfiguration(Object conf) throws ClassCastException;
}
