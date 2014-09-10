package org.opencompare.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencompare.Snapshot;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Description;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

/**
 * This connection won't be shared between multiple threads, so its methods
 * don't have to be synchronized.
 */
public class JdbcDescriptionsDatabase extends AbstractJdbcDatabase implements DescriptionsDatabase {

    private static final String DESCRIPTION = Description.class.getSimpleName();

	public static final int RELATIVE_ID_LENGTH = 1000;
    
    static final String SQL_CREATE_DESCRIPTION_TABLE = "CREATE TABLE %TABLE% (" +
    		"id INT PRIMARY KEY, " +
    		"relativeId VARCHAR(" + RELATIVE_ID_LENGTH + "), " +
    		"parentId INT, " +
    		"value CLOB, " +
            "sha CHAR(" + JdbcExplorablesDatabase.SHA_LENGTH + "))";
    
    static final String SQL_CREATE_INDEX1 = "CREATE INDEX idxParent ON %TABLE% (parentId)";
    static final String SQL_CREATE_INDEX2 = "CREATE INDEX idxSha ON %TABLE% (sha)";
    
    static final String SQL_INSERT_DESCRIPTION = "INSERT INTO %TABLE% VALUES (?, ?, ?, ?, ?)";
    private PreparedStatement stmtInsertDescription;
    
    static final String SQL_SELECT_DESCRIPTION_BY_ID = "SELECT id, relativeId, parentId, value, sha FROM %TABLE% WHERE id = ? ";
    private PreparedStatement stmtSelectDescriptionById;
    
    static final String SQL_SELECT_DESCRIPTION_BY_SHA = "SELECT id, relativeId, parentId, value, sha FROM %TABLE% WHERE sha = ? ";
    private PreparedStatement stmtSelectDescriptionBySha;
    
    static final String SQL_SELECT_DESCRIPTION_VALUE_BY_PARENT_SHA = "SELECT c.value, c.sha FROM %TABLE% c, %TABLE% p WHERE p.sha = ? AND p.id = c.parentId";
    private PreparedStatement stmtSelectDescriptionValueByParentSha;
    
    static final String SQL_SELECT_CHILD_DESCRIPTIONS = "SELECT id, relativeId, parentId, value, sha FROM %TABLE% WHERE parentId = ? ORDER BY relativeId";
    private PreparedStatement stmtSelectChildDescriptions;
    
    static final String SQL_SELECT_DESCRIPTIONS_COUNT = "SELECT COUNT(*) FROM %TABLE%";
    private PreparedStatement stmtSelectDescriptionsCount;
    
    public JdbcDescriptionsDatabase(Snapshot snapshot, boolean createNew) throws SQLException, ClassNotFoundException, ExplorationException {
    	super(snapshot);
    	
        openConnection(snapshot.getName(), createNew);
        if (createNew) {
            createTables();
        }
        prepareStatements();
    }

    protected String getSql(String statement) {
    	return statement.replace("%TABLE%", "Descriptions");
    }
    
    private void prepareStatements() throws SQLException {
        stmtInsertDescription = connection.prepareStatement(getSql(SQL_INSERT_DESCRIPTION));
        stmtSelectChildDescriptions = connection.prepareStatement(getSql(SQL_SELECT_CHILD_DESCRIPTIONS));
        stmtSelectDescriptionsCount = connection.prepareStatement(getSql(SQL_SELECT_DESCRIPTIONS_COUNT));
        stmtSelectDescriptionById = connection.prepareStatement(getSql(SQL_SELECT_DESCRIPTION_BY_ID));
        stmtSelectDescriptionBySha = connection.prepareStatement(getSql(SQL_SELECT_DESCRIPTION_BY_SHA));
        stmtSelectDescriptionValueByParentSha = connection.prepareStatement(getSql(SQL_SELECT_DESCRIPTION_VALUE_BY_PARENT_SHA));
    }

    private void openConnection(String name, boolean create) throws ClassNotFoundException, SQLException {
    	System.out.println("Opening connection " + getSnapshot());
    	
        System.setProperty("derby.system.home", JdbcDatabaseManager.getDbFolder());
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        connection = DriverManager.getConnection("jdbc:derby:" + name + ";create=" + create);
    }

    private void createTables() throws SQLException {
        PreparedStatement stmtCreateExplorable = connection.prepareStatement(getSql(SQL_CREATE_DESCRIPTION_TABLE));
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
    	addDescription(e, stmtInsertDescription);
    }
    
    public void addReference(Explorable e) throws ExplorationException {
    	throw new UnsupportedOperationException("Explorables database doesn't support addReference()");
    }
    
    public void addActual(Explorable e) throws ExplorationException {
    	throw new UnsupportedOperationException("Explorables database doesn't support addActual()");
    }
    
