package org.opencompare.ui.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.opencompare.database.ConflictFilter;
import org.opencompare.database.ConflictTypeFilter;
import org.opencompare.database.Database;
import org.opencompare.database.JdbcDescriptionsDatabase;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

public abstract class ExplorableTreeTableModel extends AbstractTreeTableModel implements Closeable {

	private final Database database;
	private final JdbcDescriptionsDatabase descriptionsDatabase;
        
        private ConflictFilter filter = null;
	
	/*
	 * TODO: This is a very stupid single-threaded ever-growing memory-leaking
	 * cache. Replace it with something smarter later.
	 */
	private final Map<String, List<Explorable>> children = new HashMap<String, List<Explorable>>();
	private Explorable root = null;

	public ExplorableTreeTableModel(Database database, JdbcDescriptionsDatabase descriptions) throws ExplorationException {
		this.database = database;
		this.descriptionsDatabase = descriptions;
	}

	private List<Explorable> getChildren(Object parent) throws ExplorationException {
		Explorable ex = (Explorable) parent;
		String parentSha = ex.getSha();
		
		List<Explorable> res = children.get(parentSha);
		if (res == null) {
			res = filter == null ? 
                                database.getChildren(ex) : 
                                database.getChildren(ex, filter);
			children.put(parentSha, res);
			
			/*
			 * Populate descriptions. We do it here instead of database layer to
			 * avoid overhead, because the database layer is used on various
			 * stages when we don't need descriptions.
			 */
			if (descriptionsDatabase != null) {
				Map<String, String> descriptions = descriptionsDatabase.getChildrenDescriptions(parentSha);
				if (!descriptions.isEmpty()) {
					for (Explorable e: res) {
						e.setDescription(descriptions.get(e.getSha()));
					}
				}
			}
		}
		return res;
	}
	
	public Object getChild(Object parent, int index) {
		try {
			return getChildren(parent).get(index);
		} catch (ExplorationException e) {
			return null;
		}
	}

	public int getChildCount(Object parent) {
		try {
			return getChildren(parent).size();
		} catch (ExplorationException e) {
			return -1;
		}
	}

	public int getIndexOfChild(Object parent, Object child) {
		try {
			return getChildren(parent).indexOf(child);
		} catch (ExplorationException e) {
			return -1;
		}
	}

	@Override
	public Object getRoot() {
		try {
			if (root == null) {
				root = database.getRoot();
			}
			return root;
		} catch (ExplorationException e) {
			return null;
		}
	}

	public Database getDatabase() {
		return database;
	}

	public JdbcDescriptionsDatabase getDescriptionsDatabase() {
		return descriptionsDatabase;
	}

	public void close() throws IOException {
		database.close();
		if (descriptionsDatabase != null) {
			descriptionsDatabase.close();
		}
	}
        
        // This will erase the cache
        public void setFilter(boolean includeIdentical) {
            filter = includeIdentical ? null : new ConflictTypeFilter(false, true, true, true);
            children.clear();
        }
        
        public boolean getFilter() {
            return filter == null;
        }
        
}
