package org.opencompare.explorable;

import org.opencompare.explore.ExplorationException;

public class Root extends Explorable {

	/**
	 * Root always has the same ID -- one (1).
	 */
    public Root() throws ExplorationException {
        super(1, 0, "root");
    }

    @Override
    public String toString() {
        return "Root";
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
