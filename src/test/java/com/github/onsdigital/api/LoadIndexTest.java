package com.github.onsdigital.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.onsdigital.index.LoadIndexHelper;

/**
 * Temporarily commenting out integration tests with elasticsearch as the travis
 * build fails to delete deat directory?
 * 
 * @author daviesa
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LoadIndexHelper.class)
public class LoadIndexTest {
	@Mock
	HttpServletRequest httpServletRequest;
	@Mock
	HttpServletResponse httpServletResponse;

	/**
	 * This is more of an integration test as it exercises the collaboration of
	 * other classes to scan the file system and generate a real ElasticSearch
	 * indexÏ
	 * 
	 * @throws Exception
	 *             if anything unexpected happens
	 */
	// @Test
	// public void testGet() throws Exception {
	// assertEquals("Should get success message", "LoadIndex succeeded", new
	// LoadIndex().get(httpServletRequest, httpServletResponse));
	//
	// GetResponse esGetResponse =
	// ElasticSearchServer.getClient().prepareGet("ons", "home",
	// "1").execute().actionGet();
	// assertEquals("Index defined as ons", "ons", esGetResponse.getIndex());
	//
	// Map<String, Object> documentMap = esGetResponse.getSource();
	// assertFalse("Source should be populated", documentMap.isEmpty());
	// assertNotNull("Index type should be populated", esGetResponse.getType());
	// esGetResponse.getHeaders();
	//
	// assertNotNull("url should be populated", documentMap.get("url"));
	// assertNotNull("title should be populated", documentMap.get("title"));
	// assertNotNull("path should be populated", documentMap.get("path"));
	// }

	/**
	 * Mock out any collaborator calls and fire an exception when scanning the
	 * file system returns no results
	 */
	@Test
	public void testNoSystemFilesFound() throws IOException {
		PowerMockito.mockStatic(LoadIndexHelper.class);
		Mockito.when(LoadIndexHelper.getAbsoluteFilePaths("target/classes/home")).thenReturn(Collections.<String> emptyList());
		assertEquals("Should get failure message", "LoadIndex failed", new LoadIndex().get(httpServletRequest, httpServletResponse));
	}

	/**
	 * Mock out any collaborator calls and fire an exception when looking for
	 * fields from the filename that we want to index
	 */
	// @Test
	// public void testNoFieldsFound() throws IOException {
	// PowerMockito.mockStatic(LoadIndexHelper.class);
	// List<String> testFileNames = new ArrayList<String>();
	// testFileNames.add("anyFileName");
	// Mockito.when(LoadIndexHelper.getAbsoluteFilePaths()).thenReturn(testFileNames);
	// Mockito.when(LoadIndexHelper.getDocumentMap(Mockito.anyString())).thenReturn(null);
	// assertEquals("Should get failure message", "LoadIndex failed", new
	// LoadIndex().get(httpServletRequest, httpServletResponse));
	// }
}
