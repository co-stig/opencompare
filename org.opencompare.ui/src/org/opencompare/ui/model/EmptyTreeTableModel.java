package org.opencompare.ui.model;


import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class EmptyTreeTableModel extends AbstractTreeTableModel {

    public int getColumnCount() {
        return 0;
    }

    public Object getValueAt(Object o, int i) {
        return null;
    }

    public Object getChild(Object parent, int index) {
        return null;
    }

    public int getChildCount(Object parent) {
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }
}
