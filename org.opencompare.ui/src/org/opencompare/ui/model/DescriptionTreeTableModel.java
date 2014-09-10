package org.opencompare.ui.model;

import org.opencompare.database.Database;
import org.opencompare.explorable.Description;
import org.opencompare.explore.ExplorationException;

public class DescriptionTreeTableModel extends ExplorableTreeTableModel {

	public DescriptionTreeTableModel(Database database) throws ExplorationException {
		super(database, null);
	}

	public int getColumnCount() {
		return 2;
	}

	public Object getValueAt(Object o, int i) {
		Description d = (Description) o;
		
		switch (i) {
		case 0:
			return d.getRelativeId();
		case 1:
			return d.getValue();
		}
		
		return null;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Description";
		}
		return "";
	}
}
