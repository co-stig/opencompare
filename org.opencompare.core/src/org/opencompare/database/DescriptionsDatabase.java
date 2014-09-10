package org.opencompare.database;

import java.util.Map;

import org.opencompare.explore.ExplorationException;


public interface DescriptionsDatabase extends Database {

	Map<String, String> getChildrenDescriptions(String parentSha) throws ExplorationException;

}
