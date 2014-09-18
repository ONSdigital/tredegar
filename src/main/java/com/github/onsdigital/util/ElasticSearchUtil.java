package com.github.onsdigital.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.github.onsdigital.common.ClosedConnectionException;

/**
 * 
 * <p>
 * {@link ElasticSearchUtil} connects to an elastic search cluster and perform
 * search & read operations under indexes or types.
 * </p>
 * 
 * <p>
 * This class does not create an embedded non-data node. It simply creates a
 * transport client that which sniffs available nodes through connected node. It
 * is lightweight and still highly available
 * </p>
 * 
 * <p>
 * See more at
 * http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api
 * /current/client.html <a> </a>
 * </p>
 * 
 * @author boorhun
 *
 */

public class ElasticSearchUtil {

	private SearchConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            A {@link SearchConnectionManager} is required to perform
	 *            search operations. Connection must be open to perform any
	 *            search operation
	 */
	public ElasticSearchUtil(SearchConnectionManager connectionManager) {
		this.connectionManager = connectionManager;

	}

	/**
	 * 
	 * Performs the search and returns documents as a list of maps that contains
	 * key-value pairs
	 * 
	 * @param queryBuilder
	 * @return documents as a list of maps that contains key-value pairs
	 * @throws ClosedConnectionException
	 */
	public List<Map<String, Object>> search(ONSQueryBuilder queryBuilder) {
		testConnection();
		SearchResponse searchResponse = execute(queryBuilder);
		return buildList(searchResponse);
	}

	private SearchResponse execute(ONSQueryBuilder queryBuilder) {
		BaseQueryBuilder builder = buildSearch(queryBuilder);
		SearchRequestBuilder requestBuilder = buildRequest(queryBuilder,
				builder);
		return requestBuilder.get();
	}

	private SearchRequestBuilder buildRequest(ONSQueryBuilder queryBuilder,
			BaseQueryBuilder builder) {
		SearchRequestBuilder requestBuilder = connectionManager.getClient()
				.prepareSearch(queryBuilder.getIndex())
				.setFrom(queryBuilder.getFrom())
				.setSize(queryBuilder.getSize())
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(builder.buildAsBytes());

		String type = queryBuilder.getType();
		if (StringUtils.isNotEmpty(type)) {
			requestBuilder.setTypes(type);
		}
		return requestBuilder;
	}

	private BaseQueryBuilder buildSearch(ONSQueryBuilder queryBuilder) {
		// Return all documents
		if (StringUtils.isEmpty(queryBuilder.query)) {
			return new MatchAllQueryBuilder();
		}
		// return documents with fields containing words that start with given
		// query
		return new MultiMatchQueryBuilder(queryBuilder.getQuery(),
				queryBuilder.getFields())
				.type(MatchQueryBuilder.Type.PHRASE_PREFIX);
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

	private void testConnection() {
		if (connectionManager.isConnected() == false) {
			throw new ClosedConnectionException("Connection is closed");
		}
	}

	public SearchConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(SearchConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
