package org.opencompare.explorable.files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

public class SimpleFile extends Explorable {

    private final long checksum;
    private final File path;

    public SimpleFile(int id, int parentId, File path, long checksum, String sha) {
        super(id, parentId, sha);
        this.path = path;
        this.checksum = checksum;
    }

    public SimpleFile(int id, int parentId, File path) throws ExplorationException {
        this(id, parentId, path, calculateChecksum(path), null);
    }
    
	private static long calculateChecksum(File path) throws ExplorationException {
		// TODO: Try NIO here?
		try {
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(path));
			try {
				return crc32(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			throw new ExplorationException(e);
		}
	}

	public File getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "SimpleFile [checksum=" + checksum + ", path=" + path + "]";
    }

    @Override
    public String getRelativeId() {
        return path.getName();
    }

    @Override
    public String getValue() {
        return Long.toString(getValueHashCode());
    }

	@Override
	public long getValueHashCode() {
		return checksum;
	}

    @Override
    public String getUserFriendlyValue() {
        return "[File]";
    }
}
