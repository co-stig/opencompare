package org.opencompare.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencompare.Snapshot;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.ConflictType;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

/**
 * This connection won't be shared between multiple threads, so its methods
 * don't have to be synchronized.
 */
class JdbcConflictsDatabase extends AbstractJdbcDatabase {

    public static final int COMMENT_LENGTH = 1000;
    private static final String CONFLICT = Conflict.class.getSimpleName();
    
    static final String SQL_CREATE_CONFLICT_TABLE = "CREATE TABLE Conflicts (" +
    		"id INT PRIMARY KEY, " +
    		"parentId INT, " +
    		"referenceId INT, " +
    		"actualId INT, " +
    		"conflictType SMALLINT, " +
    		"comment VARCHAR(" + COMMENT_LENGTH + "))";
    
    static final String SQL_CREATE_INDEXES = "CREATE INDEX idxParent ON Conflicts (parentId)";

    static final String SQL_ADD_FK_ACTUAL = "ALTER TABLE Conflicts ADD CONSTRAINT fkActual FOREIGN KEY (actualId) REFERENCES Actual (id)";
    static final String SQL_ADD_FK_REFERENCE = "ALTER TABLE Conflicts ADD CONSTRAINT fkReference FOREIGN KEY (referenceId) REFERENCES Reference (id)";
    
    static final String SQL_INSERT_CONFLICT = "INSERT INTO %TABLE% VALUES (?, ?, ?, ?, ?, ?)";
    private PreparedStatement stmtInsertConflict;
    private PreparedStatement stmtInsertActual;
    private PreparedStatement stmtInsertReference;

    static final String SQL_SELECT_CONFLICT_BY_ID = 
    		"SELECT c.id, c.parentId, c.conflictType, c.comment, " + 
				"r.id as rid, r.relativeId as rrelativeId, r.type as rtype, r.hash as rhash, r.value as rvalue, r.parentId as rparent, r.sha as rsha, " + 
				"a.id as aid, a.relativeId as arelativeId, a.type as atype, a.hash as ahash, a.value as avalue, a.parentId as aparent, a.sha as asha " +
			"FROM Conflicts c " +
			"LEFT JOIN Reference r ON c.referenceId = r.id " + 
			"LEFT JOIN Actual a ON c.actualId = a.id " +
			"WHERE c.id = ?";
    private PreparedStatement stmtSelectConflictById;
    
    static final String SQL_SELECT_CHILD_CONFLICTS = 
    		"SELECT c.id, c.parentId, c.conflictType, c.comment, " + 
				"r.id as rid, r.relativeId as rrelativeId, r.type as rtype, r.hash as rhash, r.value as rvalue, r.parentId as rparent, r.sha as rsha, " + 
				"a.id as aid, a.relativeId as arelativeId, a.type as atype, a.hash as ahash, a.value as avalue, a.parentId as aparent, a.sha as asha " +
			"FROM Conflicts c " +
			"LEFT JOIN Reference r ON c.referenceId = r.id " + 
			"LEFT JOIN Actual a ON c.actualId = a.id " +
			"WHERE c.parentId = ? ORDER BY c.id";
    private PreparedStatement stmtSelectChildConflicts;
    
    private static final String SQL_SELECT_CONFLICTS_COUNT = "SELECT COUNT(*) FROM %TABLE%";
    private PreparedStatement stmtSelectConflictsCount;
    
    public JdbcConflictsDatabase(Snapshot snapshot, boolean createNew) throws SQLException, ClassNotFoundException, ExplorationException {
    	super(snapshot);
    	
        openConnection(snapshot.getName());
        if (createNew) {
            createTables();
        }
        prepareStatements();
    }

    private String getSql(String statement, boolean reference) {
    	return statement.replace("%TABLE%", reference ? "Reference" : "Actual");
    }
    
