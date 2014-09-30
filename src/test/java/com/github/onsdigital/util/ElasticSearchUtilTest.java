package com.github.onsdigital.util;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.onsdigital.search.EmbeddedElasticSearchServer;

public class ElasticSearchUtilTest {

	private EmbeddedElasticSearchServer embeddedServer;

	@Before
	public void startEmbeddedServer() throws ElasticsearchException,
			IOException {
		embeddedServer = new EmbeddedElasticSearchServer("testNode");
		prepareMockData();
	}

	private void prepareMockData() throws ElasticsearchException, IOException {
		embeddedServer
				.getClient()
				.prepareIndex("testindex", "testtype", String.valueOf(1))
				.setSource(
						jsonBuilder().startObject().field("title", "testTitle")
								.field("tags", "taggy", "tennis", "doh")
								.field("theme", "testTheme").endObject())
				.execute().actionGet();
	}

	@After
	public void shutdownEmbeddedServer() {
		embeddedServer.shutdown();
	}

	@Test
	public void testSearchQuery() {

		// SearchHelper util = null;
		try {
			// util = openConnection();
			// List<Map<String, Object>> results = util
			// .search(new ONSQueryBuilder("testindex").setQuery("do"));
			// Assert.assertEquals(results.size(), 1);
		} finally {
			// closeConnection(util);
		}
	}

}
