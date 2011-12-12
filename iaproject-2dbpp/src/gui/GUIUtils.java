package gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public final class GUIUtils {
	
	public static final int BIG_FONT_SIZE = 14;
	public static final int BUTTON_FONT_SIZE = BIG_FONT_SIZE - 3;
	public static final int SMALL_FONT_SIZE = 10;
	
	public static final Font TITLE_FONT = new Font(Font.DIALOG, Font.BOLD, BIG_FONT_SIZE);
	public static final Font BUTTON_FONT = new Font(Font.MONOSPACED, Font.BOLD, BUTTON_FONT_SIZE);
	public static final Font STATE_BAR_FONT = new Font(Font.SERIF, Font.PLAIN, SMALL_FONT_SIZE);
	
	public static final JComponent getVerticalSeparator(int sizeBefore, int sizeAfter) {
		JPanel toRet = new JPanel();
		toRet.setLayout(new BoxLayout(toRet, BoxLayout.X_AXIS));
		
		toRet.add(Box.createHorizontalStrut(sizeBefore));
		toRet.add(new JSeparator(SwingConstants.VERTICAL));
		toRet.add(Box.createHorizontalStrut(sizeAfter));
		
		return toRet;
	}
	
	public static final JComponent getHorizontalSeparator(int sizeBefore, int sizeAfter) {
		JPanel toRet = new JPanel();
		toRet.setLayout(new BoxLayout(toRet, BoxLayout.Y_AXIS));
		
		toRet.add(Box.createVerticalStrut(sizeBefore));
		toRet.add(new JSeparator(SwingConstants.HORIZONTAL));
		toRet.add(Box.createVerticalStrut(sizeAfter));
		
		return toRet;
	}
	
	public static final void showErrorMessage(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
}
