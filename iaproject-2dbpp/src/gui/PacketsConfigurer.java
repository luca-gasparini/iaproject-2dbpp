package gui;

import gui.common.AbstractDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import logic.PacketConfiguration;
import core.DataParsingException;

public class PacketsConfigurer extends AbstractDialog<PacketConfiguration[]> {
	
	private static final long serialVersionUID = -5093906029953118604L;
	
	private enum Columns {
		INDEX("#", 0, Integer.class),
		WIDTH("Width", 1, Integer.class),
		HEIGHT("Height", 2, Integer.class),
		MOLTEPLICITY("Molteplicity", 3, Integer.class),
		COLOR("Color", 4, Color.class);
		
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
			
			if (value == null) {
				setBackground(table.getBackground());
				return this;
			}
			
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
	
	private class MyTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 8300604930231863330L;
		
		private final Random rand = new Random(System.currentTimeMillis());
		private final String[] columnNames;
		private final Class<?>[] columnTypes;
		private final ArrayList<List<Object>> dataRows;
		
		public MyTableModel() {
			columnNames = new String[Columns.values().length];
			columnTypes = new Class[Columns.values().length];
			for (Columns col : Columns.values()) {
				columnNames[col.getIndex()] = col.getName();
				columnTypes[col.getIndex()] = col.getType();
			}
			this.dataRows = new ArrayList<List<Object>>();
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == Columns.INDEX.getIndex()) {
				return false;
			}
			return true;
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			if (row >= dataRows.size()) {
				// user is inserting a new packet so...
				// ...insert a new empty row in dataRows...
				dataRows.add(Arrays.asList(new Object[columnNames.length]));
				// ...set row index...
				setValueAt(Integer.valueOf(dataRows.size() - 1), 
						dataRows.size() - 1 , Columns.INDEX.getIndex());
				// ...set a default color...
				setValueAt(new Color(rand.nextInt()), 
						dataRows.size() - 1 , Columns.COLOR.getIndex());
				// ...fire the new-row events for the just inserted row
				fireTableRowsInserted(dataRows.size() - 1, dataRows.size() - 1);
			}
			
			List<Object> rowData = dataRows.get(row);
			rowData.set(column, aValue);
			fireTableCellUpdated(row, column);
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			if (row >= dataRows.size()) {
				return null;
			}
			
			List<Object> rowData = dataRows.get(row);
			return rowData.get(column);
		}

		@Override
		public int getRowCount() {
			return dataRows.size() + 1;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		public void removeRow(int row) {
			if (row >= dataRows.size()) {
				// do nothing
				return;
			}
			
			// remove row
			dataRows.remove(row);
			fireTableRowsDeleted(row, row);
			
			// update all indexes from removed row to the end
			for (int i = row; i < dataRows.size(); i++) {
				setValueAt(Integer.valueOf(i), i, Columns.INDEX.getIndex());
			}
		}
		
		public void addRow(PacketConfiguration pc) {
			Object[] rowData = new Object[getColumnCount()];
			
			rowData[Columns.INDEX.getIndex()] = Integer.valueOf(dataRows.size());
			rowData[Columns.WIDTH.getIndex()] = Integer.valueOf(pc.getWidth());
			rowData[Columns.HEIGHT.getIndex()] = Integer.valueOf(pc.getHeight());
			rowData[Columns.MOLTEPLICITY.getIndex()] = Integer.valueOf(pc.getMolteplicity());
			rowData[Columns.COLOR.getIndex()] = pc.getColor();
			
			dataRows.add(Arrays.asList(rowData));
			
			fireTableRowsInserted(dataRows.size() - 1, dataRows.size() - 1);
		}
		
		/**
		 * YOU MUST NOT MODIFY RETURNED DATA!!
		 * @return
		 */
		public ArrayList<List<Object>> getData() {
			return this.dataRows;
		}
	}
	
	private MyTableModel packets;
	private JTable pktTable;
	
	public PacketsConfigurer(Window parent,	PacketConfiguration[] oldValue) {
		super(parent, "Packets configuration", oldValue);
	}

	@Override
	protected void paintValue(PacketConfiguration[] value) {
		for (PacketConfiguration pc : value) {
			packets.addRow(pc);
		}
	}

	@Override
	protected PacketConfiguration[] getUIValue() throws DataParsingException {
		List<List<Object>> dataVector = packets.getData();
		
		PacketConfiguration[] pktArray = new PacketConfiguration[dataVector.size()];
		
		for (int i = 0; i < dataVector.size(); i++) {
			List<Object> row = dataVector.get(i);
			
			Integer width = (Integer) row.get(Columns.WIDTH.getIndex());
			if (width == null) throw new DataParsingException("Invalid width on row " + i);
			Integer height = (Integer) row.get(Columns.HEIGHT.getIndex());
			if (height == null) throw new DataParsingException("Invalid height on row " + i);
			Integer molteplicity = (Integer) row.get(Columns.MOLTEPLICITY.getIndex());
			if (molteplicity == null) throw new DataParsingException("Invalid molteplicity on row " + i);
			Color color = (Color) row.get(Columns.COLOR.getIndex());
			if (color == null) throw new DataParsingException("Invalid color on row " + i);
			
			pktArray[i] = new PacketConfiguration(width.intValue(), 
					height.intValue(), molteplicity.intValue(), color);
		}
				
		return pktArray;
	}

	@Override
	protected JComponent buildValuePainter() {
		
		packets = new MyTableModel();
		
		pktTable = new JTable(packets);
		pktTable.setDefaultEditor(Color.class, new ColorCellEditor());
		pktTable.setDefaultRenderer(Color.class, new ColorCellRenderer());
		pktTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pktTable.setRowSelectionAllowed(true);
		pktTable.setColumnSelectionAllowed(false);
		pktTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					packets.removeRow(pktTable.getSelectedRow());
				}
			}
		});
		
		JScrollPane scroller = new JScrollPane(pktTable, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel pane = new JPanel(new BorderLayout());
		pane.add(scroller);
		pane.add(GUIUtils.getHorizontalSeparator(5, 0), BorderLayout.PAGE_END);
		
		return pane;
	}
	
	public void ensureSelectedRow(int row) {
		Rectangle rect = pktTable.getCellRect(row, 0, true);
		pktTable.scrollRectToVisible(rect);
		pktTable.setRowSelectionInterval(row, row);
		pktTable.setColumnSelectionInterval(1, 1);
	}

}