    private void prepareStatements() throws SQLException {
        stmtInsertActual = connection.prepareStatement(getSql(JdbcExplorablesDatabase.SQL_INSERT_EXPLORABLE, false));
        stmtInsertReference = connection.prepareStatement(getSql(JdbcExplorablesDatabase.SQL_INSERT_EXPLORABLE, true));
        stmtInsertConflict = connection.prepareStatement(getSql(SQL_INSERT_CONFLICT));
        stmtSelectConflictById = connection.prepareStatement(SQL_SELECT_CONFLICT_BY_ID);
        stmtSelectChildConflicts = connection.prepareStatement(SQL_SELECT_CHILD_CONFLICTS);
        stmtSelectConflictsCount = connection.prepareStatement(getSql(SQL_SELECT_CONFLICTS_COUNT));
    }

    private void openConnection(String name) throws ClassNotFoundException, SQLException {
    	System.out.println("Opening connection " + getSnapshot());
    	
        System.setProperty("derby.system.home", JdbcDatabaseManager.getDbFolder());
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        connection = DriverManager.getConnection("jdbc:derby:" + name + ";create=true");
    }

    private void createTables() throws SQLException {
    	PreparedStatement stmtCreateActual = connection.prepareStatement(getSql(JdbcExplorablesDatabase.SQL_CREATE_EXPLORABLE_TABLE, "Actual"));    	
        try {
    		stmtCreateActual.executeUpdate();
    	} finally {
    		stmtCreateActual.close();
    	}
    	
    	PreparedStatement stmtCreateReference = connection.prepareStatement(getSql(JdbcExplorablesDatabase.SQL_CREATE_EXPLORABLE_TABLE, "Reference"));
    	try {
    		stmtCreateReference.executeUpdate();
    	} finally {
    		stmtCreateReference.close();
    	}
    	
        PreparedStatement stmtCreateExplorable = connection.prepareStatement(SQL_CREATE_CONFLICT_TABLE);
        try {
            stmtCreateExplorable.executeUpdate();
        } finally {
            stmtCreateExplorable.close();
        }
        
        PreparedStatement stmtFkActual = connection.prepareStatement(SQL_ADD_FK_ACTUAL);
        try {
        	stmtFkActual.executeUpdate();
        } finally {
        	stmtFkActual.close();
        }
        
        PreparedStatement stmtFkReference = connection.prepareStatement(SQL_ADD_FK_REFERENCE);
        try {
        	stmtFkReference.executeUpdate();
        } finally {
        	stmtFkReference.close();
        }
        
        createIndexes();
        connection.commit();
    }

    public void addReference(Explorable e) throws ExplorationException {
		JdbcExplorablesDatabase.addExplorable(e, stmtInsertReference);
    }
    
    public void addActual(Explorable e) throws ExplorationException {
    	JdbcExplorablesDatabase.addExplorable(e, stmtInsertActual);
    }
    
    /**
     * Here we suppose unique ID has already been generated for e. Also we
     * expect the reference and actual explorables already in the same database.
     */
    public void add(Explorable e) throws ExplorationException {
		assert (e instanceof Conflict);
		Conflict c = (Conflict) e;
    	
    	/*
		 * Relative ID should always be complete, it's an ID after all.
		 * Therefore we throw an exception if it's too long.
		 */
    	String relativeId = c.getRelativeId();
    	if (relativeId.length() > JdbcExplorablesDatabase.RELATIVE_ID_LENGTH) {
    		throw new ExplorationException("Explorable relative ID exceeds database column size: " + relativeId);
    	}
    	
		/*
		 * Comment length should be controlled in the UI layer, not allowing the
		 * user to exceed the limit. Therefore here we can safely throw an
		 * exception if it's too long.
		 */
    	String comment = c.getComment();
    	if (comment != null && comment.length() > COMMENT_LENGTH) {
    		throw new ExplorationException("Conflict's comment length exceeds database column size: " + comment);
    	}
    	
        try {
            stmtInsertConflict.setInt(1, c.getId());						// id
            stmtInsertConflict.setInt(2, c.getParentId());					// parentId
            if (c.getReference() == null) {									// referenceId
            	stmtInsertConflict.setNull(3, Types.INTEGER);			
            } else {
            	stmtInsertConflict.setInt(3, c.getReference().getId());
            }
            if (c.getActual() == null) {									// actualId
            	stmtInsertConflict.setNull(4, Types.INTEGER);			
            } else {
            	stmtInsertConflict.setInt(4, c.getActual().getId());
            }
            stmtInsertConflict.setShort(5, (short) c.getType().ordinal());	// conflictType
            if (comment == null) {											// comment
            	stmtInsertConflict.setNull(6, Types.VARCHAR);			
            } else {
            	stmtInsertConflict.setString(6, comment);
            }
            
            stmtInsertConflict.executeUpdate();
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to insert conflict to database: " + c, ex);
        }
    }

