package org.opencompare.database;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.opencompare.Snapshot;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

/**
 * This connection won't be shared between multiple threads, so its methods
 * don't have to be synchronized.
 */
public class JdbcExplorablesDatabase extends AbstractJdbcDatabase {

    public static final int RELATIVE_ID_LENGTH = 1000;
    public static final int VALUE_LENGTH = 1000;
    public static final int TYPE_LENGTH = 30;
    public static final int SHA_LENGTH = 64;
    
    static final String SQL_CREATE_EXPLORABLE_TABLE = "CREATE TABLE %TABLE% (" +
    		"id INT PRIMARY KEY, " +
    		"relativeId VARCHAR(" + RELATIVE_ID_LENGTH + "), " +
    		"parentId INT, " +
    		"value VARCHAR(" + VALUE_LENGTH + "), " +
    		"hash BIGINT, " +
    		"type VARCHAR(" + TYPE_LENGTH + "), " + 
            "sha CHAR(" + SHA_LENGTH + "))";
    
    static final String SQL_CREATE_INDEXES = "CREATE INDEX idxParent ON %TABLE% (parentId)";
    
    static final String SQL_INSERT_EXPLORABLE = "INSERT INTO %TABLE% VALUES (?, ?, ?, ?, ?, ?, ?)";
    private PreparedStatement stmtInsertExplorable;
    
    static final String SQL_SELECT_EXPLORABLE_BY_ID = "SELECT id, relativeId, parentId, value, hash, type, sha FROM %TABLE% WHERE id = ? ";
    private PreparedStatement stmtSelectExplorableById;
    
    static final String SQL_SELECT_CHILD_EXPLORABLES = "SELECT id, relativeId, parentId, value, hash, type, sha FROM %TABLE% WHERE parentId = ? ORDER BY relativeId";
    private PreparedStatement stmtSelectChildExplorables;
    
    static final String SQL_SELECT_EXPLORABLES_COUNT = "SELECT COUNT(*) FROM %TABLE%";
    private PreparedStatement stmtSelectExplorablesCount;
    
    static final String SQL_SELECT_FILES_COUNT = "SELECT COUNT(*) FROM %TABLE% WHERE type IN ('SimpleFile', 'PropertiesFile', 'XConfFile')";
    private PreparedStatement stmtSelectFilesCount;
    
    public JdbcExplorablesDatabase(Snapshot snapshot, boolean createNew) throws SQLException, ClassNotFoundException, ExplorationException {
    	super(snapshot);
    	
        openConnection(snapshot.getName());
        if (createNew) {
            createTables();
        }
        prepareStatements();
    }

    protected String getSql(String statement) {
    	return statement.replace("%TABLE%", "Explorables");
    }
    
    private void prepareStatements() throws SQLException {
        stmtInsertExplorable = connection.prepareStatement(getSql(SQL_INSERT_EXPLORABLE));
        stmtSelectChildExplorables = connection.prepareStatement(getSql(SQL_SELECT_CHILD_EXPLORABLES));
        stmtSelectExplorablesCount = connection.prepareStatement(getSql(SQL_SELECT_EXPLORABLES_COUNT));
        stmtSelectExplorableById = connection.prepareStatement(getSql(SQL_SELECT_EXPLORABLE_BY_ID));
        stmtSelectFilesCount = connection.prepareStatement(getSql(SQL_SELECT_FILES_COUNT));
    }

    private void openConnection(String name) throws ClassNotFoundException, SQLException {
    	System.out.println("Opening connection " + getSnapshot());

    	System.setProperty("derby.system.home", JdbcDatabaseManager.getDbFolder());
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        connection = DriverManager.getConnection("jdbc:derby:" + name + ";create=true");
    }

    private void createTables() throws SQLException {
        PreparedStatement stmtCreateExplorable = connection.prepareStatement(getSql(SQL_CREATE_EXPLORABLE_TABLE));
        try {
            stmtCreateExplorable.executeUpdate();
        } finally {
            stmtCreateExplorable.close();
        }
        
        createIndexes();
    }

    /**
     * Here we suppose unique ID has already been generated for e.
     * Also here we have access to temporary full ID and its SHA.
     */
    public void add(Explorable e) throws ExplorationException {
		addExplorable(e, stmtInsertExplorable);
    }
    
    public void addReference(Explorable e) throws ExplorationException {
    	throw new UnsupportedOperationException("Explorables database doesn't support addReference()");
    }
    
    public void addActual(Explorable e) throws ExplorationException {
    	throw new UnsupportedOperationException("Explorables database doesn't support addActual()");
    }
    
