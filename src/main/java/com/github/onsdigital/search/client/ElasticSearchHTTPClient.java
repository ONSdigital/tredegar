package com.github.onsdigital.search.client;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import com.github.onsdigital.search.client.base.ElasticSearchClient;
import com.github.onsdigital.search.client.base.SearchBuilder;
import com.github.onsdigital.search.util.HTTPSearchBuilder;
import com.google.gson.JsonObject;

/**
 * 
 * Singleton wrapper class for Elastic Search {@link JestClient} for elastic
 * search operations through HTTP. Initialized using information provided
 * throught environment variables
 * 
 * @author Bren
 *
 */

public class ElasticSearchHTTPClient implements ElasticSearchClient {

	private static JestClient client;
	private static ElasticSearchClient instance;
	final static String BONSAI_URL = System.getenv("BONSAI_URL");

	private ElasticSearchHTTPClient(String url) {
		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(url)
				.multiThreaded(true).build());
		client = factory.getObject();
	}

	public static ElasticSearchClient getClient() {
		if (instance == null) {
			synchronized (ElasticSearchHTTPClient.class) {
				if (instance == null) {
					instance = new ElasticSearchHTTPClient(BONSAI_URL);
				}
			}
		}
		return instance;
	}

	@Override
	public void close() {
		if (client != null) {
			// client.shutdownClient();

			// TODO: Jest client uses Apache MINA internally to handle
			// connections. Even thought it uses a connection pool. There is no
			// close method to return connection to the pool. It only shuts down
			// the client. Stupid. Get a decent http client or implemented one
			// with connection pooling functionality
		} else {
			System.out.println("Warning! Attempted closing null client");
		}
	}

	@Override
	public SearchBuilder prepareSearch(String index, String query) {
		return new HTTPSearchBuilder(client, query).setIndices(index);
	}

	@Override
	public JsonObject execute(SearchBuilder builder) throws Exception {
		return builder.execute();
	}
}
