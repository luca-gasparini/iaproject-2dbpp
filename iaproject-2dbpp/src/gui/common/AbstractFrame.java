package gui.common;

import javax.swing.JFrame;

public abstract class AbstractFrame extends JFrame {
	
	private static final long serialVersionUID = 6379062773201320543L;

	public AbstractFrame(String title, int closeOperation) {
		setTitle(title);
		this.setDefaultCloseOperation(closeOperation);
		
		// lay out all the components
		init();
	}
	
	protected abstract void init();
	
}
