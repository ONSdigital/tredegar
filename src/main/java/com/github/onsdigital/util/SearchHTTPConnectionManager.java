package com.github.onsdigital.util;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class SearchHTTPConnectionManager {

	private JestClient client;
	private String url;

	public SearchHTTPConnectionManager(String url) {
		this.url = url;
	}

	public void openConnection() {
		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(url)
				.multiThreaded(true).build());
		client = factory.getObject();
	}

	public void closeConnection() {
		if (client != null) {
			client.shutdownClient();
		}
	}

	public JestClient getClient() {
		return client;
	}

	public boolean isConnected() {
		return client == null ? false : true;
	}

}
