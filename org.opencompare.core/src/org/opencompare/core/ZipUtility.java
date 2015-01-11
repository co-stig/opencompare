package org.opencompare.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.opencompare.WithProgressAdapter;

public class ZipUtility {

	public interface Progress {
		// For files only
		void onStart(ZipEntry entry, File outputFile);
		void onFinish(ZipEntry entry, File outputFile);
		
		// For folders only
		void onFolder(ZipEntry entry, File folder);
	}
	
	public static void unzip(File inputZipFile, File targetFolder, WithProgressAdapter progress) throws ZipException, IOException {
		unzip(inputZipFile, targetFolder, progress, null);
	}
	
	public static void unzip(File inputZipFile, File targetFolder, WithProgressAdapter progress, Progress zipProgress) throws ZipException, IOException {
		ZipFile zf = new ZipFile(inputZipFile);
		try {
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				unzipSingleFile(zf, entries.nextElement(), targetFolder, progress, zipProgress);
			}
		} finally {
			zf.close();
		}
	}

	private static void unzipSingleFile(ZipFile zipFile, ZipEntry entry, File folder, WithProgressAdapter progress, Progress zipProgress) throws ZipException, IOException {
		// It seems that:
		// 1. Entries in zip file are sorted such that directories always come
		// first before the files contained.
		// 2. Directory names always end with slash /.

		String fullFilename = folder.getAbsolutePath() + File.separator + entry.getName();
		File outputFile = new File(fullFilename);

		if (fullFilename.endsWith("/")) {
			// It's a directory
			outputFile.mkdirs();
			if (zipProgress != null) {
				zipProgress.onFolder(entry, outputFile);
			}
		} else {
			// It's a file
			if (zipProgress != null) {
				zipProgress.onStart(entry, outputFile);
			}
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
			try {
				// No need to close those, as soon as we close the whole file at
				// the end
				InputStream is = zipFile.getInputStream(entry);

				int len = 0;
				byte[] buffer = new byte[8192];
				while ((len = is.read(buffer)) >= 0) {
					if (progress != null) {
						progress.increment(len);
					}
					out.write(buffer, 0, len);
				}
			} finally {
				out.close();
				if (zipProgress != null) {
					zipProgress.onFinish(entry, outputFile);
				}
			}
		}
	}

	public static void zip(File inputFolder, String root, File outputZipFile, WithProgressAdapter progress) throws FileNotFoundException, IOException {
		zip(inputFolder, root, outputZipFile, progress, null);
	}
	
	public static void zip(File inputFolder, String root, File outputZipFile, WithProgressAdapter progress, Progress zipProgress) throws FileNotFoundException, IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputZipFile));
		try {
			zipSingleFile(out, inputFolder, root, progress, zipProgress);
		} finally {
			out.finish();
			out.close();
		}
	}

	private static void zipSingleFile(ZipOutputStream out, File file, String entryName, WithProgressAdapter progress, Progress zipProgress) throws ZipException, IOException {
		ZipEntry entry = new ZipEntry(entryName);
		if (file.isDirectory()) {
			entryName += "/";
			out.putNextEntry(entry);
			out.closeEntry();
			for (File f : file.listFiles()) {
				zipSingleFile(out, f, entryName + f.getName(), progress, zipProgress);
			}
			if (zipProgress != null) {
				zipProgress.onFolder(entry, file);
			}
		} else {
			if (zipProgress != null) {
				zipProgress.onStart(entry, file);
			}
			out.putNextEntry(entry);

			// "Copy" the file
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			try {
				int len = 0;
				byte[] buffer = new byte[8192];
				while ((len = is.read(buffer)) >= 0) {
					if (progress != null) {
						progress.increment(len);
					}
					out.write(buffer, 0, len);
				}
			} finally {
				is.close();
			}

			out.closeEntry();
			if (zipProgress != null) {
				zipProgress.onFinish(entry, file);
			}
		}
	}

}
