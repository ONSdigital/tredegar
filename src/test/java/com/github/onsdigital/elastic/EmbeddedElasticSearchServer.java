package com.github.onsdigital.elastic;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class EmbeddedElasticSearchServer {

	private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

	private final Node node;
	private final String dataDirectory;

	public EmbeddedElasticSearchServer(String clusterName) {
		this(DEFAULT_DATA_DIRECTORY, clusterName);
	}

	public EmbeddedElasticSearchServer(String dataDirectory, String clusterName) {
		this.dataDirectory = dataDirectory;

		ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings
				.settingsBuilder().put("cluster.name", clusterName)
				.put("transport.tcp.port:", 28999).put("http.enabled", "false")
				.put("path.data", dataDirectory);

		node = NodeBuilder.nodeBuilder().local(true)
				.settings(elasticsearchSettings.build()).node();
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
