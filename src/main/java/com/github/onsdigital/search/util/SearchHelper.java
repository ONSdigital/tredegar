package com.github.onsdigital.search.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import com.github.onsdigital.bean.SearchResult;

/**
 * 
 * <p>
 * {@link SearchHelper} connects to an elastic search cluster and perform search
 * operation under indexes or types.
 * </p>
 * 
 * @author Bren
 *
 */

public class SearchHelper {

	private Client client;

	/**
	 * @param connectionManager
	 *            A {@link Client} is required to perform search operations.
	 * 
	 */
	public SearchHelper(Client client) {
		this.client = client;
	}

	/**
	 * 
	 * Performs the search and returns documents as a list of maps that contains
	 * key-value pairs
	 * 
	 * @param queryBuilder
	 * @return {@link SearchResult}
	 * @throws Exception
	 */
	public SearchResult search(ONSQueryBuilder queryBuilder) throws Exception {
		System.out.println("Searcing For:"
				+ ReflectionToStringBuilder.toString(queryBuilder));
		return new SearchResult(execute(queryBuilder));
	}

	private SearchResponse execute(ONSQueryBuilder queryBuilder)
			throws Exception {
		SearchRequestBuilder searchBuilder = buildRequest(queryBuilder);
		return searchBuilder.get();
	}

	private SearchRequestBuilder buildRequest(ONSQueryBuilder queryBuilder) {
		SearchRequestBuilder searchBuilder = client.prepareSearch(queryBuilder
				.getIndex());

		searchBuilder.setExtraSource(queryBuilder.buildQuery());

		String type = queryBuilder.getType();
		if (StringUtils.isNotEmpty(type)) {
			searchBuilder.setTypes(type);
		}
		return searchBuilder;
	}

}
