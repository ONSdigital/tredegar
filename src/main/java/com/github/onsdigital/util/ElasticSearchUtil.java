package com.github.onsdigital.util;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;

import com.github.onsdigital.bean.SearchResult;
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
	 * @return {@link SearchResult}
	 * @throws ClosedConnectionException
	 */
	public SearchResult search(ONSQueryBuilder queryBuilder) {
		testConnection();
		SearchResponse searchResponse = execute(queryBuilder);
		return new SearchResult(searchResponse);
	}

	private SearchResponse execute(ONSQueryBuilder queryBuilder) {
		BaseQueryBuilder builder = buildSearch(queryBuilder);
		SearchRequestBuilder requestBuilder = buildRequest(queryBuilder,
				builder);
		return requestBuilder.execute().actionGet();
	}

	private SearchRequestBuilder buildRequest(ONSQueryBuilder queryBuilder,
			BaseQueryBuilder builder) {
		SearchRequestBuilder requestBuilder = connectionManager.getClient()
				.prepareSearch(queryBuilder.getIndex())
				.setFrom(calculateFrom(queryBuilder))
				.setSize(queryBuilder.getSize())
				.setQuery(builder.buildAsBytes()).setHighlighterPreTags("<strong>")
				.setHighlighterPostTags("</strong>").setHighlighterNumOfFragments(0);

		for (String field : queryBuilder.getFields()) {
			requestBuilder.addHighlightedField(field);
		}

		String type = queryBuilder.getType();
		if (StringUtils.isNotEmpty(type)) {
			requestBuilder.setTypes(type);
		}
		return requestBuilder;
	}

	private int calculateFrom(ONSQueryBuilder builder) {
		return builder.getSize() * (builder.getPage() - 1);
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
