package com.github.onsdigital.search;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.json.ContentType;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Helper methods for loading index into search engine
 */
public class LoadIndexHelper {
	private static final String NAME = "name";
	private static final String CDID = "cdid";
	 private static final String TAGS = "tags";
	private static final String TITLE = "title";
	private static final String TYPE = "type";
	private static final String LEDE = "lede";
	private static final String URL = "url";
	private static final String DELIMITTER = "/";
	private static final String SUMMARY = "summary";

	/**
	 * Loads up the file names from a system scan
	 * 
	 * @return list of strings representing files
	 * @throws IOException
	 *             if any file io operations failed
	 */
	public static List<String> getAbsoluteFilePaths(String path) throws IOException {
		List<String> fileNames = new ArrayList<String>();
		final Path rootDir = Paths.get(path);
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
		String url = absoluteFilePath.substring(absoluteFilePath.indexOf(Configuration.getTaxonomyPath()) + Configuration.getTaxonomyPath().length());
		String[] splitPath = url.split(DELIMITTER);

		List<String> splitPathAsList = new ArrayList<String>(Arrays.asList(splitPath));
		// remove first index which is just a space
		splitPathAsList.remove(0);
		// remove last index which is the data.json
		splitPathAsList.remove(splitPathAsList.size() - 1);

		JsonObject jsonObject = getJsonObject(absoluteFilePath);
		String type = getField(jsonObject, TYPE);

		Map<String, String> documentMap = null;
		ContentType contentType = ContentType.valueOf(type);
		String splitUrl = url.substring(0, url.indexOf("data.json"));
		String title = getField(jsonObject, TITLE);
		String summary = getField(jsonObject, SUMMARY);
		String name = getField(jsonObject, NAME);
		String lede = getField(jsonObject, LEDE);
		switch (contentType) {
		case home:
			documentMap = buildDocumentMap(splitUrl, splitPathAsList, type, name, lede, summary);
			break;
		case timeseries:
			String cdid = getField(jsonObject, CDID);
			documentMap = buildTimeseriesMap(splitUrl, splitPathAsList, type, name, cdid);
			break;
		case unknown:
			System.out.println("json file: " + absoluteFilePath + "has unknown content type: " + contentType);
			break;
		default:
			documentMap = buildDocumentMap(splitUrl, splitPathAsList, type, title, lede, summary);
			break;
		}

		return documentMap;
	}

	private static Map<String, String> buildDocumentMap(String url, List<String> pathTokens, String type, String title, String lede, String summary) {

		Map<String, String> documentMap = new HashMap<String, String>();
		documentMap.put(URL, url);
		documentMap.put(TYPE, type);
		documentMap.put(TITLE, title);
		documentMap.put(TAGS, pathTokens.toString());
		documentMap.put(LEDE, lede);
		documentMap.put(SUMMARY, summary);
		return documentMap;
	}

	private static Map<String, String> buildTimeseriesMap(String url, List<String> pathTokens, String type, String title, String cdid) {

		Map<String, String> documentMap = new HashMap<String, String>();
		documentMap.put(URL, url);
		documentMap.put(TYPE, type);
		documentMap.put(TITLE, title);
		documentMap.put(TAGS, pathTokens.toString());
		documentMap.put(CDID, cdid);
		return documentMap;
	}

	private static JsonObject getJsonObject(String absoluteFilePath) {
		JsonObject jsonObject;
		try {
			jsonObject = new JsonParser().parse(FileUtils.readFileToString(new File(absoluteFilePath), Charset.forName("UTF-8"))).getAsJsonObject();
		} catch (JsonSyntaxException | IOException e) {
			throw new RuntimeException("Failed to parse json: " + absoluteFilePath, e);
		}
		return jsonObject;
	}

	private static String getField(JsonObject jsonObject, String field) {
		if (StringUtils.isEmpty(field)) {
			throw new IllegalArgumentException("Field cannot be null");
		}

		JsonElement jsonElement = jsonObject.get(field);
		if (jsonElement == null) {
			return ContentType.unknown.name();
		}

		return jsonElement.getAsString();
	}
}
