package com.github.onsdigital.search.util;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.bean.SearchResult;
import com.github.onsdigital.common.ClosedConnectionException;
import com.github.onsdigital.search.client.base.ElasticSearchClient;
import com.github.onsdigital.search.client.base.SearchBuilder;
import com.github.onsdigital.util.ONSQueryBuilder;
import com.github.onsdigital.util.SearchConnectionManager;
import com.google.gson.JsonObject;

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

	private ElasticSearchClient client;

	/**
	 * @param connectionManager
	 *            A {@link SearchConnectionManager} is required to perform
	 *            search operations. Connection must be open to perform any
	 *            search operation
	 */
	public SearchHelper(ElasticSearchClient client) {
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
	 * @throws ClosedConnectionException
	 */
	public SearchResult search(ONSQueryBuilder queryBuilder) throws Exception {
		return new SearchResult(execute(queryBuilder));
	}

	private JsonObject execute(ONSQueryBuilder queryBuilder) throws Exception {
		SearchBuilder searchBuilder = buildRequest(queryBuilder);
		return searchBuilder.execute();
	}

	private SearchBuilder buildRequest(ONSQueryBuilder queryBuilder) {
		SearchBuilder searchBuilder = client.prepareSearch(
				queryBuilder.getIndex(), queryBuilder.buildQuery());

		String type = queryBuilder.getType();
		if (StringUtils.isNotEmpty(type)) {
			searchBuilder.setTypes(type);
		}
		return searchBuilder;
	}

}
