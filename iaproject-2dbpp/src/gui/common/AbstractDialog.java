package gui.common;

import gui.GUIUtils;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import core.DataParsingException;

public abstract class AbstractDialog<T> extends JDialog {
	
	private static final long serialVersionUID = -7417147782920757953L;
	
	private T value;
	private JButton cancelBtn;
	private JButton saveBtn;
	
	/**
	 * 
	 * @param parent
	 * @param title
	 * @param oldValue can be null, that means "no old value"
	 */
	public AbstractDialog(Window parent, String title, T oldValue) {
		super(parent);
		
		this.value = oldValue;
		
		this.setModal(true);
		this.setTitle(title);
		this.setSize(700, 600);
		
		this.setLayout(new BorderLayout(0, 10));
		initContent();
		initButtons();
		
		if (value != null) {
			paintValue(value);
		}
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	/**
	 * Note: value can be null
	 * @param value
	 */
	protected abstract void paintValue(T value);
	
	/**
	 * Returns value now contained in the GUI
	 * @return
	 * @throws DataParsingException 
	 */
	protected abstract T getUIValue() throws DataParsingException;
	protected abstract JComponent buildValuePainter();
	
	private void initContent() {
		JComponent valuePainter = buildValuePainter();
		
		Box box = Box.createVerticalBox();
		box.add(valuePainter);
		box.add(Box.createVerticalGlue());
		
		this.add(box, BorderLayout.CENTER);
	}
	
	private void initButtons() {
		cancelBtn = new JButton("ANNULLA");
		cancelBtn.setFont(GUIUtils.BUTTON_FONT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCancel();
			}
		});
		
		saveBtn = new JButton("SALVA");
		saveBtn.setFont(GUIUtils.BUTTON_FONT);
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});
		
		JPanel btnPanel = new JPanel(new GridLayout(1, 0));
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(cancelBtn);
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(saveBtn);
		btnPanel.add(Box.createHorizontalGlue());
		
		this.add(btnPanel, BorderLayout.PAGE_END);
	}
	
	protected final void doSave() {
		try {
			this.value = getUIValue();
		} catch (DataParsingException e) {
			GUIUtils.showErrorMessage(this, e.toString());
			return;
		}
		this.setVisible(false);
		this.dispose();
	}
	
	protected final void doCancel() {
		// don't update value
		this.setVisible(false);
		this.dispose();
	}
	
	public T getValue() {
		return value;
	}
	
}
