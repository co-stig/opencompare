package org.opencompare.ui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.opencompare.explorable.OptionDefinition;
import org.opencompare.explorable.OptionValue;

public class OptionsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private final List<OptionValue> options;

	public OptionsTableModel(List<OptionValue> options) {
		this.options = new ArrayList<OptionValue>(options);
	}
	
	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return options.size();
	}

	public Object getValueAt(int row, int column) {
		OptionValue option = options.get(row);

		switch (column) {
			case 0:
				OptionDefinition def = option.getDefinition();
				if (def.isRequired()) {
					return "* " + def.getDescription();
				} else {
					return def.getDescription();
				}
			case 1:
				return option.getValue();
			case 2:
				return option.getDefinition().getPackage();
		}
		
		// Will never be here anyway
		return null;
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		if (column == 1) {
			OptionValue val = options.get(row);
			
			// It's a bit ugly, but it looks like default cell editor always supplies String as a value
			switch(val.getDefinition().getType()) {
			case Int:
				if (value instanceof String) {
					val.setValue(Integer.parseInt((String) value));
				} else if (value instanceof Number) {
					val.setValue(((Number) value).intValue());
				}
				break;
			case YesNo:
				if (value instanceof String) {
					val.setValue(Boolean.parseBoolean((String) value));
				} else if (value instanceof Boolean) {
					val.setValue(value);
				}
				break;
			default:
				val.setValue(value);
			}
			
			fireTableCellUpdated(row, column);
		}
	}
	
	public OptionValue getOption(int row) {
		return options.get(row);
	}
}
