package org.opencompare.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class FolderDisposer implements Closeable {

	private final File folderToDelete;
	
	public FolderDisposer(File folderToDelete) {
		this.folderToDelete = folderToDelete;
	}


	@Override
	public String toString() {
		return "FolderDisposer [folderToDelete=" + folderToDelete + "]";
	}

	@Override
	public void close() throws IOException {
		deleteRecursive(folderToDelete);
	}

	private void deleteRecursive(File file) {
		if (file.isDirectory()) {
			for (File child: file.listFiles()) {
				deleteRecursive(child);
			}
		}
		file.delete();
	}
	
}
