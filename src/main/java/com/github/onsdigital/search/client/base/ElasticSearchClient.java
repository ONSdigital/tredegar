package com.github.onsdigital.search.client.base;

import com.google.gson.JsonObject;

/**
 * 
 * Elastic Search client abstraction to make elastic search operations protocol
 * agnostic. Actual clients should be wrapped into concrete classes implementing
 * this interface
 * 
 * @author Bren
 */
public interface ElasticSearchClient {

	/**
	 * 
	 * Closes or returns client to client pool when finished.
	 * 
	 */
	void close();

	SearchBuilder prepareSearch(String index, String query);

	JsonObject execute(SearchBuilder builder) throws Exception;
}
