package com.github.onsdigital.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class LoadIndexHelperTest {
	private final static String RESOURCE_FILE_PATH = "target/taxonomy";

	@Test
	public void testGetFileNames() throws IOException {
		List<String> fileNames = LoadIndexHelper.getAbsoluteFilePaths(RESOURCE_FILE_PATH);
		assertFalse("Lookup should return some files", fileNames.isEmpty());
	}

	@Test(expected = NoSuchFileException.class)
	public void testWrongPath() throws IOException {
		List<String> fileNames = LoadIndexHelper.getAbsoluteFilePaths("thepath/thesubpath");
		System.out.println("Tut siise is: " + fileNames.size());
		assertFalse("Lookup should return some files", fileNames.isEmpty());
	}

	@Test
	public void testGetDocumentMapForContentType() throws JsonIOException, JsonSyntaxException, IOException {
		Map<String, String> documentMap = LoadIndexHelper.getDocumentMap("target/taxonomy/economy/inflationandpriceindices/bulletins/consumerpriceinflation/data.json");
		assertEquals("url should math file structure", "/economy/inflationandpriceindices/bulletins/consumerpriceinflation/", documentMap.get("url"));
		assertEquals("type should be bulletins", "bulletin", documentMap.get("type"));
		assertEquals("title should be data.json", "Consumer Price Inflation", documentMap.get("title"));
		assertTrue("tags should contain subdirs", documentMap.get("tags").contains("inflation"));
	}

	@Test
	public void testGetDocumentMapForHomePage() throws JsonIOException, JsonSyntaxException, IOException {
		Map<String, String> documentMap = LoadIndexHelper.getDocumentMap("target/taxonomy/economy/data.json");
		assertEquals("url should math file structure", "/economy/", documentMap.get("url"));
		assertEquals("type should be bulletins", "home", documentMap.get("type"));
		assertEquals("title should be uksectoraccounts", "Economy", documentMap.get("title"));
		assertFalse("tags should not contain content type subdir", documentMap.get("tags").contains("CONTENT_TYPE"));
	}

}
