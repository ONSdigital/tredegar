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

				if (isValidFileExtensionType(fileName) && isValidFilePath(fileName)) {
					fileNames.add(fileName);
					System.out.println("ScanFileSystem added this file: " + fileName);
				} else {
					System.out.println("ScanFileSystem ignored this file: " + fileName);
				}
			}
		}
		stream.close();

		return fileNames;
	}

	private static boolean isValidFilePath(String fileName) {
		if (fileName.contains("target/classes/files/businessindustryandtrade")
				|| fileName.contains("target/classes/files/economy")
				|| fileName.contains("target/classes/files/employmentandlabourmarket")
				|| fileName.contains("target/classes/file/peoplepopulationandcommunity")) {
			return true;
		}
		return false;
	}

	private static boolean isValidFileExtensionType(String fileName) {
		return fileName.endsWith(".html") || fileName.endsWith(".xls") || fileName.endsWith(".xlsx")
				|| fileName.endsWith(".csv");
	}
}
