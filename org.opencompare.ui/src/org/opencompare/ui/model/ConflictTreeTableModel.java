package org.opencompare.ui.model;

import org.opencompare.ExploreApplication;
import org.opencompare.database.Database;
import org.opencompare.database.DescriptionsDatabase;
import org.opencompare.explorable.Conflict;
import org.opencompare.explore.ExplorationException;

public class ConflictTreeTableModel extends ExplorableTreeTableModel {

	public ConflictTreeTableModel(Database database, DescriptionsDatabase descriptions) throws ExplorationException {
		super(database, descriptions);
	}

	public int getColumnCount() {
		return ExploreApplication.ENABLE_EXPERIMENTAL_FUNCTIONALITY ? 6 : 4;
	}

	public Object getValueAt(Object o, int i) {
		Conflict c = (Conflict) o;
		
		switch (i) {
		case 0:
			return c.getRelativeId();
		case 1:
			return c.getType();
		case 2:
			return c.getActual() == null ? "" : c.getActual().getUserFriendlyValue();
		case 3:
			return c.getReference() == null ? "" : c.getReference().getUserFriendlyValue();
		case 4:
			return c.getComment();
		case 5:
			return c.getDescription() == null ? "" : c.getDescription();
		}
		
		return null;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Type";
		case 2:
			return "Actual value";
		case 3:
			return "Reference value";
		case 4:
			return "Comment";
		case 5:
			return "Description";
		}
		return "";
	}
}
