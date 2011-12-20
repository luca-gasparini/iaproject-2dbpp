package gui;

import gui.common.AbstractDialog;
import gui.common.JIntegerTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import logic.BinConfiguration;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;
import core.DataParsingException;

public class ProblemConfigurer extends AbstractDialog<ProblemConfiguration> {
	
	private static final long serialVersionUID = -1111598546169426454L;
	
	private class PacketGraphic extends ResizableRawGraphics {
		private static final long serialVersionUID = -316443024628550817L;
		
		private static final int MAGNIFYING = 5;
		
		private final PacketConfiguration pc;
		
		public PacketGraphic(PacketConfiguration pc) {
			super(pc.getSize());
			
			this.pc = pc;
			this.setMagnificationFactor(MAGNIFYING);
		}
		
		@Override
		protected void doPaint(Graphics2D g2d, int factor) {
			Rectangle rect = new Rectangle(pc.getWidth() * factor, pc.getHeight() * factor);
			
			g2d.setColor(pc.getColor());
			g2d.fill(rect);
			
			g2d.setColor(Color.BLACK);
			g2d.draw(rect);
		}
	}
	
	private class PacketListRender implements ListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Box theBox = Box.createHorizontalBox();
			
			PacketConfiguration pc = (PacketConfiguration) value;
			
			JLabel dimensionLbl = new JLabel(pc.getWidth() + "x" + pc.getHeight());
			JLabel numberLbl = new JLabel("#" + pc.getMolteplicity());
			
			JPanel packetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			packetPanel.setOpaque(false);
			packetPanel.add(new PacketGraphic(pc));
			
			theBox.add(packetPanel);
			theBox.add(Box.createHorizontalStrut(100));
			theBox.add(dimensionLbl);
			theBox.add(Box.createHorizontalStrut(20));
			theBox.add(numberLbl);
			theBox.add(Box.createHorizontalGlue());
			
			if (isSelected) {
				theBox.setBackground(list.getSelectionBackground());
				theBox.setForeground(list.getSelectionForeground());
			} else {
				theBox.setBackground(list.getBackground());
				theBox.setForeground(list.getForeground());
			}
			theBox.setEnabled(list.isEnabled());
			theBox.setFont(list.getFont());
			theBox.setOpaque(true);
			
			return theBox;
		}
		
	}
	
	public ProblemConfigurer(Window parent,
			ProblemConfiguration oldValue) {
		super(parent, "Problem configuration", oldValue);
	}
	
	private DefaultListModel packetListModel;
	private JIntegerTextField binWidth;
	private JIntegerTextField binHeight;
	
	@Override
	protected void paintValue(ProblemConfiguration value) {
		binWidth.setValue(Integer.valueOf(value.getBin().getWidth()));
		binHeight.setValue(Integer.valueOf(value.getBin().getHeight()));
		
		packetListModel.removeAllElements();
		for (PacketConfiguration pc : value.getPackets()) {
			packetListModel.addElement(pc);
		}
	}

	@Override
	protected ProblemConfiguration getUIValue() throws DataParsingException {
		Integer binW = binWidth.getValue();
		Integer binH = binHeight.getValue();
		
		if (binW == null || binH == null) {
			throw new DataParsingException("Wrong bin dimension");
		}
		BinConfiguration bc = new BinConfiguration(binW.intValue(), binH.intValue());
		
		PacketConfiguration[] tmp = new PacketConfiguration[packetListModel.size()];
		
		packetListModel.copyInto(tmp);
		
		/*
		 * check max
		 */
		int maxW = 0;
		int maxH = 0;
		for (int i=0; i<tmp.length; i++) {
			if (maxW<tmp[i].getWidth())
				maxW = tmp[i].getWidth();
			if (maxH<tmp[i].getHeight())
				maxH = tmp[i].getHeight();
		}
		if (maxW>binH || maxH>binH) {
			throw new DataParsingException("Bin TOO small");
		}
		
		List<PacketConfiguration> packets = Arrays.asList(tmp);
		
		return new ProblemConfiguration(bc, packets);
	}
	
	/**
	 * 
	 * @param selectedRow can be negative that means no selection.
	 */
	private void modifyPacketList(int selectedRow) {
		PacketConfiguration[] tmp = new PacketConfiguration[packetListModel.size()];
		packetListModel.copyInto(tmp);
		
		PacketsConfigurer packConf = new PacketsConfigurer(ProblemConfigurer.this, tmp);
		if (selectedRow >= 0) {
			packConf.ensureSelectedRow(selectedRow);
		}
		PacketConfiguration[] newPackets = packConf.askUser();
		
		packetListModel.removeAllElements();
		for (PacketConfiguration pc : newPackets) {
			packetListModel.addElement(pc);
		}
	}

	@Override
	protected JComponent buildValuePainter() {
		// Packet configuration
		JButton editBtn = new JButton("EDIT");
		editBtn.setFont(GUIUtils.BUTTON_FONT);
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modifyPacketList(-1);
			}
		});
		
		JButton dropAllBtn = new JButton("DROP ALL");
		dropAllBtn.setFont(GUIUtils.BUTTON_FONT);
		dropAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				packetListModel.removeAllElements();
			}
		});
		
		JPanel btnPanel = new JPanel(new GridLayout(1, 0));
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(dropAllBtn);
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(editBtn);
		btnPanel.add(Box.createHorizontalGlue());
		
		packetListModel = new DefaultListModel();
		final JList pktList = new JList(packetListModel);
		pktList.setCellRenderer(new PacketListRender());
		pktList.getInputMap(JList.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "_deleteRow");
		pktList.getActionMap().put("_deleteRow", new AbstractAction() {
			private static final long serialVersionUID = -6997563795270827231L;
			@Override
			public void actionPerformed(ActionEvent e) {
				int selIndex = pktList.getSelectedIndex();
				if (selIndex < 0) return;
				packetListModel.remove(selIndex);
				pktList.setSelectedIndex(selIndex);
			}
		});
		pktList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					modifyPacketList(pktList.getSelectedIndex());
				}
			}
		});
		
		JScrollPane pktPainter = new JScrollPane(pktList, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JPanel pktPane = new JPanel(new BorderLayout());
		TitledBorder border = BorderFactory.createTitledBorder("Input packets");
		pktPane.setBorder(border);
		pktPane.add(pktPainter, BorderLayout.CENTER);
		pktPane.add(btnPanel, BorderLayout.SOUTH);
		
		// bin size
		JLabel binWLbl = new JLabel("Width");
		binWLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		
		binWidth = new JIntegerTextField();
		
		JLabel binHLbl = new JLabel("Height");
		binHLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		
		binHeight = new JIntegerTextField();
		binHeight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});
		
		Box binBox = Box.createHorizontalBox();
		border = BorderFactory.createTitledBorder("Bin size");
		binBox.setBorder(border);
		binBox.add(binWLbl);
		binBox.add(Box.createHorizontalStrut(5));
		binBox.add(binWidth);
		binBox.add(Box.createHorizontalStrut(20));
		binBox.add(binHLbl);
		binBox.add(Box.createHorizontalStrut(5));
		binBox.add(binHeight);
		
		// add all together
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(pktPane, BorderLayout.CENTER);
		pane.add(binBox, BorderLayout.PAGE_END);
		
		return pane;
	}
	
}
