package com.github.onsdigital.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.github.davidcarboni.ResourceUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
	private static final String DATA_JSON_FILENAME = "data.json";

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
	 * @throws IOException
	 * @throws JsonSyntaxException
	 * @throws JsonIOException
	 */
	public static Map<String, String> getDocumentMap(String absoluteFilePath) throws JsonIOException, JsonSyntaxException, IOException {
		String[] pathAfterTaxonomy = absoluteFilePath.split("files");
		String url = pathAfterTaxonomy[1];

		String[] splitPath = url.split(DELIMITTER);
		int splitPathLength = splitPath.length;
		int folderNameTokenIndex = splitPathLength - 2;
		int fileNameTokenIndex = splitPathLength - 1;

		String type = getType(splitPath, folderNameTokenIndex);
		String fileName = splitPath[fileNameTokenIndex];

		Map<String, String> documentMap = null;
		if (HOME.equals(type)) {
			if (DATA_JSON_FILENAME.equals(fileName)) {
				// Read title from data.json
				String title = getTitle(absoluteFilePath);
				// Exclude data.json from url
				url = url.substring(0, url.indexOf(fileName));
				documentMap = buildDocumentMap(url, splitPath, type, title);
			} else {
				// Skip files other than data.json under home directories
			}
		} else {
			documentMap = buildDocumentMap(url, splitPath, type, fileName);
		}
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
		if (!type.equals(BULLETINS) && !type.equals(ARTICLES) && !type.equals(METHODOLOGY) && !type.equals(DATASETS)) {
			type = HOME;
		}
		return type;
	}

	private static String getTitle(String filePath) throws IOException {
		return new JsonParser().parse(FileUtils.readFileToString(new File(filePath))).getAsJsonObject().get("name").getAsString();
	}
}
