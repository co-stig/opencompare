package org.opencompare.database;

import java.io.File;

import org.opencompare.Snapshot;
import org.opencompare.WithProgress;
import org.opencompare.explore.ExplorationException;

public interface DatabaseManager {
    
	Snapshot createExplorablesDatabase(String name) throws ExplorationException;

	Snapshot createConflictsDatabase(String name) throws ExplorationException;

	Snapshot createDescriptionsDatabase() throws ExplorationException;

	Database newExplorablesConnection(Snapshot snapshot) throws ExplorationException;

	Database newConflictsConnection(Snapshot snapshot) throws ExplorationException;

	DescriptionsDatabase newDescriptionsConnection() throws ExplorationException;

	Database newConnection(Snapshot snapshot) throws ExplorationException;

	Snapshot[] listSnapshots();

	Snapshot getSnapshot(String name);

	void importSnapshot(final File input, final WithProgress progress);

	void exportSnapshot(final Snapshot snapshot, final File output, final WithProgress progress);

}
