package org.opencompare.explore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.opencompare.explorable.Explorable;

public class ExplorationQueue {

	private static final ExplorationQueue instance = new ExplorationQueue();
	private final BlockingQueue<Explorable> tasks = new LinkedBlockingQueue<Explorable>();
	
	public Explorable next() throws InterruptedException {
		return tasks.take();
	}
	
	public void add(Explorable e) throws InterruptedException {
		tasks.put(e);
	}
	
	public static ExplorationQueue getInstance() {
		return instance;
	}

}
