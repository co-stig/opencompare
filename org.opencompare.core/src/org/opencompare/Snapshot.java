package org.opencompare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Properties;

import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.ProcessConfiguration;

// TODO: Incorporate some logic inside, so that this class is lifecycle-aware,
// e.g. fill completedOn date with the current time when the state goes to Finished.
public class Snapshot implements Comparable<Snapshot> {

	public enum State {
		Empty, InProgress, Finished, Aborted, Broken
	}
	
	public enum Type {
		Snapshot, Conflicts, Descriptions, Unknown
	}
	
	private final File folder;
    private long size;
    
    // These are read from properties file
    
    private Date completedOn;		// Can be null
    private Date createdOn;			// Can be null
    private Date lastModified;		// Can be null
    private String name;
    private String version;
    private String toolVersion;
    private String referenceTable;	// Can be null
    private String actualTable;		// Can be null
    private String mainTable;		// Can be null
    private String author;
    private State state;
    private Type type;

	/**
	 * This constructor is used for loading existing snapshots. Be careful -- it
	 * doesn't preserve additional properties, i.e. if the snapshot with options
	 * is reloaded, option values will be lost.
	 * 
	 * TODO: Handle all properties correctly.
	 */
    public Snapshot(File folder) {
        this.folder = folder;
        size = calculateSizeRecursive(folder);

        File propertiesFile = getPropertiesFile(folder);
        Properties prop = null;
        
        if (propertiesFile.exists()) {
        	try {
				prop = loadProperties(propertiesFile);
			} catch (IOException e) {
				/*
				 * We still have to display something to the user, so don't
				 * rethrow exception here, but rather ignore it and display the
				 * snapshot as "broken".
				 */
			}
        }
        
        if (prop == null) {
        	lastModified = null;
        	createdOn = null;
        	completedOn = null;
        	name = folder.getName();
        	version = "N/A";
        	toolVersion = "N/A";
        	referenceTable = null;
        	actualTable = null;
        	mainTable = null;
        	author = "N/A";
        	state = State.Broken;
        	type = Type.Unknown;
        } else {
        	// No checks for nulls here -- let the user see ugly exceptions!
        	// TODO: Be careful, make some of them optional, or handle different app versions otherwise somehow
        	lastModified = new Date(Long.parseLong(prop.getProperty("lastModified")));
        	createdOn = new Date(Long.parseLong(prop.getProperty("createdOn")));
        	completedOn = new Date(Long.parseLong(prop.getProperty("completedOn")));
        	name = prop.getProperty("name");
        	version = prop.getProperty("version");
        	toolVersion = prop.getProperty("toolVersion");
        	referenceTable = prop.getProperty("referenceTable");
        	actualTable = prop.getProperty("actualTable");
        	mainTable = prop.getProperty("mainTable");
        	author = prop.getProperty("author");
        	state = State.valueOf(prop.getProperty("state"));
        	type = Type.valueOf(prop.getProperty("type"));
        }
    }

    /**
	 * This constructor is used for creating brand new snapshots. Suppose we
	 * already have the snapshot folder, although it might be empty.
	 */
    public Snapshot(File folder, String name, String version, String referenceTable, String actualTable, String mainTable, State state, Type type) throws IOException {
        this.folder = folder;
        this.size = 0;
    	this.lastModified = new Date();
    	this.createdOn = new Date();
    	this.completedOn = new Date();
    	this.name = name;
    	this.version = version;
    	this.toolVersion = ExploreApplication.TOOL_VERSION;
    	this.referenceTable = referenceTable;
    	this.actualTable = actualTable;
    	this.mainTable = mainTable;
    	this.author = ExploreApplication.getUser();
    	this.state = state;
    	this.type = type;
		/*
		 * We don't save it here because this means we should create the folder
		 * before creating a database there, and Derby won't like it.
		 */
    }
    
    // Methods used in constructor are private static to ensure we don't override anything
    
	private static Properties loadProperties(File propertiesFile) throws IOException {
		Properties prop = new Properties();
		InputStream in = new FileInputStream(propertiesFile);
		try {
			prop.load(in);
		} finally {
			in.close();
		}
		return prop;
	}
	
    private static File getPropertiesFile(File folder) {
    	return new File(folder.getAbsolutePath() + File.separator + "snapshot.properties");
    }
    
