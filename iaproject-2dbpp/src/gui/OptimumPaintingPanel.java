package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

public class OptimumPaintingPanel extends JPanel {

	private static final long serialVersionUID = -2130969442515268077L;
	
	private class TokenPainter implements OptimumPainter {
		@Override
		public void paint(GUIOptimum newOptimum) {
			OptimumPaintingPanel.this.askPaint(newOptimum, TokenPainter.this);
		}
	}
	
	private class BinListModel extends AbstractListModel {
		private static final long serialVersionUID = 8557643349450515292L;
		private List<GUIBin> actualList = Collections.emptyList(); 
		
		public void setData(List<GUIBin> bins) {
			removeAllData();
			
			actualList = bins;
			if (bins.size() == 0) return;
			fireIntervalAdded(this, 0, actualList.size() - 1);
		}
		
		public void removeAllData() {
			int size = actualList.size();
			if (size == 0) {
				return;
			}
			
			actualList = Collections.emptyList();
			fireIntervalRemoved(this, 0, size - 1);
		}

		@Override
		public int getSize() {
			return actualList.size();
		}

		@Override
		public Object getElementAt(int index) {
			return actualList.get(index);
		}
	}
	
	private BinListModel binList;
	/**
	 * Please note that although elements are inserted sequentially,
	 * they are printed backwards by overriding the 
	 * {@link DefaultListModel#getElementAt(int)} method.
	 */
	private DefaultListModel optimumList;
	// use unmodifiable JTextField so text can be selected but not modified
	private JTextField nIteration;
	private JTextField fitnessValue;
	private JTextField nBins;
	private JTextField elapsedTime;
	private JPanel binDisplayer;
	private TokenPainter lastToken = null;
	
	private List<Box> boxList = new LinkedList<Box>();
	private int binPerRow = -1;
	private int magnificationFactor = -1;
	private final int MAX_BIN_PER_ROW = 3;
	private boolean firstPrint = true;
	
	public OptimumPaintingPanel() {
		init();
	}
	
