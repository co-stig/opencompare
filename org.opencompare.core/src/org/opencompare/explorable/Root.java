package org.opencompare.explorable;

import java.io.File;

import org.opencompare.explore.ExplorationException;

public class Root extends Explorable {

    private final File path;

	/**
	 * Root always has the same ID -- one (1).
	 */
    public Root(File path, String sha) throws ExplorationException {
        super(1, 0, sha);
        this.path = path;
    }

    public File getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Root [path=" + path + "]";
    }

    @Override
    public String getRelativeId() {
        return "root";
    }

    @Override
    public String getValue() {
        return "";
    }

	@Override
	public long getValueHashCode() {
		return 0;
	}

    @Override
    public String getUserFriendlyValue() {
        return "[Root]";
    }
}
