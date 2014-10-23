package com.github.onsdigital.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.json.CollectionItem;

/**
 * Holds the details for a collection of content types
 */
public class CollectionSearchResult {
	private static final String INDEX_VALUE = "latest";
	private static final String INDEX_KEY = "indexNumber";
	private static final String RELEASE_DATE = "releaseDate";
	private static final String URL = "url";
	private static final String DATA_JSON = "data.json";
	private static final String PATH_ROOT = "target";
	private static final String TITLE = "title";
	private long numberOfResults;
	private List<Map<String, String>> results;
	private int page;

	/**
	 * ctor that parses the files and stores information about these files into
	 * a map
	 * 
	 * @param files
	 *            collection of files representing the content types
	 * @param page
	 *            indicates which page number, needed to help identify latest
	 *            bulletin
	 */
	public CollectionSearchResult(List<File> files, int page) {
		results = new ArrayList<Map<String, String>>();
		this.numberOfResults = files.size();
		this.page = page;
		init(files);
	}

	/**
	 * @return number of files found during the search
	 */
	public long getNumberOfResults() {
		return numberOfResults;
	}

	/**
	 * @param numberOfHits
	 *            number of files stored
	 */
	public void setNumberOfResults(long numberOfHits) {
		this.numberOfResults = numberOfHits;
	}

	/**
	 * @return collection of key,value pairs that detail a content type
	 */
	public List<Map<String, String>> getResults() {
		return results;
	}

	private void init(List<File> files) {
		for (File file : files) {
			Map<String, String> item = new HashMap<String, String>();
			CollectionItem collectionItemJson = getCollectionItem(file);
			item.put(TITLE, collectionItemJson.title);
			item.put(RELEASE_DATE, collectionItemJson.releaseDate);

			String[] urlWithoutJson = getUrl(file);
			item.put(URL, urlWithoutJson[0]);

			if ((page == 1) && (files.indexOf(file) == 0)) {
				item.put(INDEX_KEY, INDEX_VALUE);
			}

			results.add(item);
		}
	}

	private String[] getUrl(File file) {
		String[] url = file.getPath().split(PATH_ROOT);
		String[] urlWithoutJson = url[1].split(DATA_JSON);
		return urlWithoutJson;
	}

	private CollectionItem getCollectionItem(File file) {
		try {
			return Serialiser.deserialise(new FileInputStream(file), CollectionItem.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
