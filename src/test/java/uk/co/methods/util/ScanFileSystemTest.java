package uk.co.methods.util;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.onsdigital.util.ScanFileSystem;

/**
 * Exercises scanning the file system
 */
public class ScanFileSystemTest {
	private String rootSearch = "src/main/resources";
	@Test
	public void testGetFileNames() throws IOException {
		ScanFileSystem scanFileSystem = new ScanFileSystem();
		final Path rootDir = Paths.get(rootSearch);

		List<String> fileNames = new ArrayList<String>();
		scanFileSystem.getFileNames(fileNames, rootDir);
		assertFalse(fileNames.isEmpty());
	}

	@Test(expected = IOException.class)
	public void testUnknownRootDir() throws Exception {
		ScanFileSystem scanFileSystem = new ScanFileSystem();

		String unknownRoot = "unknown/unknown/unknown";
		final Path unknownRootDir = Paths.get(unknownRoot);
		List<String> fileNames = new ArrayList<String>();
		scanFileSystem.getFileNames(fileNames, unknownRootDir);
	}
}