    private void addDescription(Explorable e, PreparedStatement stmtInsertExplorable) throws ExplorationException {
		/*
		 * Relative ID should always be complete, it's an ID after all.
		 * Therefore we throw an exception if it's too long.
		 */
    	String relativeId = e.getRelativeId();
    	if (relativeId.length() > RELATIVE_ID_LENGTH) {
    		throw new ExplorationException("Description relative ID exceeds database column size: " + relativeId);
    	}
    	
    	String value = e.getValue() == null ? "" : e.getValue();
    	byte[] valueBytes = value.getBytes();
    	InputStream valueIs = new ByteArrayInputStream(valueBytes);
    	
        try {
            stmtInsertExplorable.setInt(1, e.getId());								// id
            stmtInsertExplorable.setString(2, relativeId);							// relativeId
            stmtInsertExplorable.setInt(3, e.getParentId());						// parentId
            stmtInsertExplorable.setAsciiStream(4, valueIs, valueBytes.length);		// value
            stmtInsertExplorable.setString(5, e.getSha());							// SHA
            stmtInsertExplorable.executeUpdate();
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to insert explorable to database: " + e, ex);
        }
    }

    public Description getById(int id) throws ExplorationException {
        try {
            stmtSelectDescriptionById.setInt(1, id);
            ResultSet rs = stmtSelectDescriptionById.executeQuery();
            try {
                return rs.next() ? parseRow(rs) : null;
            } finally {
                rs.close();
            }
        } catch (SQLException ex) {
            throw new ExplorationException("Unable to get Explorable by id: " + id, ex);
        }
    }

    /*
     * Key is child's SHA, value is child's value.
     */
    @SuppressWarnings("unchecked")
	public Map<String, String> getChildrenDescriptions(String parentSha) throws ExplorationException {
    	try {
    		Map<String, String> res = null;
    		
    		stmtSelectDescriptionValueByParentSha.setString(1, parentSha);
    		ResultSet rs = stmtSelectDescriptionValueByParentSha.executeQuery();
    		try {
    			while (rs.next()) {
    				Clob clob = rs.getClob(1);
    				String value = clob.getSubString(1, (int) clob.length());
    				String sha = rs.getString(2);
    				
    				if (res == null) {
    					// Lazy loading to avoid creating too many empty maps
    					res = new HashMap<String, String>();
    				}
    				res.put(sha, value);
    			}
    		} finally {
    			rs.close();
    		}
    		
    		return res == null ? Collections.EMPTY_MAP : res;
    	} catch (SQLException ex) {
    		throw new ExplorationException("Unable to get Description value by SHA: " + parentSha, ex);
    	}
    }
    
    public Description getBySha(String sha) throws ExplorationException {
    	try {
    		stmtSelectDescriptionBySha.setString(1, sha);
    		ResultSet rs = stmtSelectDescriptionBySha.executeQuery();
    		try {
    			return rs.next() ? parseRow(rs) : null;
    		} finally {
    			rs.close();
    		}
    	} catch (SQLException ex) {
    		throw new ExplorationException("Unable to get Description by SHA: " + sha, ex);
    	}
    }
    
    private Description parseRow(ResultSet rs) throws SQLException, ExplorationException {
        int id = rs.getInt(1);
        String relativeId = rs.getString(2);
        int parentId = rs.getInt(3);
        Clob clob = rs.getClob(4);
		String value = clob.getSubString(1, (int) clob.length());
        String sha = rs.getString(5);
        
        return (Description) Configuration.getExplorableFactory(DESCRIPTION).newExplorable(DESCRIPTION, id, parentId, relativeId, value, value.hashCode(), sha);
    }

    public List<Explorable> getChildren(Explorable parent) throws ExplorationException {
        try {
            stmtSelectChildDescriptions.setInt(1, parent.getId());
            ResultSet rs = stmtSelectChildDescriptions.executeQuery();
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
            ResultSet rs = stmtSelectDescriptionsCount.executeQuery();
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
        	
            stmtInsertDescription.close();
            stmtSelectChildDescriptions.close();
            stmtSelectDescriptionById.close();
            stmtSelectDescriptionBySha.close();
            stmtSelectDescriptionValueByParentSha.close();
            stmtSelectDescriptionsCount.close();
            connection.close();
        } catch (SQLException ex) {
        	IOException rethrow = new IOException("Unable to free one of the JDBC resources");
        	rethrow.initCause(ex);
        	throw rethrow;
        }
    }

    public int sizeFilesOnly() throws ExplorationException {
        throw new UnsupportedOperationException("Descriptions database does not support sizeFilesOnly()");
    }
    
    public void createIndexes() throws SQLException {
        PreparedStatement stmtCreateIndex1 = connection.prepareStatement(getSql(SQL_CREATE_INDEX1));
        try {
            stmtCreateIndex1.executeUpdate();
        } finally {
            stmtCreateIndex1.close();
        }
        
        PreparedStatement stmtCreateIndex2 = connection.prepareStatement(getSql(SQL_CREATE_INDEX2));
        try {
            stmtCreateIndex2.executeUpdate();
        } finally {
            stmtCreateIndex2.close();
        }
    }
}
