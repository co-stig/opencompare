package org.opencompare.explorable.files.java;

import java.io.File;
import java.util.Arrays;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;

public class FileFactory implements ExplorableFactory {
	
	private static final String TYPE_SIMPLE = SimpleFile.class.getSimpleName();
	private static final String TYPE_XCONF = XConfFile.class.getSimpleName();
	private static final String TYPE_PROPERTIES = PropertiesFile.class.getSimpleName();

	@Override
	public Explorable parseExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		System.out.println("org.opencompare.explorable.files.js.FileFactory.newExplorable1(" + type + ", " + id + ", " + parentId + ", " + relativeId + ", " + value + ", " + hash + ", " + sha + ")");
		if (type.equals(TYPE_PROPERTIES) || type.equals(TYPE_XCONF)) {
			File path = new File(relativeId);
			String filename = path.getName().toLowerCase();
			if (filename.endsWith(".properties") || filename.endsWith(".rbinfo")) {
				return new PropertiesFile(id, parentId, path, hash, sha);
			} else if (filename.endsWith(".xconf")) {
				return new XConfFile(id, parentId, path, hash, sha);
			}
		}
		return null;
	}

	@Override
	public Explorable createExplorable(Explorable origin, String type, Object... params) throws ExplorationException {
		System.out.println("org.opencompare.explorable.files.js.FileFactory.newExplorable2(" + origin + ", " + type + ", " + Arrays.toString(params) + ")");
		if (type.equals(TYPE_SIMPLE)) {
			File path = (File) params[0];
			String filename = path.getName().toLowerCase();
			if (filename.endsWith(".properties") || filename.endsWith(".rbinfo")) {
				return new PropertiesFile(Configuration.getSharedDatabase().nextId(), origin.getId(), path);
			} else if (filename.endsWith(".xconf")) {
				return new XConfFile(Configuration.getSharedDatabase().nextId(), origin.getId(), path);
			}
		}
		return null;
	}
	
}