    private static long calculateSizeRecursive(File path) {
        long total = 0;
        if (path.isFile()) {
            return path.length();
        } else {
            for (File f: path.listFiles()) {
                total += calculateSizeRecursive(f);
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return name + " " + version + " (" + folder + ")";
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((folder == null) ? 0 : folder.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Snapshot other = (Snapshot) obj;
		if (folder == null) {
			if (other.folder != null)
				return false;
		} else if (!folder.equals(other.folder))
			return false;
		return true;
	}

	public int compareTo(Snapshot o) {
        return this.name.compareTo(o.name);
    }

    public String getLastModifiedFormatted() {
        return getDateFormatted(lastModified);
    }

    public String getCreatedOnFormatted() {
    	return getDateFormatted(createdOn);
    }
    
    public String getCompletedOnFormatted() {
    	return getDateFormatted(completedOn);
    }
    
    private static String getDateFormatted(Date date) {
    	return 
    			date == null ? 
				"N/A" : 
				DateFormat.getDateTimeInstance().format(date);
    }
    
    public String getSizeFormatted() {
        return NumberFormat.getIntegerInstance().format(size);
    }
    
    public void delete() {
        deleteRecursive(folder);
    }

    private void deleteRecursive(File path) {
        if (path.isDirectory()) {
            for (File f: path.listFiles()) {
                deleteRecursive(f);
            }
        }
        path.delete();
    }

    public void save() throws IOException {
    	save(null);
    }
    
	public void save(ProcessConfiguration config) throws IOException {
		lastModified = new Date();

		Properties prop = new Properties();
		prop.setProperty("lastModified", Long.toString(lastModified.getTime()));
		prop.setProperty("createdOn", Long.toString(createdOn.getTime()));
		prop.setProperty("completedOn", Long.toString(completedOn.getTime()));
		prop.setProperty("name", name);
		prop.setProperty("version", version);
		prop.setProperty("toolVersion", toolVersion);
		if (referenceTable != null) {
			prop.setProperty("referenceTable", referenceTable);
		}
		if (actualTable != null) {
			prop.setProperty("actualTable", actualTable);
		}
		if (mainTable != null) {
			prop.setProperty("mainTable", mainTable);
		}
		prop.setProperty("author", author);
		prop.setProperty("state", state.toString());
		prop.setProperty("type", type.toString());

		ApplicationConfiguration.getInstance().appendConfiguration(prop);
		if (config != null) {
			config.appendConfiguration(prop);
		}
		
		OutputStream out = new FileOutputStream(getPropertiesFile(folder));
		try {
			prop.store(out, "Snapshot metadata");
		} finally {
			out.close();
		}
	}
    
    // Getters
    
    public Date getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public File getFolder() {
        return folder;
    }

    public long getSize() {
        return size;
    }

	public String getVersion() {
		return version;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public String getReferenceTable() {
		return referenceTable;
	}

	public String getActualTable() {
		return actualTable;
	}

	public String getMainTable() {
		return mainTable;
	}

	public String getAuthor() {
		return author;
	}

	public State getState() {
		return state;
	}

	public Type getType() {
		return type;
	}

	// Setters, which will trigger properties file rewrite

	public void setLastModified(Date lastModified) throws IOException {
		this.lastModified = lastModified;
		save();
	}

	public void setName(String name) throws IOException {
		this.name = name;
		save();
	}

	public void setVersion(String version) throws IOException {
		this.version = version;
		save();
	}

	public void setToolVersion(String toolVersion) throws IOException {
		this.toolVersion = toolVersion;
		save();
	}

	public void setReferenceTable(String referenceTable) throws IOException {
		this.referenceTable = referenceTable;
		save();
	}

	public void setActualTable(String actualTable) throws IOException {
		this.actualTable = actualTable;
		save();
	}

	public void setMainTable(String mainTable) throws IOException {
		this.mainTable = mainTable;
		save();
	}

	public void setAuthor(String author) throws IOException {
		this.author = author;
		save();
	}

	public void setState(State state) throws IOException {
		this.state = state;
		if (state.equals(State.Finished) || state.equals(State.Aborted)) {
			setCompletedOn(new Date());
		}
		save();
	}

	public void setType(Type type) throws IOException {
		this.type = type;
		save();
	}

	public void recalculateSize() throws IOException {
		this.size = calculateSizeRecursive(folder);
		save();
	}

	public Date getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Date completedOn) throws IOException {
		this.completedOn = completedOn;
		save();
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) throws IOException {
		this.createdOn = createdOn;
		save();
	}
}
