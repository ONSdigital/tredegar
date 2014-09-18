package com.github.onsdigital.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for loading index into search engine
 */
public class LoadIndexHelper {
	/** the string to start tokenizing from */
	private static final String TAXONOMY = "taxonomy";
	/** The folder to start loading from */
	private static final String ROOT_SEARCH = "src/main/resources/files/" + TAXONOMY;

	/**
	 * Finds the place to start splitting tokens
	 * 
	 * @param fileName
	 *            the absolute path to file
	 * @return string tokens representing values to be passed to search engine
	 */
	public static String[] getTaxonomy(String fileName) {
		String[] values = fileName.split(TAXONOMY);
		String indexArgs = values[1];
		String[] splitIndexArgs = indexArgs.split("/");
		return splitIndexArgs;
	}

	/**
	 * Loads up the file names from a system scan
	 * 
	 * @return list of strings representing files
	 * @throws IOException
	 *             if any file io operations failed
	 */
	public static List<String> getFileNames() throws IOException {
		List<String> fileNames = new ArrayList<String>();
		final Path rootDir = Paths.get(ROOT_SEARCH);
		fileNames = ScanFileSystem.getFileNames(fileNames, rootDir);
		return fileNames;
	}
}
