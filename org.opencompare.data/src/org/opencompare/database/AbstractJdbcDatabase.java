package org.opencompare.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencompare.Snapshot;
import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

abstract class AbstractJdbcDatabase implements Database {

	private final static Map<String, Integer> sequences = new HashMap<String, Integer>();
	
    private final Snapshot snapshot;
    
    protected Connection connection;
    
    static final String SQL_SELECT_MAX_ID = "SELECT MAX(id) FROM %TABLE%";
    
    public AbstractJdbcDatabase(Snapshot snapshot) {
    	this.snapshot = snapshot;
	}

	public int nextId() throws ExplorationException {
    	synchronized (sequences) {
    		String name = getSnapshot().getName();
    		Integer i = sequences.get(name);
    		if (i == null) {	
    			// That's the first time we connect to this database
    			try {
					i = getMaxId();
					if (i == 0) {
						i = 1;		// Root element always has ID 1, so we should start at least with 2
					}
				} catch (SQLException e) {
					throw new ExplorationException("Unable to select maximum ID from database", e);
				}
    		}
    		++i;
    		sequences.put(name, i);
    		return i;
    	}
	}

	private Integer getMaxId() throws SQLException {
        PreparedStatement stmtSelectMaxId = connection.prepareStatement(getSql(SQL_SELECT_MAX_ID));
        try {
        	ResultSet rs = stmtSelectMaxId.executeQuery();
        	rs.next();
        	return rs.getInt(1);
        } finally {
        	stmtSelectMaxId.close();
        }
	}

	public Snapshot getSnapshot() {
		return snapshot;
	}
	
    public List<Explorable> getChildren(Explorable parent, ConflictFilter filter) throws ExplorationException {
        List<Explorable> res = new ArrayList<Explorable> ();
        int out = 0, in = 0;
        for (Explorable e : getChildren(parent)) {
            Conflict c = (Conflict) e;
            if (filter.include(c)) {
                res.add(c);
                ++in;
            } else {
                ++out;
            }
        }
        System.out.println("Included " + in + ", excluded " + out);
        return res;
    }

    /**
     * Here keys are child relative IDs.
     */
    public Map<String, Explorable> getChildrenAsMap(Explorable parent) throws ExplorationException {
        Map<String, Explorable> res = new HashMap<String, Explorable>();
        for (Explorable e : getChildren(parent)) {
            res.put(e.getRelativeId(), e);
        }
        return res;
    }

    /**
     * Here keys are child relative IDs.
     */
    public Map<String, Explorable> getChildrenAsMap(Explorable parent, ConflictFilter filter) throws ExplorationException {
        Map<String, Explorable> res = new HashMap<String, Explorable>();
        for (Explorable e : getChildren(parent, filter)) {
            res.put(e.getRelativeId(), e);
        }
        return res;
    }
    
    public Explorable getRoot() throws ExplorationException {
		return getById(1);
    }

    protected abstract String getSql(String statement);

    public String getFullId(Explorable e) throws ExplorationException {
        StringBuilder res = new StringBuilder();
        while (e.getParentId() != 0) {
            res.insert(0, e.getRelativeId());
            res.insert(0, '/');
            e = getById(e.getParentId());
        }
        return res.toString();
    }
    
}
