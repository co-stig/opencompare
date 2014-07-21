package org.opencompare.explore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.opencompare.explorable.Explorable;

public class ExplorationQueue {

	private final BlockingQueue<Explorable> tasks = new LinkedBlockingQueue<Explorable>();
	
	public ExplorationQueue(Explorable root) {
		try {
			tasks.put(root);
		} catch (InterruptedException e) {
			// This will never happen, because we are in the constructor
		}
	}

	public Explorable next() throws InterruptedException {
		return tasks.take();
	}
	
	public void add(Explorable e) throws InterruptedException {
		tasks.put(e);
	}

}
