package com.github.onsdigital.search.client;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.github.onsdigital.search.client.base.AbstractElasticSearchClient;
import com.github.onsdigital.search.client.base.ElasticSearchClient;

/**
 * 
 * Wrapper class for Elastic Search {@link NodeClient}. Cluster aware by default
 * with fail over and high availability functions. The node is not started right
 * away.
 * 
 * @author Bren
 */
public class ElasticSearchNodeClient extends AbstractElasticSearchClient {

	final static String CLUSTER_NAME = System.getenv("BONSAI_CLUSTERNAME");

	private static ElasticSearchNodeClient instance;

	private static Node node;

	private ElasticSearchNodeClient() {
		super(createNode().client());
		Runtime.getRuntime().addShutdownHook(new ShutDownNodeThread());
	}

	private static Node createNode() {
		node = NodeBuilder.nodeBuilder().clusterName(CLUSTER_NAME).data(false)
				.build();
		return node;
	}

	public static ElasticSearchClient getClient() {
		if (instance == null) {
			synchronized (ElasticSearchHTTPClient.class) {
				if (instance == null) {
					instance = new ElasticSearchNodeClient();
				}
			}
		}
		return instance;
	}

	class ShutDownNodeThread extends Thread {

		@Override
		public void run() {
			node.stop();
		}
	}

}
