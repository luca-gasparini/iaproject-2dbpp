package gui;

import java.awt.Font;

public final class GUIUtils {
	
	public static final int BIG_FONT_SIZE = 14;
	public static final int BUTTON_FONT_SIZE = BIG_FONT_SIZE - 3;
	public static final int SMALL_FONT_SIZE = 10;
	
	public static final Font TITLE_FONT = new Font(Font.DIALOG, Font.BOLD, BIG_FONT_SIZE);
	public static final Font BUTTON_FONT = new Font(Font.MONOSPACED, Font.BOLD, BUTTON_FONT_SIZE);
	public static final Font STATE_BAR_FONT = new Font(Font.SERIF, Font.PLAIN, SMALL_FONT_SIZE);
}
