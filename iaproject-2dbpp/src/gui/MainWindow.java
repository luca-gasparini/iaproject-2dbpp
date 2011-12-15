package gui;

import gui.common.AbstractFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import logic.ConfigurationManager;
import logic.ProblemConfiguration;
import util.Constants;
import core.CoreController;
import core.CoreDescriptor;
import core.CoreManager;
import core.DataParsingException;

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
			return getName();
		}
		
		public String getName() {
			return this.name;
		}
		
		public CoreDescriptor getDescriptor() throws Exception {
			/* if the application is well written, only EventDispatching
			 * thread should access this area, so there's no concurrency
			 * problem here (otherwise make the method synchronized)
			 */
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
		private JComboBox coresCmb;
		
		public EngineConfPanel() {
			defaultColor = this.getBackground();
			init();
		}
		
		private void init() {
			this.setLayout(new BorderLayout(0, 10));
			
			// add title to the configuration section
			TitledBorder titleBorder = BorderFactory.createTitledBorder("CORE CONFIGURATION");
			titleBorder.setTitleFont(GUIUtils.TITLE_FONT);
			titleBorder.setTitleJustification(TitledBorder.LEFT);
			this.setBorder(titleBorder);
			
			// create the ComboBox containing all Cores
			coresCmb = new JComboBox();
			Map<String, Class<? extends CoreDescriptor>> cores = CoreManager.getCores();
			
			if (cores.isEmpty()) {
				throw new IllegalStateException("There's no CORE available");
			}
			
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
			
			// this will cause the registered action listener to be fired
			coresCmb.setSelectedIndex(0);
			
			// lay out all created components
			JPanel comboPanel = new JPanel(new BorderLayout(5, 0));
			comboPanel.add(new JLabel("1) Choose a core: "), BorderLayout.LINE_START);
			comboPanel.add(coresCmb, BorderLayout.CENTER);
			
			this.add(comboPanel, BorderLayout.PAGE_START);
		}
		
		private void updateCorePanel(JComponent newComp) {
			if (prevComponent != null) {
				this.remove(prevComponent);
			}
			
			JLabel label = new JLabel("2) Configure it...");
			label.setHorizontalAlignment(SwingConstants.LEFT);
			Box labelBox = Box.createHorizontalBox();
			labelBox.add(label);
			labelBox.add(Box.createHorizontalGlue());
			
			newComp.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
			
			JPanel pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
			pane.add(labelBox);
			pane.add(newComp);
			pane.add(Box.createVerticalGlue());
			
			this.add(pane, BorderLayout.CENTER);
			prevComponent = pane;
			
			this.validate();
		}
		
		public CoreItem getChoosedCore() {
			return (CoreItem) coresCmb.getSelectedItem();
		}
		
		/**
		 * 
		 * @param name
		 * @throws IllegalArgumentException if specified core does not exist.
		 */
		public CoreItem setChoosedCore(String name) throws IllegalArgumentException {
			ComboBoxModel dataModel = coresCmb.getModel();
			
			for (int i = 0; i < dataModel.getSize(); i++) {
				CoreItem item = (CoreItem) dataModel.getElementAt(i);
				if (name.equals(item.getName())) {
					coresCmb.setSelectedIndex(i);
					return item;
				}
			}
			
			throw new IllegalArgumentException("No CORE with given name");
		}
	}
	
	private class OptimumPaintingPanel extends JPanel implements OptimumPainter {
		
		private static final long serialVersionUID = -2130969442515268077L;
		
		private JList binList;
		// use unmodifiable JTextField so text can be selected but not modified
		private JTextField nIteration;
		private JTextField fitnessValue;
		private JTextField nBins;
		private JTextField elapsedTime;
		private JPanel binDisplayer;
		
		public OptimumPaintingPanel() {
			init();
		}
		
		private void init() {
			TitledBorder optTitleBorder = BorderFactory.createTitledBorder("OPTIMUM DISPLAYER");
			optTitleBorder.setTitleFont(GUIUtils.TITLE_FONT);
			optTitleBorder.setTitleJustification(TitledBorder.LEFT);
			this.setBorder(optTitleBorder);
			
			// create components
			binList = new JList();
			binList.setVisibleRowCount(7);
			binList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			JScrollPane listScroller = new JScrollPane(binList, 
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			listScroller.setPreferredSize(new Dimension(150, 150));
			TitledBorder binTitle = BorderFactory.createTitledBorder("Bin List");
			listScroller.setBorder(binTitle);
			
			JLabel elapsedTimeLbl = new JLabel("elapsed time");
			elapsedTimeLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			nIteration = new JTextField(10);
			nIteration.setEditable(false);
			
			JLabel nIterationLbl = new JLabel("# iterations");
			nIterationLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			elapsedTime = new JTextField(10);
			elapsedTime.setEditable(false);
			
			JLabel fitnessValueLbl = new JLabel("fitness value");
			fitnessValueLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			fitnessValue = new JTextField(10);
			fitnessValue.setEditable(false);
			
			JLabel nBinsLbl = new JLabel("# bins");
			nBinsLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			nBins = new JTextField(10);
			nBins.setEditable(false);
			
			//JPanel binDisplayer = new JPanel(new BorderLayout());
			binDisplayer = new JPanel(new BorderLayout());
			binDisplayer.add(GUIUtils.getHorizontalSeparator(5, 5), BorderLayout.PAGE_START);
			
			// lay out components
			this.setLayout(new BorderLayout());
			
			JPanel infoPanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridy = 0;
			c.gridx = 0;
			c.insets = new Insets(0, 90, 0, 5);
			c.anchor = GridBagConstraints.LINE_END;
			infoPanel.add(nIterationLbl, c);
			
			c.gridy = 1;
			c.gridx = 0;
			infoPanel.add(elapsedTimeLbl, c);
			
			c.gridy = 2;
			c.gridx = 0;
			infoPanel.add(fitnessValueLbl, c);
			
			c.gridy = 3;
			c.gridx = 0;
			infoPanel.add(nBinsLbl, c);
			
			c.gridy = 0;
			c.gridx = 1;
			c.weightx = 0.2;
			c.insets = new Insets(2, 0, 2, 0);
			c.anchor = GridBagConstraints.LINE_START;
			infoPanel.add(nIteration, c);
			
			c.gridy = 1;
			c.gridx = 1;
			infoPanel.add(elapsedTime, c);
			
			c.gridy = 2;
			c.gridx = 1;
			infoPanel.add(fitnessValue, c);
			
			c.gridy = 3;
			c.gridx = 1;
			infoPanel.add(nBins, c);
			
			
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.add(listScroller, BorderLayout.LINE_START);
			topPanel.add(infoPanel, BorderLayout.CENTER);
			this.add(topPanel, BorderLayout.PAGE_START);
			
			this.add(binDisplayer, BorderLayout.CENTER);
			
		}

		@Override
		public void paint(GUIOptimum newOptimum) {
			// print only if core is running
			if (actualState == State.READY || actualState == State.CONFIGURATION) {
				return;
			}
			
			this.elapsedTime.setText(newOptimum.getElapsedTime() + "ms");
			this.fitnessValue.setText(Float.toString(newOptimum.getFitness()));
			this.nIteration.setText(Integer.toString(newOptimum.getNIterations()));
			this.nBins.setText(Integer.toString(newOptimum.getBins().size()));
			// TODO display bins!!!
			// Nicola C.: approccio per test
			binList.setListData(newOptimum.getBins().toArray());
			binDisplayer.add(newOptimum.getBins().get(0));
			
			binDisplayer.setBackground(Color.BLACK);
			binDisplayer.validate();
			
		}
		
		public void reset() {
			this.elapsedTime.setText(null);
			this.fitnessValue.setText(null);
			this.nIteration.setText(null);
			this.nBins.setText(null);
			this.binList.removeAll();
			// TODO remove displayed bins!!!
		}
	}
	
	public enum State {
		CONFIGURATION,
		READY,
		RUNNING,
		PAUSED
	}
	
	private OptimumPaintingPanel opp;
	private EngineConfPanel ecp;
	
	private JLabel stateLabel;
	
	private JButton confInputBtn;
	private JButton loadConfBtn;
	private JButton saveConfBtn;
	private JButton startCoreBtn;
	private JButton pauseBtn;
	private JButton resetBtn;
	
	private final ConfigurationManager configManager;
	
	private CoreController coreController;
	private ProblemConfiguration problemConf;
	
	private State actualState;
	
	public MainWindow(File configFile) {
		super("IA Project - 2D Bin Packing Problem", JFrame.EXIT_ON_CLOSE);
		this.configManager = new ConfigurationManager();
		
		if (configFile == null) {
			this.switchToState(State.CONFIGURATION);
			return;
		}
		
		try {
			loadConfiguration(configFile);
		} catch (ClassCastException e) {
			GUIUtils.showErrorMessage(MainWindow.this, e.toString());
		} catch (Exception e) {
			GUIUtils.showErrorMessage(MainWindow.this, e.toString());
		}
	}
	
	@Override
	public void init() {
		this.setSize(1100, 700);
		this.setLocationRelativeTo(null);
		
		ecp = new EngineConfPanel();
		opp = new OptimumPaintingPanel();
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 2, 3));
		
		//****** PAGE_START *********//
		// empty
		
		//****** LINE_START (engine configuration) *********//		
		confInputBtn = new JButton("CONFIGURE PROBLEM");
		confInputBtn.setFont(GUIUtils.BUTTON_FONT);
		confInputBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProblemConfigurer pc = new ProblemConfigurer(MainWindow.this, problemConf);
				problemConf = pc.askUser();
				
				if (problemConf != null) {
					switchToState(State.READY);
				}
			}
		});
		
		
		final JFileChooser fileChooser = new JFileChooser(Constants.CURRENT_DIR);
		
		loadConfBtn = new JButton("LOAD CONFIGURATION");
		loadConfBtn.setFont(GUIUtils.BUTTON_FONT);
		loadConfBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = fileChooser.showOpenDialog(MainWindow.this);
				
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				File configFile = fileChooser.getSelectedFile();
				try {
					loadConfiguration(configFile);
				} catch (ClassCastException e1) {
					GUIUtils.showErrorMessage(MainWindow.this, e1.toString());
				} catch (Exception e1) {
					GUIUtils.showErrorMessage(MainWindow.this, e1.toString());
				}
			}
		});
		
		saveConfBtn = new JButton("SAVE CONFIGURATION");
		saveConfBtn.setFont(GUIUtils.BUTTON_FONT);
		saveConfBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int ret = fileChooser.showSaveDialog(MainWindow.this);
				
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				File confFile = fileChooser.getSelectedFile();
				try {
					saveConfiguration(confFile);
				} catch (IOException e1) {
					GUIUtils.showErrorMessage(MainWindow.this, e1.toString());
				}
			}
		});
		
		startCoreBtn = new JButton("START");
		startCoreBtn.setFont(GUIUtils.BUTTON_FONT);
		startCoreBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (coreController != null) {
					GUIUtils.showErrorMessage(MainWindow.this, "There's an already running core: reset it first");
					return;
				}
				if (problemConf == null) {
					GUIUtils.showErrorMessage(MainWindow.this, "Please CONFIGURE PROBLEM first");
					return;
				}
				
				try {
					CoreDescriptor core = ecp.getChoosedCore().getDescriptor();
					coreController = core.getConfiguredInstance(problemConf, opp);
				} catch (Exception e1) {
					GUIUtils.showErrorMessage(MainWindow.this, e1.toString());
					return;
				}
				
				switch (actualState) {
				case READY:
					switchToState(State.RUNNING);
					break;
				default:
					GUIUtils.showErrorMessage(MainWindow.this, "Wrong state: should be in READY state");
					break;
				}
				
				coreController.start();
			}
		});
		
		// lay them out in their panels
		JPanel enginePanel = new JPanel(new BorderLayout(0, 5));
		enginePanel.add(ecp, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.BOTH;
		
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.insets = new Insets(0, 70, 2, 70);
		buttonPanel.add(confInputBtn, con);
		con.gridwidth = 1; // reset
		
		con.gridx = 0;
		con.gridy = 1;
		con.insets = new Insets(2, 10, 0, 10);
		buttonPanel.add(loadConfBtn, con);
		
		con.gridx = 1;
		con.gridy = 1;
		buttonPanel.add(saveConfBtn, con);
		
		con.gridx = 0;
		con.gridy = 2;
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.ipady = 6;
		con.insets = new Insets(20, 60, 0, 60);
		buttonPanel.add(startCoreBtn, con);
		
		// add panels to the mainPanel
		JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
		
		leftPanel.add(enginePanel, BorderLayout.CENTER);
		leftPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		mainPanel.add(leftPanel, BorderLayout.LINE_START);
		
		
		//****** CENTER (optimum displayer) *********//
		// create components
		pauseBtn = new JButton("PAUSE");
		pauseBtn.setFont(GUIUtils.BUTTON_FONT);
		pauseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (coreController == null) {
					GUIUtils.showErrorMessage(MainWindow.this, "Core is not started yet");
					return;
				}
				
				switch (actualState) {
				case CONFIGURATION:
				case READY:
					GUIUtils.showErrorMessage(MainWindow.this, "Wrong state");
					break;
				case PAUSED:
					switchToState(State.RUNNING);
					coreController.resume();
					break;
				case RUNNING:
					switchToState(State.PAUSED);
					coreController.pause();
					break;
				}
			}
		});
		
		resetBtn = new JButton("NEW EXECUTION");
		resetBtn.setFont(GUIUtils.BUTTON_FONT);
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (coreController == null) {
					GUIUtils.showErrorMessage(MainWindow.this, "Core is not started yet");
					return;
				}
				
				switch (actualState) {
				case READY:
				case CONFIGURATION:
					GUIUtils.showErrorMessage(MainWindow.this, "Core is not running: START it first");
					break;
					
				case RUNNING:
				case PAUSED:
					coreController.stop();
					coreController = null;
					switchToState(State.READY);
					break;
				}
			}
		});
		
		// add panels to the mainPanel
		JPanel btnPanel = new JPanel(new GridLayout(1, 0));
		btnPanel.add(Box.createGlue());
		btnPanel.add(pauseBtn);
		btnPanel.add(Box.createGlue());
		btnPanel.add(resetBtn);
		btnPanel.add(Box.createGlue());
		
		JPanel tmpPanel = new JPanel(new BorderLayout(0, 5));
		tmpPanel.add(opp, BorderLayout.CENTER);
		tmpPanel.add(btnPanel, BorderLayout.PAGE_END);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(GUIUtils.getVerticalSeparator(5, 5), BorderLayout.LINE_START);
		centerPanel.add(tmpPanel, BorderLayout.CENTER);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		//****** LINE_END *********//
		// empty
		
		//****** PAGE_END (state bar) *********//
		stateLabel = new JLabel("This is our state bar");
		stateLabel.setFont(GUIUtils.STATE_BAR_FONT);
		stateLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		
		JPanel statePanel = new JPanel(new BorderLayout());
		statePanel.add(GUIUtils.getHorizontalSeparator(7, 2), BorderLayout.PAGE_START);
		statePanel.add(stateLabel, BorderLayout.PAGE_END);
		
		mainPanel.add(statePanel, BorderLayout.PAGE_END);
		
		//****** FINALLY... *********//
		// ... add mainPanel to this frame
		this.add(mainPanel, BorderLayout.CENTER);
	}
	
	private void loadConfiguration(File configFile) throws ClassCastException, Exception {
		if (coreController != null) {
			throw new Exception("There's a RUNNING core: please stop it first");
		}
		
		this.configManager.loadFromFile(configFile);
		
		CoreItem item = this.ecp.setChoosedCore(configManager.getCoreName());
		item.getDescriptor().setCoreConfiguration(configManager.getCoreConfiguration());
		this.problemConf = this.configManager.getProblemConfiguration();
		
		this.switchToState(State.READY);
	}
	
	private void saveConfiguration(File configfile) throws IOException {
		if (this.problemConf == null) {
			throw new IOException("Problem configuration is still empty");
		}
		
		CoreItem choosedCore = ecp.getChoosedCore();
		if (choosedCore == null) {
			throw new IOException("Haven't choosed a core yet");
		}
		
		CoreDescriptor coreDesc;
		try {
			coreDesc = choosedCore.getDescriptor();
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		Object coreConfig;
		try {
			coreConfig = coreDesc.getCoreConfiguration();
		} catch (DataParsingException e) {
			throw new IOException(e);
		}
		
		this.configManager.setProblemConfiguration(problemConf);
		this.configManager.setCoreName(choosedCore.getName());
		this.configManager.setCoreConfiguration(coreConfig);
		
		this.configManager.saveToFile(configfile);
	}
	
	private void switchToState(State newState) {
		
		switch (newState) {
		case CONFIGURATION:
			stateLabel.setText("Waiting for CONFIGURATION");
			pauseBtn.setText("PAUSE");
			opp.reset();
			
			break;
			
		case READY:
			stateLabel.setText("Core READY");
			pauseBtn.setText("PAUSE");
			opp.reset();
			
			break;
			
		case RUNNING:
			stateLabel.setText("Core RUNNING");
			pauseBtn.setText("PAUSE");
			
			break;
			
		case PAUSED:
			stateLabel.setText("Core PAUSED");
			pauseBtn.setText("RESUME");
			
			break;
		}
		
		this.actualState = newState;
	}

}