    public Explorable getById(int id) throws ExplorationException {
        try {
            stmtSelectConflictById.setInt(1, id);
            ResultSet rs = stmtSelectConflictById.executeQuery();
            try {
                return rs.next() ? parseRow(rs) : null;
            } finally {
                rs.close();
            }
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to get Conflict by id: " + id, ex);
        }
    }

    private Explorable parseRow(ResultSet rs) throws SQLException, ExplorationException {
        int id = rs.getInt(1);
        int parentId = rs.getInt(2);
        ConflictType type = ConflictType.valueOf(rs.getShort(3));
        String comment = rs.getString(4);
        
        Explorable reference = null;
        int rId = rs.getInt(5);
        if (rId != 0) {
        	String rRelativeId = rs.getString(6);
        	String rType = rs.getString(7);
        	long rHash = rs.getLong(8);
        	String rValue = rs.getString(9);
        	int rParentId = rs.getInt(10);
        	String rSha = rs.getString(11);
        	
        	reference = Configuration.getExplorableFactory(rType).newExplorable(rType, rId, rParentId, rRelativeId, rValue, rHash, rSha);
        }
        
        Explorable actual = null;
        int aId = rs.getInt(12);
        if (aId != 0) {
	        String aRelativeId = rs.getString(13);
	        String aType = rs.getString(14);
	        long aHash = rs.getLong(15);
	        String aValue = rs.getString(16);
	        int aParentId = rs.getInt(17);
	        String aSha = rs.getString(18);
	        
	        actual = Configuration.getExplorableFactory(aType).newExplorable(aType, aId, aParentId, aRelativeId, aValue, aHash, aSha);
        }
        
        return Configuration.getExplorableFactory(CONFLICT).newExplorable(null, CONFLICT, id, parentId, reference, actual, type, comment);
    }

    public List<Explorable> getChildren(Explorable parent) throws ExplorationException {
        try {
            stmtSelectChildConflicts.setInt(1, parent.getId());
            ResultSet rs = stmtSelectChildConflicts.executeQuery();
            try {
                List<Explorable> res = new ArrayList<Explorable>();
                while (rs.next()) {
                    res.add(parseRow(rs));
                }
                return res;
            } finally {
                rs.close();
            }

        } catch (SQLException ex) {
            throw new ExplorationException("Unable to read child Conflicts from the database", ex);
        }
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

    public int size() throws ExplorationException {
        try {
            ResultSet rs = stmtSelectConflictsCount.executeQuery();
            try {
                rs.next();
                return rs.getInt(1);
            } finally {
                rs.close();
            }
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to get explorables count from the database", ex);
        }
    }

    public void close() throws IOException {
        try {
        	System.out.println("Closing connection " + getSnapshot());
            connection.commit();

        	stmtInsertActual.close();
            stmtInsertReference.close();
            stmtInsertConflict.close();
            stmtSelectChildConflicts.close();
            stmtSelectConflictById.close();
            stmtSelectConflictsCount.close();
            connection.close();
        } catch (SQLException ex) {
        	IOException rethrow = new IOException("Unable to free one of the JDBC resources");
        	rethrow.initCause(ex);
        	throw rethrow;
        }
    }

    public int sizeFilesOnly() throws ExplorationException {
        throw new UnsupportedOperationException("Conflicts database does not support sizeFilesOnly()");
    }
    
    public void createIndexes() throws SQLException {
        PreparedStatement stmtCreateIndexes = connection.prepareStatement(SQL_CREATE_INDEXES);
        try {
            stmtCreateIndexes.executeUpdate();
        } finally {
            stmtCreateIndexes.close();
        }
    }

	@Override
	protected String getSql(String statement) {
    	return getSql(statement, "Conflicts");
	}
	
	private String getSql(String statement, String table) {
		return statement.replace("%TABLE%", table);
	}
}
