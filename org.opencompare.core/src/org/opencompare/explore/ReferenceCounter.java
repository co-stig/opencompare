package org.opencompare.explore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: Synchronize all methods instead?
// TODO: This is not used right now, see also GarbageCollector
public class ReferenceCounter {
	
	private final Map<Integer, AtomicInteger> data = Collections.synchronizedMap(new HashMap<Integer, AtomicInteger>());
	
	/**
	 * Called before children are enqueued for exploration, used for reference
	 * counting to free resources.
	 */
	public void setNumberOfChildren(int parentId, int numberOfChildren) {
		if (numberOfChildren > 0) {
			data.put(parentId, new AtomicInteger(numberOfChildren));
		}
	}

	/**
	 * Called every time a child explored, used for reference counting to free
	 * resources.
	 * 
	 * @return true if there's no more children to explore and resources can be
	 *         freed.
	 */
	public boolean oneChildExplored(int parentId) {
		AtomicInteger i = data.get(parentId);
		if (i == null) {
			// We didn't bother to add this explorable for reference counting -- ignore
			return false;
		}
		
		if (i.decrementAndGet() <= 0) {
			data.remove(parentId);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasUnexploredChildren(int parentId) throws ExplorationException {
		AtomicInteger i = data.get(parentId);
		return i != null && i.get() > 0;
	}	
}
