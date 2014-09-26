package com.github.onsdigital.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper methods for loading index into search engine
 */
public class LoadIndexHelper {
	private static final String TAGS = "tags";
	private static final String TITLE = "title";
	private static final String TYPE = "type";
	private static final String URL = "url";
	private static final String HOME = "home";
	private static final String DATASETS = "datasets";
	private static final String METHODOLOGY = "methodology";
	private static final String ARTICLES = "articles";
	private static final String BULLETINS = "bulletins";
	private static final String DELIMITTER = "/";
	private static final String ROOT_SEARCH = "target/classes/files/";

	/**
	 * Loads up the file names from a system scan
	 * 
	 * @return list of strings representing files
	 * @throws IOException
	 *             if any file io operations failed
	 */
	public static List<String> getAbsoluteFilePaths() throws IOException {
		List<String> fileNames = new ArrayList<String>();

		final Path rootDir = Paths.get(ROOT_SEARCH);
		fileNames = ScanFileSystem.getFileNames(fileNames, rootDir);
		return fileNames;
	}

	/**
	 * Builds up a map that represents the data structure for indexing
	 * 
	 * @param absoluteFilePath
	 *            the complete path and filename
	 * @return the collection of key value pairs representing an indexable item
	 */
	public static Map<String, String> getDocumentMap(String absoluteFilePath) {
		String[] pathAfterTaxonomy = absoluteFilePath.split("files");
		String url = pathAfterTaxonomy[1];

		String[] splitPath = url.split(DELIMITTER);
		int splitPathLength = splitPath.length;
		int typeTokenIndex = splitPathLength - 2;

		String type = getType(splitPath, typeTokenIndex);

		int fileNameTokenIndex = splitPathLength - 1;
		String title = splitPath[fileNameTokenIndex];

		Map<String, String> documentMap = buildDocumentMap(url, splitPath, type, title);
		return documentMap;
	}

	private static Map<String, String> buildDocumentMap(String url, String[] splitPath, String type, String title) {

		Map<String, String> documentMap = new HashMap<String, String>();
		documentMap.put(URL, url);
		documentMap.put(TYPE, type);
		documentMap.put(TITLE, title);
		documentMap.put(TAGS, Arrays.toString(splitPath));
		return documentMap;
	}

	private static String getType(String[] splitPath, int typeTokenIndex) {

		String type = splitPath[typeTokenIndex];
		if (!type.equals(BULLETINS) && !type.equals(ARTICLES)
				&& !type.equals(METHODOLOGY) && !type.equals(DATASETS)) {
			type = HOME;
		}
		return type;
	}
}
