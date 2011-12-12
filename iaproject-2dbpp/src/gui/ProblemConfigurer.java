package gui;

import gui.common.AbstractDialog;
import gui.common.JIntegerTextField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import core.DataParsingException;

import logic.BinConfiguration;
import logic.PacketConfiguration;
import logic.ProblemConfiguration;

public class ProblemConfigurer extends AbstractDialog<ProblemConfiguration> {
	
	private static final long serialVersionUID = -1111598546169426454L;

	private class PacketsPainter extends JScrollPane {
		
		private static final long serialVersionUID = 3890016259055101406L;

		public PacketsPainter() {
			// TODO
			
		}
		
		public List<PacketConfiguration> getPackets() {
			// TODO
			return Collections.emptyList();
		}
		
		public void paintPackets(List<PacketConfiguration> pkts) {
			// TODO
		}
		
	}
	
	public ProblemConfigurer(Window parent,
			ProblemConfiguration oldValue) {
		super(parent, "Problem configuration", oldValue);
	}
	
	private PacketsPainter pktPainter;
	private JIntegerTextField binWidth;
	private JIntegerTextField binHeight;
	
	@Override
	protected void paintValue(ProblemConfiguration value) {
		binWidth.setValue(Integer.valueOf(value.getBin().getWidth()));
		binHeight.setValue(Integer.valueOf(value.getBin().getHeight()));
		
		pktPainter.paintPackets(value.getPackets());
	}

	@Override
	protected ProblemConfiguration getUIValue() throws DataParsingException {
		Integer binW = binWidth.getValue();
		Integer binH = binHeight.getValue();
		
		if (binW == null || binH == null) {
			throw new DataParsingException("Wrong bin dimension");
		}
		BinConfiguration bc = new BinConfiguration(binW.intValue(), binH.intValue());
		
		List<PacketConfiguration> packets = pktPainter.getPackets();
		if (packets == null) {
			throw new DataParsingException("No packets inserted");
		}
		
		return new ProblemConfiguration(bc, packets);
	}

	@Override
	protected JComponent buildValuePainter() {
		
		// Packet configuration
		JButton editBtn = new JButton("EDIT");
		editBtn.setFont(GUIUtils.BUTTON_FONT);
		
		JButton dropAllBtn = new JButton("DROP ALL");
		dropAllBtn.setFont(GUIUtils.BUTTON_FONT);
		
		JPanel btnPanel = new JPanel(new GridLayout(1, 0));
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(dropAllBtn);
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(editBtn);
		btnPanel.add(Box.createHorizontalGlue());
		
		pktPainter = new PacketsPainter();
		
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
		binBox.add(binWidth);
		binBox.add(Box.createHorizontalStrut(20));
		binBox.add(binHLbl);
		binBox.add(binHeight);
		
		// add all together
		JPanel pane = new JPanel(new GridLayout(0, 1));
		pane.add(pktPane);
		pane.add(binBox);
		
		return pane;
	}
	
}
