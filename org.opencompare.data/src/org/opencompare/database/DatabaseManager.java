package org.opencompare.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.opencompare.Snapshot;
import org.opencompare.WithProgress;
import org.opencompare.WithProgressAdapter;
import org.opencompare.Snapshot.State;
import org.opencompare.Snapshot.Type;
import org.opencompare.explore.ExplorationException;

public class DatabaseManager {
    
	private final static String DEFAULT_DB_FOLDER = "db";
	private static final String DESCRIPTIONS_DB = "descriptions";
	private final static String DB_FOLDER = System.getProperty("db.folder", DEFAULT_DB_FOLDER);


	public static synchronized Snapshot createExplorablesDatabase(String name) throws ExplorationException {
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

	public static synchronized Snapshot createConflictsDatabase(String name) throws ExplorationException {
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

	public static synchronized Snapshot createDescriptionsDatabase() throws ExplorationException {
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

	public static synchronized JdbcExplorablesDatabase newExplorablesConnection(Snapshot snapshot) throws ExplorationException {
		try {
			return new JdbcExplorablesDatabase(snapshot, false);
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + snapshot + "'", ex);
		}
	}

	public static synchronized JdbcConflictsDatabase newConflictsConnection(Snapshot snapshot) throws ExplorationException {
		try {
			return new JdbcConflictsDatabase(snapshot, false);
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + snapshot + "'", ex);
		}
	}

	public static synchronized JdbcDescriptionsDatabase newDescriptionsConnection() throws ExplorationException {
		try {
			return new JdbcDescriptionsDatabase(getSnapshot(DESCRIPTIONS_DB), false);
		} catch (Exception ex) {
			throw new ExplorationException("Unable to connect to database '" + DESCRIPTIONS_DB + "'", ex);
		}
	}

	public static synchronized Database newConnection(Snapshot snapshot) throws ExplorationException {
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

	public static Snapshot[] listSnapshots() {
		List<Snapshot> res = new ArrayList<Snapshot>();
		for (File f : new File(getDbFolder()).listFiles()) {
			if (f.isDirectory()) {
				res.add(new Snapshot(f));
			}
		}
		return res.toArray(new Snapshot[0]);
	}

	public static Snapshot getSnapshot(String name) {
		File folder = getSnapshotFolder(name);
		return folder.exists() ? new Snapshot(folder) : null;
	}

	private static File getSnapshotFolder(String name) {
		return new File(getDbFolder() + File.separator + name);
	}

	private static void unzipSingleFile(ZipFile zipFile, ZipEntry entry, File folder, WithProgressAdapter progress) throws ZipException, IOException {
		// It seems that:
		// 1. Entries in zip file are sorted such that directories always come
		// first before the files contained.
		// 2. Directory names always end with slash /.

		String fullFilename = folder.getAbsolutePath() + File.separator + entry.getName();
		File outputFile = new File(fullFilename);

		if (fullFilename.endsWith("/")) {
			// It's a directory
			outputFile.mkdirs();
		} else {
			// It's a file
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
			try {
				// No need to close those, as soon as we close the whole file at
				// the end
				InputStream is = zipFile.getInputStream(entry);

				int len = 0;
				byte[] buffer = new byte[8192];
				while ((len = is.read(buffer)) >= 0) {
					progress.increment(len);
					out.write(buffer, 0, len);
				}
			} finally {
				out.close();
			}
		}
	}

	private static void unzip(File inputZipFile, File targetFolder, WithProgressAdapter progress) throws ZipException, IOException {
		ZipFile zf = new ZipFile(inputZipFile);
		try {
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				unzipSingleFile(zf, entries.nextElement(), targetFolder, progress);
			}
		} finally {
			zf.close();
		}
	}

	public static void importSnapshot(final File input, final WithProgress progress) {
		new Thread() {
			public void run() {
				progress.setMaximum((int) input.length());
				progress.start();
				try {
					unzip(
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

	private static void zipSingleFile(ZipOutputStream out, File file, String entryName, WithProgressAdapter progress) throws ZipException, IOException {
		if (file.isDirectory()) {
			entryName += "/";
			out.putNextEntry(new ZipEntry(entryName));
			out.closeEntry();
			for (File f : file.listFiles()) {
				zipSingleFile(out, f, entryName + f.getName(), progress);
			}
		} else {
			out.putNextEntry(new ZipEntry(entryName));

			// "Copy" the file
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			try {
				int len = 0;
				byte[] buffer = new byte[8192];
				while ((len = is.read(buffer)) >= 0) {
					progress.increment(len);
					out.write(buffer, 0, len);
				}
			} finally {
				is.close();
			}

			out.closeEntry();
		}
	}

	private static void zip(File inputFolder, String root, File outputZipFile, WithProgressAdapter progress) throws FileNotFoundException, IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputZipFile));
		try {
			zipSingleFile(out, inputFolder, root, progress);
		} finally {
			out.finish();
			out.close();
		}
	}

	public static void exportSnapshot(final Snapshot snapshot, final File output, final WithProgress progress) {
		new Thread() {
			public void run() {
				progress.setMaximum((int) snapshot.getSize());
				progress.start();
				try {
					zip(
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
        new File(DB_FOLDER).mkdirs();   // Make sure it exists
        return DB_FOLDER;
    }
}
