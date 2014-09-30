package com.github.onsdigital.search.client.base;

import org.elasticsearch.client.Client;

import com.github.onsdigital.search.util.TransportSearchBuilder;
import com.google.gson.JsonObject;

/**
 * 
 * Singleton wrapper class for Elastic Search official TCP and UDP based clients
 * implementing {@link Client}.
 * 
 * @author Bren
 *
 */

public abstract class AbstractElasticSearchClient implements
		ElasticSearchClient {

	protected static Client client;

	protected AbstractElasticSearchClient(Client client) {
		AbstractElasticSearchClient.client = client;
	}

	@Override
	public void close() {
		if (client != null) {
			// Returns to pool internally if any configured
			client.close();
		} else {
			System.out.println("Warning! Attempted closing null client");
		}
	}

	@Override
	public SearchBuilder prepareSearch(String index, String query) {
		return new TransportSearchBuilder(client, query).setIndices(index);
	}

	@Override
	public JsonObject execute(SearchBuilder builder) throws Exception {
		return builder.execute();
	}
	
}
