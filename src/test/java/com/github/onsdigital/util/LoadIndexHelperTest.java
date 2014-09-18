package com.github.onsdigital.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class LoadIndexHelperTest {
	private final String TEST_FILE = "src/main/resources/taxonomy/data/timeseries/timeseries-test.txt";

	@Test
	public void testGetTaxonomy() {
		String[] result = LoadIndexHelper.getTaxonomy(TEST_FILE);
		assertTrue("Tokenize file path should return tokens", result.length > 0);
	}

	@Test
	public void testGetFileNames() throws IOException {
		List<String> fileNames = LoadIndexHelper.getFileNames();
		assertFalse("Lookup should return some files", fileNames.isEmpty());
	}
}
