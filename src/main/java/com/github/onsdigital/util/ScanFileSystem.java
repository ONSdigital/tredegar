package com.github.onsdigital.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Searches the file system
 */
public class ScanFileSystem {

	/**
	 * Iterates through the file system from a specified root directory and
	 * stores the file names
	 * 
	 * @param fileNames
	 *            a List to store results in
	 * @param dir
	 *            the root directory to start searching from
	 * @return the list with file names
	 * @throws IOException
	 *             if any file io operations fail
	 */
	public static List<String> getFileNames(List<String> fileNames, Path dir)
			throws IOException {
		System.out.println("ScanFileSystem getFileNames ...");

		if (fileNames == null || dir == null) {
			throw new IllegalArgumentException(
					"List of fileNames and Path dir cannot be null");
		}

		DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
		for (Path path : stream) {
			if (path.toFile().isDirectory())
				getFileNames(fileNames, path);
			else {
				fileNames.add(path.toAbsolutePath().toString());
			}
		}
		stream.close();

		System.out.println("ScanFileSystem number of files: " + fileNames.size());
		return fileNames;
	}
}