    // Static here is a bit ugly, but reusable from Conflicts database.
    static void addExplorable(Explorable e, PreparedStatement stmtInsertExplorable) throws ExplorationException {
		/*
		 * Relative ID should always be complete, it's an ID after all.
		 * Therefore we throw an exception if it's too long.
		 */
    	String relativeId = e.getRelativeId();
    	if (relativeId.length() > RELATIVE_ID_LENGTH) {
    		throw new ExplorationException("Explorable relative ID exceeds database column size: " + relativeId);
    	}
    	
		/*
		 * Truncate the value if necessary. Default Derby charset is UTF-8, thus
		 * we can simply truncate the string, instead of dealing with byte[].
		 */
    	String value = e.getValue();
    	if (value.length() > VALUE_LENGTH) {
    		value = value.substring(0, VALUE_LENGTH);
    	}

    	/*
    	 * New types won't be added too often, so simple assert() is enough.
    	 */
    	String type = e.getClass().getSimpleName();
    	assert(type.length() <= TYPE_LENGTH);
    	
        try {
            stmtInsertExplorable.setInt(1, e.getId());				// id
            stmtInsertExplorable.setString(2, relativeId);			// relativeId
            stmtInsertExplorable.setInt(3, e.getParentId());		// parentId
            stmtInsertExplorable.setString(4, value);				// value
            stmtInsertExplorable.setLong(5, e.getValueHashCode());	// hash
            stmtInsertExplorable.setString(6, type);				// type
            stmtInsertExplorable.setString(7, e.getSha());			// SHA
            stmtInsertExplorable.executeUpdate();

            System.out.println(
        			"Inserted Explorable into database: id = " + e.getId() + 
    				", relativeId = " + relativeId + 
    				", parentId = " + e.getParentId() + 
    				", value = " + value + 
    				", hash = " + e.getValueHashCode() + 
    				", type = " + type +
    				", SHA = " + e.getSha());
        } catch (SQLException ex) {
            System.out.println(
        			"FAILED to insert Explorable into database: id = " + e.getId() + 
    				", relativeId = " + relativeId + 
    				", parentId = " + e.getParentId() + 
    				", value = " + value + 
    				", hash = " + e.getValueHashCode() + 
    				", type = " + type +
    				", SHA = " + e.getSha());
        	
            throw new ExplorationException("Unable to insert explorable to database: " + e, ex);
        }
    }

    public Explorable getById(int id) throws ExplorationException {
        try {
            stmtSelectExplorableById.setInt(1, id);
            ResultSet rs = stmtSelectExplorableById.executeQuery();
            try {
                return rs.next() ? parseRow(rs) : null;
            } finally {
                rs.close();
            }
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to get Explorable by id: " + id, ex);
        }
    }

    private Explorable parseRow(ResultSet rs) throws SQLException, ExplorationException {
        int id = rs.getInt(1);
        String relativeId = rs.getString(2);
        int parentId = rs.getInt(3);
        String value = rs.getString(4);
        long hash = rs.getLong(5);
        String type = rs.getString(6);
        String sha = rs.getString(7);

        return Configuration.getExplorableFactory(type).newExplorable(type, id, parentId, relativeId, value, hash, sha);
    }

    public List<Explorable> getChildren(Explorable parent) throws ExplorationException {
        try {
            stmtSelectChildExplorables.setInt(1, parent.getId());
            ResultSet rs = stmtSelectChildExplorables.executeQuery();
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
            throw new ExplorationException("Unable to read child Explorables from the database", ex);
        }
    }

    public int size() throws ExplorationException {
        try {
            ResultSet rs = stmtSelectExplorablesCount.executeQuery();
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

        	stmtInsertExplorable.close();
            stmtSelectChildExplorables.close();
            stmtSelectExplorableById.close();
            stmtSelectExplorablesCount.close();
            stmtSelectFilesCount.close();
            connection.close();
        } catch (SQLException ex) {
        	IOException rethrow = new IOException("Unable to free one of the JDBC resources");
        	rethrow.initCause(ex);
        	throw rethrow;
        }
    }

    public int sizeFilesOnly() throws ExplorationException {
        try {
            ResultSet rs = stmtSelectFilesCount.executeQuery();
            try {
                rs.next();
                return rs.getInt(1);
            } finally {
                rs.close();		// TODO: Occasionally got an NPE here
            }
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to get files count from the database", ex);
        }
    }
    
    public void createIndexes() throws SQLException {
        PreparedStatement stmtCreateIndexes = connection.prepareStatement(getSql(SQL_CREATE_INDEXES));
        try {
            stmtCreateIndexes.executeUpdate();
        } finally {
            stmtCreateIndexes.close();
        }
    }
}
