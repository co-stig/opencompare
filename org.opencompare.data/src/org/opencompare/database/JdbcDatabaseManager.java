package org.opencompare.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencompare.Snapshot;
import org.opencompare.Snapshot.State;
import org.opencompare.Snapshot.Type;
import org.opencompare.WithProgress;
import org.opencompare.WithProgressAdapter;
import org.opencompare.core.ZipUtility;
import org.opencompare.explore.ExplorationException;

public class JdbcDatabaseManager implements DatabaseManager {
    
	private final static String DEFAULT_DB_FOLDER = "db";
	private final static String DESCRIPTIONS_DB = "descriptions";
	private final static String DB_FOLDER = System.getProperty("db.folder", DEFAULT_DB_FOLDER);

	public synchronized Snapshot createExplorablesDatabase(String name) throws ExplorationException {
		JdbcExplorablesDatabase db = null;
		try {
			Snapshot s = new Snapshot(getSnapshotFolder(name), name, "1.0", null, null, "Explorables", State.Empty, Type.Snapshot);
			db = new JdbcExplorablesDatabase(s, true);
			s.save();
			return s;
		} catch (Exception ex) {
			throw new ExplorationException("Unable to create new database", ex);
		} finally {
			try {
				if (db != null) {
					db.close();
				}
			} catch (IOException ex) {
				throw new ExplorationException("Unable to close database connection after the database is created", ex);
			}
		}
	}

	public synchronized Snapshot createConflictsDatabase(String name) throws ExplorationException {
		JdbcConflictsDatabase db = null;
		try {
			Snapshot s = new Snapshot(getSnapshotFolder(name), name, "1.0", "Reference", "Actual", "Conflicts", State.Empty, Type.Conflicts);
			db = new JdbcConflictsDatabase(s, true);
			s.save();
			return s;
		} catch (Exception ex) {
			throw new ExplorationException("Unable to create new database", ex);
		} finally {
			try {
				if (db != null) {
					db.close();
				}
			} catch (IOException ex) {
				throw new ExplorationException("Unable to close database connection after the database is created", ex);
			}
		}
	}

	public synchronized Snapshot createDescriptionsDatabase() throws ExplorationException {
		JdbcDescriptionsDatabase db = null;
		try {
			Snapshot s = new Snapshot(getSnapshotFolder(DESCRIPTIONS_DB), DESCRIPTIONS_DB, "1.0", null, null, "Descriptions", State.Empty, Type.Descriptions);
			db = new JdbcDescriptionsDatabase(s, true);
			s.save();
			return s;
		} catch (Exception ex) {
			throw new ExplorationException("Unable to create new database", ex);
		} finally {
			try {
				if (db != null) {
					db.close();
				}
			} catch (IOException ex) {
				throw new ExplorationException("Unable to close database connection after the database is created", ex);
			}
		}
	}

	public synchronized JdbcExplorablesDatabase newExplorablesConnection(Snapshot snapshot) throws ExplorationException {
		try {
			return new JdbcExplorablesDatabase(snapshot, false);
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + snapshot + "'", ex);
		}
	}

	public synchronized JdbcConflictsDatabase newConflictsConnection(Snapshot snapshot) throws ExplorationException {
		try {
			return new JdbcConflictsDatabase(snapshot, false);
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + snapshot + "'", ex);
		}
	}

	public synchronized JdbcDescriptionsDatabase newDescriptionsConnection() throws ExplorationException {
		try {
			return new JdbcDescriptionsDatabase(getSnapshot(DESCRIPTIONS_DB), false);
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + DESCRIPTIONS_DB + "'", ex);
		}
	}

	public synchronized Database newConnection(Snapshot snapshot) throws ExplorationException {
		try {
			if (snapshot.getType() == Snapshot.Type.Conflicts) {
				return new JdbcConflictsDatabase(snapshot, false);
			} else if (snapshot.getType() == Snapshot.Type.Descriptions) {
				return new JdbcDescriptionsDatabase(snapshot, false);
			} else if (snapshot.getType() == Snapshot.Type.Snapshot) {
				return new JdbcExplorablesDatabase(snapshot, false);
			} else {
				throw new ExplorationException("Trying to connect to unknown database: " + snapshot);
			}
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + snapshot + "'", ex);
		}
	}

	public Snapshot[] listSnapshots() {
		List<Snapshot> res = new ArrayList<Snapshot>();
		for (File f : new File(getDbFolder()).listFiles()) {
			if (f.isDirectory()) {
				res.add(new Snapshot(f));
			}
		}
		return res.toArray(new Snapshot[0]);
	}

	public Snapshot getSnapshot(String name) {
		File folder = getSnapshotFolder(name);
		return folder.exists() ? new Snapshot(folder) : null;
	}

	private File getSnapshotFolder(String name) {
		return new File(getDbFolder() + File.separator + name);
	}

	public void importSnapshot(final File input, final WithProgress progress) {
		new Thread() {
			public void run() {
				progress.setMaximum((int) input.length());
				progress.start();
				try {
					ZipUtility.unzip(
                                                input, 
                                                new File(getDbFolder()), 
                                                new WithProgressAdapter(progress, 1024 * 1024) // Notify each megabyte
                                            );
					progress.complete(true);
				} catch (Exception ex) {
					System.out.println("Unable to unzip snapshot: " + input);
					ex.printStackTrace();
					progress.complete(false);
				}
			}
		}.start();
	}

	public void exportSnapshot(final Snapshot snapshot, final File output, final WithProgress progress) {
		new Thread() {
			public void run() {
				progress.setMaximum((int) snapshot.getSize());
				progress.start();
				try {
					ZipUtility.zip(
                                                snapshot.getFolder(), 
                                                snapshot.getName(), 
                                                output, 
                                                new WithProgressAdapter(progress, 1024 * 1024) // Notify each megabyte
                                            );
					progress.complete(true);
				} catch (Exception ex) {
					System.out.println("Unable to zip snapshot: " + snapshot);
					ex.printStackTrace();
					progress.complete(false);
				}
			}
		}.start();
	}

    static String getDbFolder() {
        File folder = new File(DB_FOLDER);
		folder.mkdirs();   // Make sure it exists
        return folder.getAbsolutePath();
    }
}
