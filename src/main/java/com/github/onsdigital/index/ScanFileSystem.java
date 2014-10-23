package com.github.onsdigital.index;

import java.io.File;
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

		if (fileNames == null || dir == null) {
			throw new IllegalArgumentException(
					"List of fileNames and Path dir cannot be null");
		}

		DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
		for (Path path : stream) {
			if (path.toFile().isDirectory())
				getFileNames(fileNames, path);
			else {
				String fileName = path.toAbsolutePath().toString();

				if (isJsonFile(fileName)) {
					fileNames.add(fileName);
				}
			}
		}
		stream.close();

		return fileNames;
	}

	/**
	 * @param files
	 *            collection to store the files in
	 * @param dir
	 *            the root directory to start searching from
	 * @return collection of located files
	 * @throws IOException
	 *             if file lookup fails
	 */
	public static List<File> getFiles(List<File> files, Path dir)
			throws IOException {

		if (files == null || dir == null) {
			throw new IllegalArgumentException(
					"List of fileNames and Path dir cannot be null");
		}

		DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
		for (Path path : stream) {
			if (path.toFile().isDirectory()) {
				getFiles(files, path);
			} else {
				File file = path.toFile();
				if (file.getName().equals("bulletin.json")) {
					files.add(file);
				}
			}
		}
		stream.close();

		return files;
	}

	private static boolean isJsonFile(String fileName) {
		return fileName.endsWith(".json");
	}
}
