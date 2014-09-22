package org.opencompare.explore;

import org.opencompare.explorable.Explorable;

// TODO: Implement, will be used for handling ZIP files and such
public interface GarbageCollector {

	public interface Callback {
		void afterAllChildrenExplored(Explorable root);
	}
	
	void registerOneTimeCallback(Explorable root, Callback callback);
	
}
