package com.github.onsdigital.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class LoadIndexHelperTest {
	private final static String FILE_NAME = "index";
	private final static String FILE_EXTENSION = ".html";
	private final static String CONTENT_TYPE = "bulletins";
	private final static String DELIMITTER = "/";
	private final static String PRE_BULLETINS_PATH = "/src/main/resources/files/taxonomy/economy/nationalaccounts/uksectoraccounts/";
	private final static String CONTENT_TYPE_TEST_FILE = PRE_BULLETINS_PATH + CONTENT_TYPE + DELIMITTER + FILE_NAME
			+ FILE_EXTENSION;
	private final static String HOME_PAGE_TEST_FILE = PRE_BULLETINS_PATH + FILE_NAME + FILE_EXTENSION;

	@Test
	public void testGetFileNames() throws IOException {
		List<String> fileNames = LoadIndexHelper.getAbsoluteFilePaths();
		assertFalse("Lookup should return some files", fileNames.isEmpty());
	}

	@Test
	public void testGetDocumentMapForContentType() {
		Map<String, String> documentMap = LoadIndexHelper.getDocumentMap(CONTENT_TYPE_TEST_FILE);
		assertEquals("url should math file structure", documentMap.get("url"), CONTENT_TYPE_TEST_FILE);
		assertEquals("type should be bulletins", documentMap.get("type"), "bulletins");
		assertEquals("title should be index.html", documentMap.get("title"), "index.html");
		assertTrue("tags should contain subdirs", documentMap.get("tags").contains("nationalaccounts"));
	}

	@Test
	public void testGetDocumentMapForHomePage() {
		Map<String, String> documentMap = LoadIndexHelper.getDocumentMap(HOME_PAGE_TEST_FILE);
		assertEquals("url should math file structure", documentMap.get("url"), HOME_PAGE_TEST_FILE);
		assertEquals("type should be bulletins", documentMap.get("type"), "home");
		assertEquals("title should be index.html", documentMap.get("title"), "index.html");
		assertFalse("tags should not contain content type subdir", documentMap.get("tags").contains("CONTENT_TYPE"));
	}
}
