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
			.getProperty("java.io.tmpdir");;
	private static final String DEFAULT_CLUSTERNAME = "ONS";
	private final Node node;
	private String dataDirectory;

	public EmbeddedElasticSearchServer(String clusterName) {
		this(null, DEFAULT_DATA_DIRECTORY, clusterName);
	}

	public EmbeddedElasticSearchServer(String dataDirectory, String clusterName) {
		this(null, dataDirectory, clusterName);
	}

	public EmbeddedElasticSearchServer(Settings settings, String clusterName) {
		this(settings, null, clusterName);
	}

	public EmbeddedElasticSearchServer(Settings settings, String dataDirectory,
			String clusterName) {

		this.dataDirectory = (dataDirectory == null) ? DEFAULT_DATA_DIRECTORY
				: dataDirectory;
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings
				.settingsBuilder().put("cluster.name", DEFAULT_CLUSTERNAME)
				.put("http.enabled", true)
				.put("path.data", DEFAULT_DATA_DIRECTORY)
				.put("node.data", true);

		if (settings != null) {
			settingsBuilder.put(settings);
			// If data directory is overwritten update data directory
			// accordingly for cleanup at shutdown
			String directory = settings.get("path.data");
			if (directory != null) {
				this.dataDirectory = directory;
			}
		}

		System.out
				.println("Starting embedded Elastic Search node with settings"
						+ settingsBuilder.internalMap());
		node = NodeBuilder.nodeBuilder().local(true)
				.settings(settingsBuilder.build()).node();
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
