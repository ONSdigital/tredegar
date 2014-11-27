//package com.github.onsdigital.api;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.elasticsearch.action.count.CountResponse;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import com.github.onsdigital.search.ElasticSearchServer;
//import com.github.onsdigital.search.bean.SearchResult;

/**
 * Due to intermittent failure in the elasticsearch server to initiate before
 * test is executed, this test has been commented out. However, it can be tested
 * out locally to ensure correct behaviour when loading up the embedded elastic
 * search server and performing various searches.
 */
//public class LoadIndexAndSearchTest {
//	HttpServletRequest httpServletRequest;
//	HttpServletResponse httpServletResponse;
//	
//	@Before
//	public void setUp() throws Exception {
//		// create the Embedded ElasticSearch server and load the index
//		new LoadIndex().get(httpServletRequest, httpServletResponse);
//		httpServletRequest = Mockito.mock(HttpServletRequest.class);
//		httpServletResponse = Mockito.mock(HttpServletResponse.class);
//	}
//
//	/**
//	 * This is more of an integration test as it exercises the collaboration of
//	 * other classes to scan the file system and generate a real ElasticSearch
//	 * indexÏ
//	 * 
//	 * @throws Exception
//	 *             if anything unexpected happens
//	 */
//	@Test
//	public void testGet() throws Exception {
//		CountResponse countResponse = ElasticSearchServer.getClient().prepareCount("ons").execute().actionGet();
//		assertTrue("Index ons created", countResponse.getCount() > 0);
//	}
//
//	@Test
//	public void testSearch() throws Exception {
//		Search search = new Search();
//		Mockito.when(httpServletRequest.getParameter("q")).thenReturn("inflation");
//		SearchResult searchResult = (SearchResult) search.get(httpServletRequest, httpServletResponse);
//		assertFalse("Should have found something about inflation", searchResult.getResults().isEmpty());
//	}
//
//	@Test
//	public void testSearchTimeseries() throws Exception {
//		Search search = new Search();
//		Mockito.when(httpServletRequest.getParameter("q")).thenReturn("mgsx");
//		SearchResult searchResult = (SearchResult) search.get(httpServletRequest, httpServletResponse);
//		assertFalse("Should have found something about cdid for msgx", searchResult.getResults().isEmpty());
//	}
//
//	@Test
//	public void testSearchNoResults() throws Exception {
//		Search search = new Search();
//		Mockito.when(httpServletRequest.getParameter("q")).thenReturn("rubbish");
//		SearchResult searchResult = (SearchResult) search.get(httpServletRequest, httpServletResponse);
//		assertTrue("Should not have found anything", searchResult.getResults().isEmpty());
//	}
//
//	/**
//	 * If search fails for core content types and timeseries then search should
//	 * perform a 'suggestion' or 'fuzzy' based search that attempts a certain
//	 * amount of autocorrect
//	 */
//	@Test
//	public void testSearchSuggestions() throws Exception {
//		Search search = new Search();
//		// should definately get something about consumer price index, so will
//		// attempt some variations
//		Mockito.when(httpServletRequest.getParameter("q")).thenReturn("consimer");
//		SearchResult searchResultConsimer = (SearchResult) search.get(httpServletRequest, httpServletResponse);
//		assertFalse("Should have found a suggestion for consumer", searchResultConsimer.getResults().isEmpty());
//
//		Mockito.when(httpServletRequest.getParameter("q")).thenReturn("consimer proce");
//		SearchResult searchResultConsimerProce = (SearchResult) search.get(httpServletRequest, httpServletResponse);
//		assertFalse("Should have found a suggestion for consumer", searchResultConsimerProce.getResults().isEmpty());
//
//		Mockito.when(httpServletRequest.getParameter("q")).thenReturn("consimer proce indux");
//		SearchResult searchResultConsimerProceIndux = (SearchResult) search.get(httpServletRequest, httpServletResponse);
//		assertFalse("Should have found a suggestion for consumer", searchResultConsimerProceIndux.getResults().isEmpty());
//	}
// }
