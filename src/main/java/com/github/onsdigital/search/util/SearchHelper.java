package com.github.onsdigital.search.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.search.bean.SearchResult;

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
	public SearchResult search(ONSQueryBuilder queryBuilder) {
		System.out.println("Searcing For:" + ReflectionToStringBuilder.toString(queryBuilder));
		return new SearchResult(execute(queryBuilder));
	}

	private SearchResponse execute(ONSQueryBuilder queryBuilder) {
		SearchRequestBuilder searchBuilder = buildRequest(queryBuilder);
		return searchBuilder.get();
	}

	private SearchRequestBuilder buildRequest(ONSQueryBuilder queryBuilder) {
		SearchRequestBuilder searchBuilder = client.prepareSearch(queryBuilder.getIndex());
		String[] types = queryBuilder.getTypes();
		if (ArrayUtils.isNotEmpty(types)) {
			searchBuilder.setTypes(types);
		} else {
			searchBuilder.setTypes(ContentType.home.name(), ContentType.article.name(), ContentType.bulletin.name(), ContentType.dataset.name(), ContentType.methodology.name());
		}

		searchBuilder.setExtraSource(queryBuilder.buildQuery());
		return searchBuilder;
	}

}