	private void init() {
		TitledBorder optTitleBorder = BorderFactory.createTitledBorder("OPTIMUM DISPLAYER");
		optTitleBorder.setTitleFont(GUIUtils.TITLE_FONT);
		optTitleBorder.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(optTitleBorder);
		
		// create components
		JList binJList = new JList();
		binList = new BinListModel();
		binJList.setModel(binList);
		binJList.setVisibleRowCount(7);
		binJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane binListScroller = new JScrollPane(binJList, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		binListScroller.setPreferredSize(new Dimension(150, 150));
		TitledBorder binTitle = BorderFactory.createTitledBorder("Bin List");
		binListScroller.setBorder(binTitle);
		
		final JList optimumJList = new JList();
		optimumList = new DefaultListModel() {
			private static final long serialVersionUID = 6446790039057560056L;
			@Override
			public Object getElementAt(int index) {
				return super.getElementAt(size() - 1 - index);
			}
		};
		optimumJList.setModel(optimumList);
		optimumJList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 9056812305053790194L;
			private static final String startStr = "Opt. #"; 
			private final StringBuilder builder = new StringBuilder(startStr);
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				GUIOptimum opt = (GUIOptimum) value;
				builder.setLength(startStr.length());
				builder.append(list.getModel().getSize()-index).append(' ').
						append('[').append(opt.getBins().size()).append("] ").
						append('@').append(GUIUtils.elapsedTime2String(opt.getElapsedTime()));
				return super.getListCellRendererComponent(list, builder.toString(), index, isSelected,
						cellHasFocus);
			}
		});
		optimumJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					GUIOptimum opt = (GUIOptimum) optimumJList.getSelectedValue();
					if (opt == null) return;
					OptimumPaintingPanel.this.paintOptimum(opt);
				}
			}
		});
		optimumJList.setVisibleRowCount(7);
		optimumJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane optListScroller = new JScrollPane(optimumJList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		optListScroller.setPreferredSize(new Dimension(240, 150));
		TitledBorder optListTitle = BorderFactory.createTitledBorder("Optimum history");
		optListScroller.setBorder(optListTitle);
		
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
		
		binDisplayer = new JPanel();
		JScrollPane scrollPane = new JScrollPane(binDisplayer, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel centerPane = new JPanel(new BorderLayout());
		centerPane.add(GUIUtils.getHorizontalSeparator(5, 5), BorderLayout.PAGE_START);
		centerPane.add(scrollPane, BorderLayout.CENTER);
		
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
		topPanel.add(binListScroller, BorderLayout.LINE_START);
		topPanel.add(infoPanel, BorderLayout.CENTER);
		topPanel.add(optListScroller, BorderLayout.LINE_END);
		this.add(topPanel, BorderLayout.PAGE_START);
		
		this.add(centerPane, BorderLayout.CENTER);
		
	}
	
	public void askPaint(GUIOptimum newOptimum, OptimumPainter painter) {
		// print only if given painter is the last one (avoid spurious)
		if (this.lastToken == null || !this.lastToken.equals(painter)) {
			return;
		}
		
		this.optimumList.addElement(newOptimum);
		
		paintOptimum(newOptimum);
	}
	
	private GUIOptimum lastPaintedOpt;
	
	public void paintOptimum(GUIOptimum newOptimum) {
		
		List<GUIBin> bins = newOptimum.getBins();
		assert bins.size() > 0 : "NO BINS NO PARTY";
		if (lastPaintedOpt == null) lastPaintedOpt = newOptimum;
		
		this.binList.setData(bins);
		this.elapsedTime.setText(GUIUtils.elapsedTime2String(newOptimum.getElapsedTime()));
		this.fitnessValue.setText(Float.toString(newOptimum.getFitness()));
		this.nIteration.setText(Integer.toString(newOptimum.getNIterations()));
		this.nBins.setText(Integer.toString(bins.size()));
		/* **** DO NOT ADD newOptimum TO optimumList HERE!! ****
		 * in fact this method could be called also to display an optimum
		 * from history
		 */
		
		// ***** display bins *****
		
		/* first calculate maxBinPerRow, magnification factor and set layout.
		 * This should be done at first print OR every time # of bins <
		 * actual binPerRow OR every time current optimum has more bins
		 * than the previous one (this last case should happens only during
		 * testing)
		 */
		if (firstPrint || bins.size() < binPerRow || 
				bins.size() > lastPaintedOpt.getBins().size()) {
			
			firstPrint = false;
			
			Dimension binSize = bins.get(0).getBaseDimension();
			// use dimension of binDisplayer ScrollPane
			Dimension paneSize = binDisplayer.getParent().getSize();
			int hSpace = 5, vSpace = 5;
			
			// calculate maximum bin per row...
			int tmpBinPerRow = (paneSize.width + hSpace) / (binSize.width + hSpace);
			// if tmpBinPerRow < 1 => set at least 1
			tmpBinPerRow = Math.max(tmpBinPerRow, 1);
			binPerRow = Math.min(bins.size(), Math.min(tmpBinPerRow, MAX_BIN_PER_ROW));
			
			/* calculate magnification factor checking also that 
			 * bin height will remain inside the binDispalyer height
			 */
			int widthAvailable = (paneSize.width - binPerRow * hSpace) / binPerRow;
			int magnificationFactorW = Math.max(widthAvailable / binSize.width, 1);
			int magnificationFactorH = Math.max(paneSize.height / binSize.height, 1);
			magnificationFactor = Math.min(magnificationFactorW, magnificationFactorH);
			
			// set layout manager
			binDisplayer.setLayout(new GridLayout(0, binPerRow, hSpace, vSpace));
		}
		
		// check if there is enough boxes to display all bins
		if (boxList.size() < bins.size()) {
			for (int i = boxList.size(); i < bins.size(); i++) {
				Box binContainer = Box.createVerticalBox();
				boxList.add(binContainer);
				binDisplayer.add(binContainer);
			}
			// binDisplayer layout is changed -> need revalidation
			binDisplayer.revalidate();
		}
		
		Iterator<Box> boxIt = boxList.iterator();
		for (GUIBin bin : bins) {
			
			bin.setMagnificationFactor(magnificationFactor);
			// get panel (previous code ensure that there is enough boxes)
			Box binPanel = boxIt.next();
			
			/* TODO in the future check if current bin is already painted 
			 * (i.e. is the same as previous run)
			 */
			binPanel.removeAll();
			
			bin.setAlignmentX(Component.CENTER_ALIGNMENT);
			binPanel.add(bin);
			binPanel.add(Box.createVerticalStrut(5));
			JLabel label = new JLabel("Bin " + bin.toString());
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			binPanel.add(label);
			
			binPanel.revalidate();
			binPanel.repaint();
		}
		
		while (boxIt.hasNext()) {
			Box box = (Box) boxIt.next();
			box.removeAll();
			box.revalidate();
			box.repaint();
		}
		
		lastPaintedOpt = newOptimum;
	}
	
	public OptimumPainter setUp() {
		reset();
		
		this.lastToken = new TokenPainter();
		return this.lastToken;
	}
	
	public void reset() {
		this.lastToken = null;
		
		this.elapsedTime.setText(null);
		this.fitnessValue.setText(null);
		this.nIteration.setText(null);
		this.nBins.setText(null);
		this.binList.removeAllData();
		this.optimumList.removeAllElements();
		
		this.firstPrint = true;
		this.boxList.clear();
		
		this.binDisplayer.removeAll();
		this.binDisplayer.revalidate();
		this.binDisplayer.repaint();
	}

}
