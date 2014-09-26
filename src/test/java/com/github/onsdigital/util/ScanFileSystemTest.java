package com.github.onsdigital.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * Exercises scanning the file system
 */
public class ScanFileSystemTest {
	private String rootSearch = "target/classes";

	@Test
	public void testGetFileNames() throws IOException {
		final Path rootDir = Paths.get(rootSearch);

		List<String> fileNames = new ArrayList<String>();
		ScanFileSystem.getFileNames(fileNames, rootDir);
		assertFalse(fileNames.isEmpty());

		for (String fileName : fileNames) {
			assertTrue("File extension must be one of small subset of types, e.g. html but not css",
					(fileName.endsWith(".html") || fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName
							.endsWith(".csv")));
			assertTrue(
					"File path must be one of small subset of content type paths, e.g. economy but not ui",
					(fileName.contains("target/classes/files/businessindustryandtrade")
							|| fileName.contains("target/classes/files/economy")
							|| fileName.contains("target/classes/files/employmentandlabourmarket") || fileName
							.contains("target/classes/file/peoplepopulationandcommunity")));
		}
	}

	@Test(expected = IOException.class)
	public void testUnknownRootDir() throws Exception {
		String unknownRoot = "unknown/unknown/unknown";
		final Path unknownRootDir = Paths.get(unknownRoot);
		List<String> fileNames = new ArrayList<String>();
		ScanFileSystem.getFileNames(fileNames, unknownRootDir);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullFileNames() throws Exception {
		final Path unknownRootDir = Paths.get(rootSearch);
		ScanFileSystem.getFileNames(null, unknownRootDir);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullPathDir() throws Exception {
		List<String> fileNames = new ArrayList<String>();
		ScanFileSystem.getFileNames(fileNames, null);
	}
}
