package org.opencompare.explorable.files;

import java.io.File;

import org.opencompare.explorable.Explorable;

public class Folder extends Explorable {

	private final File path;

	/**
	 * @param path Never null
	 */
	public Folder(int id, int parentId, File path, String sha) {
		super(id, parentId, sha);
		this.path = path;
	}


	public File getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Folder [path=" + path + "]";
	}

	@Override
	public String getRelativeId() {
		return path.getName();
	}

	@Override
	public String getValue() {
		return "";
	}

	/**
	 * Used mostly for estimation purposes
	 */
	public int countChildren() {
		return countChildren(path);
	}

	public static int countChildren(File folder) {
		int count = 0;
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					count += countChildren(f);
				} else if (f.isFile()) {
					++count;
				}
			}
		}
		return count;
	}

	@Override
	public long getValueHashCode() {
		return 0;
	}

    @Override
    public String getUserFriendlyValue() {
        return "[Folder]";
    }
}
