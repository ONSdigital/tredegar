package com.github.onsdigital.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class SearchUtil {

	final static String INDEX = "publication";
	final static String TYPE = "bulletin";
	final static String TITLE = "title";
	final static String TAGS = "tags";
	final static String THEME = "theme";

	public static List<Map<String, Object>> search(String query) {
		Client client = null;
		try {

			Settings settings = ImmutableSettings.settingsBuilder()
					.put("cluster.name", "elasticsearch").build();
			client = new TransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							"localhost", 9300));

			BaseQueryBuilder queryBuilder = buildQuery(query);

			SearchResponse searchResponse = searchQuery(client, queryBuilder);

			return buildList(searchResponse);

		} finally {
			close(client);
		}

	}

	private static SearchResponse searchQuery(Client client,
			BaseQueryBuilder queryBuilder) {
		SearchResponse searchResponse = client.prepareSearch(INDEX)
				.setTypes(TYPE).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(queryBuilder.buildAsBytes()).get();
		return searchResponse;
	}

	public static BaseQueryBuilder buildQuery(String query) {
		BaseQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query,
				TITLE, TAGS, THEME);
		return queryBuilder;
	}

	private static void close(Client client) {
		if (client != null) {
			client.close();
		}
	}

	private static List<Map<String, Object>> buildList(SearchResponse response) {
		// list of hits
		List<Map<String, Object>> results = new ArrayList<>();
		SearchHit hit;

		Iterator<SearchHit> iterator = response.getHits().iterator();
		while (iterator.hasNext()) {
			hit = iterator.next();
			results.add(hit.getSource());
		}

		return results;
	}
}
