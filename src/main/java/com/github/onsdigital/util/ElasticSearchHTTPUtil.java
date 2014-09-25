package com.github.onsdigital.util;

import io.searchbox.core.Search;
import io.searchbox.params.SearchType;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.bean.SearchResult;
import com.github.onsdigital.common.ClosedConnectionException;

/**
 * 
 * Elastic Search util that uses jest http client for elastic search
 * 
 * @author Bren
 *
 */

public class ElasticSearchHTTPUtil {

	private SearchHTTPConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            A {@link SearchConnectionManager} is required to perform
	 *            search operations. Connection must be open to perform any
	 *            search operation
	 */
	public ElasticSearchHTTPUtil(SearchHTTPConnectionManager connectionManager) {
		this.connectionManager = connectionManager;

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
		testConnection();
		io.searchbox.core.SearchResult searchResult = execute(queryBuilder);
		if (searchResult.isSucceeded() == false) {
			throw new RuntimeException(searchResult.getErrorMessage());
		}
		return new SearchResult(searchResult);
	}

	private io.searchbox.core.SearchResult execute(ONSQueryBuilder queryBuilder)
			throws Exception {
		Search search = buildRequest(queryBuilder);
		io.searchbox.core.SearchResult result = connectionManager.getClient()
				.execute(search);

		return result;

	}

	private Search buildRequest(ONSQueryBuilder queryBuilder) {

		// JsonObject object = new JsonObject();
		// object.addProperty("from", calculateFrom(queryBuilder));
		// object.addProperty("size", queryBuilder.getSize());
		//
		// if (ArrayUtils.isNotEmpty(queryBuilder.getFields())) {
		// JsonObject hihglight = new JsonObject();
		// for (String field : queryBuilder.getFields()) {
		// JsonObject fieldObject = new JsonObject();
		// hihglight.add(field, fieldObject);
		// }
		// object.add("highlight", hihglight);
		// }

		// JsonObject query = new Gson().fromJson(queryBuilder.buildQuery(),
		// JsonObject.class);
		// object.add("query", query);
		//
		Search.Builder builder = new Search.Builder(queryBuilder.buildQuery())
				.addIndex(queryBuilder.getIndex()).setSearchType(
						SearchType.DFS_QUERY_THEN_FETCH);

		String type = queryBuilder.getType();
		if (StringUtils.isNotEmpty(type)) {
			builder.addType(type);
		}
		return builder.build();

	}

	private void testConnection() {
		if (connectionManager.isConnected() == false) {
			throw new ClosedConnectionException("Connection is closed");
		}
	}

	public SearchHTTPConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(
			SearchHTTPConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
