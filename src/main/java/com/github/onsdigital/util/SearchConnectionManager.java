package com.github.onsdigital.util;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class SearchConnectionManager {

	private final static String CLUSTER_NAME_PROPERTY = "cluster.name";
	private final static String SNIFF_PROPERTY = "client.transport.sniff";

	private Settings settings;
	private Client client;
	private String hostname;
	private int port;

	public SearchConnectionManager(String clusterName, String hostname, int port) {
		this.settings = ImmutableSettings.settingsBuilder()
				.put(CLUSTER_NAME_PROPERTY, clusterName)
				.put(SNIFF_PROPERTY, true).build();
		this.hostname = hostname;
		this.port = port;
	}

	@SuppressWarnings("resource")
	public void openConnection() {
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(hostname,
						port));
	}

	public void closeConnection() {
		if (client != null) {
			client.close();
		}
	}

	public Client getClient() {
		return client;
	}

	public boolean isConnected() {
		return client == null ? false : true;
	}
}
