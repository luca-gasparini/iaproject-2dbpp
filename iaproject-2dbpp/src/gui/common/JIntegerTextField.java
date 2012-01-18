package gui.common;


import java.awt.event.FocusEvent;

import javax.swing.JFormattedTextField;

public class JIntegerTextField extends JFormattedTextField
{
	private static final long serialVersionUID = -2095400722967768602L;
	
	public JIntegerTextField()
	{
		super(new IntegerFormatter());
		this.setFocusLostBehavior(JFormattedTextField.COMMIT);
	}
	
	public JIntegerTextField(int value)
	{
		this();
		this.setValue(Integer.valueOf(value));
	}
	
	@Override
	public Integer getValue()
	{
		return (Integer)super.getValue();
	}
	
	@Override
	public void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
        if ( e.getID() == FocusEvent.FOCUS_GAINED )        
        	selectAll() ;
	}

}