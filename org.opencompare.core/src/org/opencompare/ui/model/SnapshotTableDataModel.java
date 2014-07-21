package org.opencompare.ui.model;

import javax.swing.table.AbstractTableModel;

import org.opencompare.ExploreApplication;
import org.opencompare.Snapshot;
import org.opencompare.database.DatabaseManager;

public class SnapshotTableDataModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMNS = ExploreApplication.ENABLE_EXPERIMENTAL_FUNCTIONALITY ? 
			new String[] {"Name", "Version", "Type", "State", "Last modified", "Size (bytes)", "Location", "Author"} :
			new String[] {"Name",            "Type", "State", "Last modified", "Size (bytes)", "Location"          };
    
    private Snapshot[] snapshots;

    public SnapshotTableDataModel() {
        refresh();
    }
    
    public void refresh() {
        snapshots = DatabaseManager.listSnapshots();
        fireTableDataChanged();
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    public int getRowCount() {
        return snapshots.length;
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Snapshot s = snapshots[rowIndex];
        if (ExploreApplication.ENABLE_EXPERIMENTAL_FUNCTIONALITY) {
	        switch (columnIndex) {
	            case 0: return s.getName();
	            case 1: return s.getVersion();
	            case 2: return s.getType().toString();
	            case 3: return s.getState().toString();
	            case 4: return s.getLastModifiedFormatted();
	            case 5: return s.getSizeFormatted();
	            case 6: return s.getFolder().getAbsolutePath();
	            case 7: return s.getAuthor();
	        }
        } else {
            switch (columnIndex) {
	            case 0: return s.getName();
	            case 1: return s.getType().toString();
	            case 2: return s.getState().toString();
	            case 3: return s.getLastModifiedFormatted();
	            case 4: return s.getSizeFormatted();
	            case 5: return s.getFolder().getAbsolutePath();
	        }
        }
        return null;
    }
    
    public Snapshot get(int row) {
        return snapshots[row];
    }
}
