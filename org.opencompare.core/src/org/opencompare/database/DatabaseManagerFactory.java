package org.opencompare.database;

// A really dumb singleton
public class DatabaseManagerFactory {
    
	private static DatabaseManager inst = null;
	
	public static void register(DatabaseManager manager) {
		inst = manager;
	}
	
	public static void unregister() {
		inst = null;
	}
	
	public static DatabaseManager get() {
		return inst;
	}

}
