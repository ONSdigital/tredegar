package com.github.onsdigital.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Exercises creation of a collection of content types
 */
public class CollectionSearchResultTest {

	@Test
	public void testGetResults() {
		List<File> files = setUpFiles(null);

		CollectionSearchResult collectionSearchResult = new CollectionSearchResult(
				files, 1);

		assertEquals("Collection should have some results", 1, files.size(),
				collectionSearchResult.getNumberOfResults());
		List<Map<String, String>> results = collectionSearchResult.getResults();
		Map<String, String> bulletin = results.get(0);
		assertEquals("title should be available from map",
				"Inflation and Price Indices", bulletin.get("title"));
		assertEquals("release date should be available from map",
				"19 February 2014", bulletin.get("releaseDate"));
		// TODO: this needs looking at - not currently working right
		assertEquals("url should be available from map", "/taxonomy/",
				bulletin.get("url"));
		assertEquals(
				"only one item in list and on page 1, so should be the latest",
				"latest", bulletin.get("indexNumber"));
	}

	@Test
	public void testGetNonLatest() {
		List<File> files = setUpFiles(null);

		CollectionSearchResult collectionSearchResult = new CollectionSearchResult(
				files, 2);
		List<Map<String, String>> results = collectionSearchResult.getResults();
		Map<String, String> bulletin = results.get(0);

		assertNull(
				"only one item in list, but on page 2 so should not be the latest",
				bulletin.get("indexNumber"));
	}

	@Test(expected = Exception.class)
	public void testUnkownFile() {
		File file = new File("unknown.json");
		List<File> files = new ArrayList<File>();
		files.add(file);

		new CollectionSearchResult(files, 1);
	}

	/**
	 * Helps setup the test cases by creating list of files
	 */
	private List<File> setUpFiles(String fileName) {
		File file;
		if (StringUtils.isEmpty(fileName)) {
			file = new File("target/taxonomy/bulletin.json");
		} else {
			file = new File(fileName);
		}

		List<File> files = new ArrayList<File>();
		files.add(file);
		return files;
	}
}
