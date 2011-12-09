package gui;

import javax.swing.JFrame;

public abstract class AbstractFrame extends JFrame {
	
	private static final long serialVersionUID = 6379062773201320543L;

	public AbstractFrame(String title) {
		setTitle(title);
		
		// lay out all the components
		initInterface();
		
		// pack & validate composed frame
		this.validate();
	}
	
	protected abstract void initInterface();
	
}
