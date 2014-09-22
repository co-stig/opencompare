package org.opencompare.explorable.files.java;

import java.io.File;

import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;

public class PropertiesFile extends SimpleFile {

	public PropertiesFile(int id, int parentId, File path, long checksum, String sha) throws ExplorationException {
        super(id, parentId, path, checksum, sha);
	}
	
	public PropertiesFile(int id, int parentId, File path) throws ExplorationException {
		super(id, parentId, path);
	}

	/**
	 * Properties file value is defined by contained name-value pairs, similarly
	 * to file-folder relationship.
	 */
	public String getValue() {
		return "";
	}

    @Override
    public String getUserFriendlyValue() {
        return "[Properties]";
    }
}
