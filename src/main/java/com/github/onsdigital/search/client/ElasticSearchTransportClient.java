package com.github.onsdigital.search.client;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.github.onsdigital.search.client.base.AbstractElasticSearchClient;
import com.github.onsdigital.search.client.base.ElasticSearchClient;

/**
 * Wrapper class for Elastic Search {@link TransportClient}. Sniffing is
 * activated by default to enable fail over and high availability features for
 * 
 * @author Bren
 *
 */
public class ElasticSearchTransportClient extends AbstractElasticSearchClient {

	final static String CLUSTER_NAME = System.getenv("BONSAI_CLUSTERNAME");
	final static String HOSTNAME = System.getenv("BONSAI_HOSTNAME");
	final static int PORT = Integer.parseInt(System
			.getenv("BONSAI_TRANSPORT_PORT"));

	private static ElasticSearchTransportClient instance;

	final static String CLUSTER_NAME_PROPERTY = "cluster.name";
	final static String SNIFF_PROPERTY = "client.transport.sniff";

	@SuppressWarnings("resource")
	private ElasticSearchTransportClient() {
		super(new TransportClient(ImmutableSettings.settingsBuilder()
				.put(CLUSTER_NAME_PROPERTY, CLUSTER_NAME)
				.put(SNIFF_PROPERTY, true).build())
				.addTransportAddress(new InetSocketTransportAddress(HOSTNAME,
						PORT)));
	}

	public static ElasticSearchClient getClient() {
		if (instance == null) {
			synchronized (ElasticSearchHTTPClient.class) {
				if (instance == null) {
					instance = new ElasticSearchTransportClient();
				}
			}
		}
		return instance;
	}
	
}
