package gui.common;

import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class FloatFormatter extends AbstractFormatter {
	private static final long serialVersionUID = -3452869412540973015L;

	@Override
	public Float stringToValue(String text) throws ParseException {
		try {
			return Float.valueOf(text);
		} catch (NumberFormatException nfe) {
			throw new ParseException(nfe.getMessage(), -1);
		}
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (!(value instanceof Float)) {
			throw new ParseException("Wrong object type", -1);
		}
		
		// thanks to polymorfism I don't need a cast
		return value.toString();
	}
	
}
