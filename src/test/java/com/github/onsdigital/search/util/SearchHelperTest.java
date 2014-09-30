package com.github.onsdigital.search.util;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.junit.Assert;

import com.github.onsdigital.bean.SearchResult;
import com.github.onsdigital.search.EmbeddedElasticSearchServer;

public class SearchHelperTest {

	private EmbeddedElasticSearchServer embeddedServer;

	public void startEmbeddedServer() throws ElasticsearchException,
			IOException {
		Settings settings = ImmutableSettings.builder()
				.put("http.enabled", false)
				.put("path.data", "target/elasticsearch-data").build();
		embeddedServer = new EmbeddedElasticSearchServer(settings, "testNode");
		prepareMockData();
	}

	private void prepareMockData() throws ElasticsearchException, IOException {
		embeddedServer
				.getClient()
				.prepareIndex("testindex", "testtype", String.valueOf(1))
				.setSource(
						jsonBuilder().startObject().field("title", "testTitle")
								.field("tags", "taggy", "tennis", "doh")
								.field("theme", "testTheme").endObject()).get();
	}

	public void shutdownEmbeddedServer() {
		embeddedServer.shutdown();
	}

	public void testSearchQuery() throws Exception {
		SearchHelper util = new SearchHelper(embeddedServer.getClient());
		SearchResult result = util.search(new ONSQueryBuilder("testindex")
				.setSearchTerm("do").setFields("tags"));
		Assert.assertEquals(1, result.getNumberOfResults());
	}

}
