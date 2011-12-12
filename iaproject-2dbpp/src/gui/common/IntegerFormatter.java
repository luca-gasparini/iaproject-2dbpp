package gui.common;


import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class IntegerFormatter extends AbstractFormatter
{

	private static final long serialVersionUID = -2149572692383613541L;

	public IntegerFormatter()
	{

	}

	@Override
	public Integer stringToValue(String arg0) throws ParseException
	{
		if (arg0 == null || arg0.equals(""))
			return null;
		try
		{
			return new Integer(arg0);
		} catch (NumberFormatException nfe)
		{
			throw new ParseException(nfe.getMessage(), -1);
		}
	}

	@Override
	public String valueToString(Object arg0) throws ParseException
	{
		if (!(arg0 instanceof Integer))
			throw new ParseException("Invalid Object", -1);
		return arg0.toString();
	}

}