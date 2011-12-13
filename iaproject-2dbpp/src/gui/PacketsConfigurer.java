package gui;

import gui.common.AbstractDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import logic.PacketConfiguration;
import core.DataParsingException;

public class PacketsConfigurer extends AbstractDialog<PacketConfiguration[]> {
	
	private static final long serialVersionUID = -5093906029953118604L;
	
	private enum Columns {
		WIDTH("Width", 0, Integer.class),
		HEIGHT("Height", 1, Integer.class),
		MOLTEPLICITY("Molteplicity", 2, Integer.class),
		COLOR("Color", 3, Color.class);
		
		private final String columnName;
		private final int colIndex;
		private final Class<?> type;
		
		private Columns(String columnName, int colIndex, Class<?> type) {
			this.columnName = columnName;
			this.colIndex = colIndex;
			this.type = type;
		}
		
		public String getName() {
			return this.columnName;
		}
		
		public int getIndex() {
			return this.colIndex;
		}
		
		public Class<?> getType() {
			return this.type;
		}
	}
	
	// quite entirely copied from Java tutorial
	private class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {
		
		private static final long serialVersionUID = 7341265862327077732L;
		
		private final JButton colorBtn;
		private final JDialog colorDialog;
		private final JColorChooser colorChooser;
		
		private Color choosedColor;
		
		public ColorCellEditor() {
			this.colorBtn = new JButton();
			this.colorBtn.setBorderPainted(false);
			this.colorBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JButton btn = (JButton) e.getSource();
					btn.setBackground(choosedColor);
					colorChooser.setColor(choosedColor);
					colorDialog.setVisible(true);
					
					/* here method is blocked by JDialog, so next instruction 
					 * won't be executed until colorDialog returns
					 */
					
					fireEditingStopped();
				}
			});
			
			this.colorChooser = new JColorChooser();
			this.colorDialog = JColorChooser.createDialog(
					// so colorBtn method execution is interrupted when dialog become visible
					this.colorBtn, 
					"Choose packet color", 
					true, 
					this.colorChooser, 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							ColorCellEditor.this.choosedColor = colorChooser.getColor();
						}
					}, 
					null);
		}
		
		@Override
		public Object getCellEditorValue() {
			return choosedColor;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			this.choosedColor = (Color) value;
			return colorBtn;
		}
	}
	
	// quite entirely copied from Java tutorial
	private class ColorCellRenderer extends JLabel implements TableCellRenderer {
		
		private static final long serialVersionUID = 1591447604801920483L;
		
		private Border unselectedBorder = null;
	    private Border selectedBorder = null;
		
	    public ColorCellRenderer() {
	    	setOpaque(true); //MUST do this for background to show up.
		}
	    
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			Color newColor = (Color) value;
	        setBackground(newColor);
	        
	        if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        
			setToolTipText("RGB value: " + newColor.getRed() + ", "
						+ newColor.getGreen() + ", "
						+ newColor.getBlue());
			return this;
		}
	}
	
	private class MyTableModel extends DefaultTableModel {
		
		private static final long serialVersionUID = 8300604930231863330L;

		
		public MyTableModel(Object[] columnNames, int rowCount) {
			super(columnNames, rowCount);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			for (Columns col : Columns.values()) {
				if (col.getIndex() == columnIndex) {
					return col.getType();
				}
			}
			
			throw new IllegalStateException("Column is not present");
		}
	}
	
	private DefaultTableModel packets;
	
	public PacketsConfigurer(Window parent,	PacketConfiguration[] oldValue) {
		super(parent, "Packets configuration", oldValue);
	}

	@Override
	protected void paintValue(PacketConfiguration[] value) {
		for (PacketConfiguration pc : value) {
			Object[] rowData = new Object[Columns.values().length];
			
			rowData[Columns.WIDTH.getIndex()] = Integer.valueOf(pc.getWidth());
			rowData[Columns.HEIGHT.getIndex()] = Integer.valueOf(pc.getHeight());
			rowData[Columns.MOLTEPLICITY.getIndex()] = Integer.valueOf(pc.getMolteplicity());
			rowData[Columns.COLOR.getIndex()] = pc.getColor();
			
			packets.addRow(rowData);
		}
	}

	@Override
	protected PacketConfiguration[] getUIValue() throws DataParsingException {
		PacketConfiguration[] pktArray = new PacketConfiguration[packets.getRowCount()];
		
		int i = 0;
		for (Object rowObj : packets.getDataVector()) {
			Vector<?> row = (Vector<?>) rowObj;
			
			Integer width = (Integer) row.get(Columns.WIDTH.getIndex());
			Integer height = (Integer) row.get(Columns.HEIGHT.getIndex());
			Integer molteplicity = (Integer) row.get(Columns.MOLTEPLICITY.getIndex());
			Color color = (Color) row.get(Columns.COLOR.getIndex());
			
			pktArray[i++] = new PacketConfiguration(width.intValue(), 
					height.intValue(), molteplicity.intValue(), color);
		}
				
		return pktArray;
	}

	@Override
	protected JComponent buildValuePainter() {
		
		String[] columnNames = new String[Columns.values().length];
		for (Columns col : Columns.values()) {
			columnNames[col.getIndex()] = col.getName();
		}
		
		packets = new MyTableModel(columnNames, 0);
		
		JTable pktTable = new JTable(packets);
		pktTable.setDefaultEditor(Color.class, new ColorCellEditor());
		pktTable.setDefaultRenderer(Color.class, new ColorCellRenderer());
		
		JScrollPane scroller = new JScrollPane(pktTable, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(scroller);
		pane.add(GUIUtils.getHorizontalSeparator(5, 0), BorderLayout.PAGE_END);
		
		return pane;
	}

}
