package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import core.CoreDescriptor;
import core.CoreManager;

public class MainWindow extends AbstractFrame {

	private static final long serialVersionUID = 3500944474175026838L;
	
	private class CoreItem {
		private final String name;
		private final Class<? extends CoreDescriptor> descClass;
		private CoreDescriptor descriptor;
		
		public CoreItem(String name, Class<? extends CoreDescriptor> descClass) {
			this.name = name;
			this.descClass = descClass;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
		
		public CoreDescriptor getDescriptor() throws Exception {
			if (descriptor == null) {
				descriptor = descClass.newInstance();
			}
			
			return descriptor;
		}
	}
	
	private class EngineConfPanel extends JPanel {
		
		private static final long serialVersionUID = -3124313336856598663L;
		
		private final Color defaultColor; 
		private JComponent prevComponent;
		
		public EngineConfPanel() {
			super(new BorderLayout(0, 5));
			defaultColor = this.getBackground();
			
			TitledBorder titleBorder = BorderFactory.createTitledBorder("CORE CONFIGURATION");
			titleBorder.setTitleFont(GUIUtils.TITLE_FONT);
			titleBorder.setTitleJustification(TitledBorder.LEFT);
			this.setBorder(titleBorder);
			
			JComboBox coresCmb = new JComboBox();
			Map<String, Class<? extends CoreDescriptor>> cores = CoreManager.getCores();
			for (Entry<String, Class<? extends CoreDescriptor>> entity : cores.entrySet()) {
				coresCmb.addItem(new CoreItem(entity.getKey(), entity.getValue()));
			}
			coresCmb.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox box = (JComboBox) e.getSource();
					CoreItem item = (CoreItem) box.getSelectedItem();
					JComponent panel;
					try {
						panel = item.getDescriptor().getConfigurationComponent();
					} catch (Exception e1) {
						updateCorePanel(new JLabel(e1.toString()));
						EngineConfPanel.this.setBackground(Color.RED);
						return;
					}
					
					EngineConfPanel.this.setBackground(defaultColor);
					updateCorePanel(panel);
				}
			});
			
			JLabel initLabel = new JLabel("Select a CORE before starting");
			initLabel.setVerticalAlignment(JLabel.TOP);
			prevComponent = initLabel;
			
			this.add(coresCmb, BorderLayout.PAGE_START);
			this.add(prevComponent, BorderLayout.CENTER);
		}
		
		private void updateCorePanel(JComponent newComp) {
			this.remove(prevComponent);
			this.add(newComp, BorderLayout.CENTER);
			prevComponent = newComp;
			
			this.validate();
		}
	}
	
	public MainWindow() {
		super("IA Project - 2D Bin Packing Problem");
	}

	@Override
	public void initInterface() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setSize(1000, 650);
		this.setLocationRelativeTo(null);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 5));
		
		//****** PAGE_START *********//
		// empty
		
		//****** LINE_START *********//
		
		JButton confInputBtn = new JButton("CONFIGURE INPUT");
		confInputBtn.setFont(GUIUtils.BUTTON_FONT);
		
		JButton loadConfBtn = new JButton("LOAD CONFIGURATION");
		loadConfBtn.setFont(GUIUtils.BUTTON_FONT);
		
		JButton saveConfBtn = new JButton("SAVE CONFIGURATION");
		saveConfBtn.setFont(GUIUtils.BUTTON_FONT);
		
		JButton startCoreBtn = new JButton("START");
		startCoreBtn.setFont(GUIUtils.BUTTON_FONT);
		
		// lay them out in their panels
		JPanel enginePanel = new JPanel(new BorderLayout(0, 5));
		enginePanel.add(new EngineConfPanel(), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = GridBagConstraints.REMAINDER;
		buttonPanel.add(confInputBtn, con);
		con.gridwidth = 1; // reset
		
		con.gridx = 0;
		con.gridy = 1;
		buttonPanel.add(loadConfBtn, con);
		
		con.gridx = 1;
		con.gridy = 1;
		buttonPanel.add(saveConfBtn, con);
		
		con.gridx = 0;
		con.gridy = 2;
		con.gridwidth = GridBagConstraints.REMAINDER;
		buttonPanel.add(startCoreBtn, con);
		
		// add panels to the mainPanel
		JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
		
		leftPanel.add(enginePanel, BorderLayout.CENTER);
		leftPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		mainPanel.add(leftPanel, BorderLayout.LINE_START);
		
		
		//****** CENTER *********//
		// create components
		
		
		// add panels to the mainPanel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		
		centerPanel.add(Box.createHorizontalStrut(5));
		centerPanel.add(new JSeparator(JSeparator.VERTICAL));
		centerPanel.add(Box.createHorizontalStrut(5));
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		//****** LINE_END *********//
		// empty
		
		//****** PAGE_END *********//
		JLabel stateLabel = new JLabel("This is our state bar");
		stateLabel.setFont(GUIUtils.STATE_BAR_FONT);
		stateLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		
		
		JPanel statePanel = new JPanel();
		statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));
		statePanel.add(Box.createVerticalStrut(7));
		statePanel.add(new JSeparator(JSeparator.HORIZONTAL));
		statePanel.add(Box.createVerticalStrut(2));
		statePanel.add(stateLabel);
		
		mainPanel.add(statePanel, BorderLayout.PAGE_END);
		
		//****** FINALLY... *********//
		// ... add mainPanel to this frame
		this.add(mainPanel, BorderLayout.CENTER);
	}

}
