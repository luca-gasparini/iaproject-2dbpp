package gui.common;

import java.awt.event.FocusEvent;

import javax.swing.JFormattedTextField;

public class JFloatTextField extends JFormattedTextField {
	private static final long serialVersionUID = -4375283121760690354L;
	

	public JFloatTextField() {
		super(new FloatFormatter());
		this.setFocusLostBehavior(JFormattedTextField.COMMIT);
	}
	
	public JFloatTextField(float value) {
		this();
		this.setValue(Float.valueOf(value));
	}
	
	@Override
	public Float getValue()
	{
		return (Float)super.getValue();
	}
	
	@Override
	public void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
        if ( e.getID() == FocusEvent.FOCUS_GAINED )        
        	selectAll() ;
	}
	
}
