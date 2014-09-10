package org.opencompare.ui.model;

import org.opencompare.ExploreApplication;
import org.opencompare.database.Database;
import org.opencompare.database.DescriptionsDatabase;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

public class SnapshotTreeTableModel extends ExplorableTreeTableModel {

	public SnapshotTreeTableModel(Database database, DescriptionsDatabase descriptions) throws ExplorationException {
		super(database, descriptions);
	}

	public int getColumnCount() {
		return ExploreApplication.ENABLE_EXPERIMENTAL_FUNCTIONALITY ? 3 : 2;
	}

	public Object getValueAt(Object o, int i) {
		Explorable e = (Explorable) o;
		
		switch (i) {
		case 0:
			return e.getRelativeId();
		case 1:
			return e.getUserFriendlyValue();
		case 2:
			return e.getDescription() == null ? "" : e.getDescription();
		}
		
		return null;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Value";
		case 2:
			return "Description";
		}
		return "";
	}
}
