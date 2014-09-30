package com.github.onsdigital.search;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class EmbeddedElasticSearchServer {

	private static final String DEFAULT_DATA_DIRECTORY = System
			.getProperty("java.io.tmpdir");
	private static final int DEFAULT_HTTP_PORT = 9200;
	private static final int DEFAULT_TCP_PORT = 9300;

	private final Node node;
	private final String dataDirectory;

	public EmbeddedElasticSearchServer(String clusterName) {
		this(null, DEFAULT_DATA_DIRECTORY, clusterName);
	}

	public EmbeddedElasticSearchServer(String dataDirectory, String clusterName) {
		this(null, dataDirectory, clusterName);
	}

	public EmbeddedElasticSearchServer(Settings settings, String dataDirectory,
			String clusterName) {

		this.dataDirectory = (dataDirectory == null) ? DEFAULT_DATA_DIRECTORY
				: dataDirectory;

		Settings elasticsearchSettings = settings;
		if (settings == null) {
			elasticsearchSettings = ImmutableSettings.settingsBuilder()
					.put("cluster.name", clusterName).put("http.enabled", true)
					.put("http.port", DEFAULT_HTTP_PORT)
					.put("transport.tcp.port", DEFAULT_TCP_PORT)
					.put("path.data", dataDirectory).put("node.data", true)
					.build();

		}
		System.out
				.println("Starting embedded Elastic Search node with settings"
						+ elasticsearchSettings.toDelimitedString(':'));
		node = NodeBuilder.nodeBuilder().local(true)
				.settings(elasticsearchSettings).node();
	}

	public Client getClient() {
		return node.client();
	}

	public void shutdown() {
		node.close();
		deleteDataDirectory();
	}

	private void deleteDataDirectory() {
		try {
			FileUtils.deleteDirectory(new File(dataDirectory));
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not delete data directory of embedded elasticsearch server",
					e);
		}
	}
}
