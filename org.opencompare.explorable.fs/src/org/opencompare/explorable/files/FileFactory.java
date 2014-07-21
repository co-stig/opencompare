package org.opencompare.explorable.files;

import java.io.File;

import org.opencompare.database.Database;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationQueue;

public class FileFactory implements ExplorableFactory {
	
	private static final String TYPE_SIMPLE = SimpleFile.class.getSimpleName();
	private static final String TYPE_XCONF = XConfFile.class.getSimpleName();
	private static final String TYPE_PROPERTIES = PropertiesFile.class.getSimpleName();
	private static final String TYPE_FOLDER = Folder.class.getSimpleName();

	/**
	 * Creates new file object with generated ID and calculated checksum. Used
	 * in the exploration phase.
	 */
	private SimpleFile newFile(int parentId, File path) throws ExplorationException {
		String filename = path.getName().toLowerCase();
		if (filename.endsWith(".properties") || filename.endsWith(".rbinfo")) {
			return new PropertiesFile(Configuration.getDatabase().nextId(), parentId, path);
		} else if (filename.endsWith(".xconf")) {
			return new XConfFile(Configuration.getDatabase().nextId(), parentId, path);
		} else {
			return new SimpleFile(Configuration.getDatabase().nextId(), parentId, path);
		}
	}

	/**
	 * Creates new file object with given ID and checksum. Used for parsing the
	 * database in comparison and display phases.
	 */
	private SimpleFile newFile(int id, int parentId, File path, long checksum, String sha) throws ExplorationException {
		String filename = path.getName().toLowerCase();
		if (filename.endsWith(".properties") || filename.endsWith(".rbinfo")) {
			return new PropertiesFile(id, parentId, path, checksum, sha);
		} else if (filename.endsWith(".xconf")) {
			return new XConfFile(id, parentId, path, checksum, sha);
		} else {
			return new SimpleFile(id, parentId, path, checksum, sha);
		}
	}

	/**
	 * Creates new folder object with generated ID. Used in the exploration
	 * phase.
	 */
	private Folder newFolder(int parentId, File path) throws ExplorationException {
		return newFolder(Configuration.getDatabase().nextId(), parentId, path, null);
	}

	/**
	 * Creates new folder object with given ID. Used for parsing the database in
	 * comparison and display phases.
	 */
	private Folder newFolder(int id, int parentId, File path, String sha) throws ExplorationException {
		return new Folder(id, parentId, path, sha);
	}

	@Override
	public Explorable newExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		if (type.equals(TYPE_FOLDER)) {
			return newFolder(id, parentId, new File(relativeId), sha);
		} else if (type.equals(TYPE_PROPERTIES) || type.equals(TYPE_XCONF) || type.equals(TYPE_SIMPLE)) {
			return newFile(id, parentId, new File(relativeId), hash, sha);
		} else {
			return null;
		}
	}

	@Override
	public Explorable newExplorable(Explorable origin, String type, Object... params) throws ExplorationException {
		return null;
	}
	
}
