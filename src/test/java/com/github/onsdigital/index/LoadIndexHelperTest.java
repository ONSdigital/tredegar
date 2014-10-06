package com.github.onsdigital.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class LoadIndexHelperTest {
	private final static String FILE_NAME = "index";
	private final static String FILE_EXTENSION = ".html";
	private final static String DATA_JSON_FILE_NAME = "data.json";
	private final static String CONTENT_TYPE = "bulletins";
	private final static String DELIMITTER = "/";
	private final static String RESOURCE_FILE_PATH = "target/classes/home";
	private final static String TAXONOMY_PATH = "/sample-taxonomy/";
	private final static String PRE_BULLETINS_PATH = RESOURCE_FILE_PATH + TAXONOMY_PATH;
	private final static String CONTENT_TYPE_TEST_FILE = PRE_BULLETINS_PATH + CONTENT_TYPE + DELIMITTER + FILE_NAME + FILE_EXTENSION;
	private final static String HOME_PAGE_TEST_FILE = Paths.get(PRE_BULLETINS_PATH + DATA_JSON_FILE_NAME).toAbsolutePath().toString();
	private final static String CONTENT_TYPE_URL = TAXONOMY_PATH + CONTENT_TYPE + DELIMITTER + FILE_NAME + FILE_EXTENSION;
	private final static String HOME_PAGE_URL = TAXONOMY_PATH;

	@Test
	public void testGetFileNames() throws IOException {
		List<String> fileNames = LoadIndexHelper.getAbsoluteFilePaths(RESOURCE_FILE_PATH);
		System.out.println("Tut siise is: " + fileNames.size());
		assertFalse("Lookup should return some files", fileNames.isEmpty());
	}

	@Test(expected = NoSuchFileException.class)
	public void testWrongPath() throws IOException {
		List<String> fileNames = LoadIndexHelper.getAbsoluteFilePaths("thepath/thesubpath");
		System.out.println("Tut siise is: " + fileNames.size());
		assertFalse("Lookup should return some files", fileNames.isEmpty());
	}

	public void testGetDocumentMapForContentType() throws JsonIOException, JsonSyntaxException, IOException {
		Map<String, String> documentMap = LoadIndexHelper.getDocumentMap(CONTENT_TYPE_TEST_FILE);
		assertEquals("url should math file structure", documentMap.get("url"), CONTENT_TYPE_URL);
		assertEquals("type should be bulletins", documentMap.get("type"), "bulletins");
		assertEquals("title should be data.json", documentMap.get("title"), "index.html");
		assertTrue("tags should contain subdirs", documentMap.get("tags").contains("sample-taxonomy"));
	}

	public void testGetDocumentMapForHomePage() throws JsonIOException, JsonSyntaxException, IOException {
		Map<String, String> documentMap = LoadIndexHelper.getDocumentMap(HOME_PAGE_TEST_FILE);
		assertEquals("url should math file structure", documentMap.get("url"), HOME_PAGE_URL);
		assertEquals("type should be bulletins", documentMap.get("type"), "home");
		assertEquals("title should be uksectoraccounts", documentMap.get("title"), "Government, Public Sector and Taxes");
		assertFalse("tags should not contain content type subdir", documentMap.get("tags").contains("CONTENT_TYPE"));
	}

}
