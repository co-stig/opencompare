package org.opencompare.explorable.files;

import java.io.File;
import java.util.Arrays;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explore.ExplorationException;

public class FileFactory implements ExplorableFactory {
	
	private static final String TYPE_SIMPLE = SimpleFile.class.getSimpleName();
	private static final String TYPE_ZIP = ZipFile.class.getSimpleName();
	private static final String TYPE_FOLDER = Folder.class.getSimpleName();

	@Override
	public Explorable parseExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		System.out.println("org.opencompare.explorable.files.FileFactory.parseExplorable(" + type + ", " + id + ", " + parentId + ", " + relativeId + ", " + value + ", " + hash + ", " + sha + ")");
		if (type.equals(TYPE_FOLDER)) {
			return new Folder(id, parentId, new File(relativeId), sha);
		} else if (type.equals(TYPE_ZIP)) {
			return new ZipFile(id, parentId, new File(relativeId), sha);
		} else if (type.equals(TYPE_SIMPLE)) {
			return new SimpleFile(id, parentId, new File(relativeId), hash, sha);
		}
		return null;
	}

	@Override
	public Explorable createExplorable(Explorable origin, String type, Object... params) throws ExplorationException {
		System.out.println("org.opencompare.explorable.files.FileFactory.createExplorable(" + origin + ", " + type + ", " + Arrays.toString(params) + ")");
		if (type.equals(TYPE_FOLDER)) {
			return new Folder(Configuration.getSharedDatabase().nextId(), origin.getId(), (File) params[0], null);
		} else if (type.equals(TYPE_SIMPLE)) {
			File path = (File) params[0];
			String filename = path.getName().toLowerCase();
			if (filename.endsWith(".zip") || filename.endsWith(".jar")) {	// TODO: Add more known ZIP extensions
				return new ZipFile(Configuration.getSharedDatabase().nextId(), origin.getId(), (File) params[0], null);
			} else {
				return new SimpleFile(Configuration.getSharedDatabase().nextId(), origin.getId(), (File) params[0]);
			}
		}
		return null;
	}
	
}
