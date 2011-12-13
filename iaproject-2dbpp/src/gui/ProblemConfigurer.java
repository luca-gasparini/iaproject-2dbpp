package gui;

import gui.common.AbstractDialog;
import gui.common.JIntegerTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import logic.BinConfiguration;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;
import core.DataParsingException;

public class ProblemConfigurer extends AbstractDialog<ProblemConfiguration> {
	
	private static final long serialVersionUID = -1111598546169426454L;
	
	private class PacketGraphic extends JPanel {
		private static final long serialVersionUID = -316443024628550817L;

		private static final int MAGNIFYING = 5;
		
		private final PacketConfiguration pc;
		private final Dimension rectDimension;
		
		public PacketGraphic(PacketConfiguration pc) {
			this.pc = pc;
			this.rectDimension = new Dimension(pc.getWidth() * MAGNIFYING, 
					pc.getHeight() * MAGNIFYING);
			
			this.setPreferredSize(new Dimension(rectDimension.width + 4, rectDimension.height + 4));
			this.setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2d = (Graphics2D) g;
			
			Rectangle rect = new Rectangle(new Point(2, 2), rectDimension);
			
			g2d.setColor(pc.getColor());
			g2d.fill(rect);
			
			g2d.setColor(Color.BLACK);
			g2d.draw(rect);
		}
		
	}
	
	private class PacketListRended implements ListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Box theBox = Box.createHorizontalBox();
			
			PacketConfiguration pc = (PacketConfiguration) value;
			
			JLabel dimensionLbl = new JLabel(pc.getWidth() + "x" + pc.getHeight());
			JLabel numberLbl = new JLabel("#" + pc.getMolteplicity());
			
			JPanel packet = new PacketGraphic(pc);
			
			theBox.add(packet);
			theBox.add(Box.createHorizontalStrut(20));
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
	
	private DefaultListModel packetList;
	private JIntegerTextField binWidth;
	private JIntegerTextField binHeight;
	
	@Override
	protected void paintValue(ProblemConfiguration value) {
		binWidth.setValue(Integer.valueOf(value.getBin().getWidth()));
		binHeight.setValue(Integer.valueOf(value.getBin().getHeight()));
		
		packetList.removeAllElements();
		for (PacketConfiguration pc : value.getPackets()) {
			packetList.addElement(pc);
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
		
		PacketConfiguration[] tmp = new PacketConfiguration[packetList.size()];
		packetList.copyInto(tmp);
		
		List<PacketConfiguration> packets = Arrays.asList(tmp);
		
		return new ProblemConfiguration(bc, packets);
	}

	@Override
	protected JComponent buildValuePainter() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// Packet configuration
		JButton editBtn = new JButton("EDIT");
		editBtn.setFont(GUIUtils.BUTTON_FONT);
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PacketConfiguration[] tmp = new PacketConfiguration[packetList.size()];
				packetList.copyInto(tmp);
				
				PacketsConfigurer packConf = new PacketsConfigurer(ProblemConfigurer.this, tmp);
				
				packetList.removeAllElements();
				for (PacketConfiguration pc : packConf.getValue()) {
					packetList.addElement(pc);
				}
			}
		});
		
		JButton dropAllBtn = new JButton("DROP ALL");
		dropAllBtn.setFont(GUIUtils.BUTTON_FONT);
		dropAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				packetList.removeAllElements();
			}
		});
		
		JPanel btnPanel = new JPanel(new GridLayout(1, 0));
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(dropAllBtn);
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(editBtn);
		btnPanel.add(Box.createHorizontalGlue());
		
		packetList = new DefaultListModel();
		JList pktList = new JList(packetList);
		pktList.setCellRenderer(new PacketListRended());
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
