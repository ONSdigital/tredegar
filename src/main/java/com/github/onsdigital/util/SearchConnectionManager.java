package com.github.onsdigital.util;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class SearchConnectionManager {

	@SuppressWarnings("resource")
	public static Client getClient() {
		Client client = null;
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch").build();
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						"localhost", 9300));

		return client;
	}

	public static void close(Client client) {
		if (client != null) {
			client.close();
		}
	}
}
