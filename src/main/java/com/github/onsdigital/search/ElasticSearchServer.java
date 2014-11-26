package com.github.onsdigital.search;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.elasticsearch.client.Client;

import com.github.davidcarboni.restolino.framework.Startup;

/**
 * Starts an {@link EmbeddedElasticSearchServer} when a client requested
 * 
 * @author Bren
 *
 */
public class ElasticSearchServer implements Startup {

	static ExecutorService pool = Executors.newSingleThreadExecutor();

	static EmbeddedElasticSearchServer server;
	static Future<Client> client;

	@Override
	public void init() {
		startEmbeddedServer();
	}

	/**
	 * NB caching the client for the entire application to use is safe and
	 * recommended:
	 * <p>
	 * <a href=
	 * "http://stackoverflow.com/questions/15773476/elasticsearch-client-thread-safety"
	 * >http://stackoverflow.com/questions/15773476/elasticsearch-client-thread-
	 * safety</a>
	 * 
	 * @return
	 */
	public static Client getClient() {
		try {
			return client.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException("Error getting Elasticsearch client - indexing may have failed", e);
		}
	}

	public static void startEmbeddedServer() {
		if (server == null) {
			client = pool.submit(new Callable<Client>() {
				@Override
				public Client call() throws Exception {
					long start;

					// Server
					start = System.currentTimeMillis();
					System.out.println("Elasticsearch: starting embedded server..");
					server = new EmbeddedElasticSearchServer("ONSNode");
					Runtime.getRuntime().addShutdownHook(new ShutDownNodeThread());
					System.out.println("Elasticsearch: embedded server started (" + (System.currentTimeMillis() - start) + "ms)");

					// Client
					start = System.currentTimeMillis();
					System.out.println("Elasticsearch: creating client..");
					Client client = server.getClient();
					System.out.println("Elasticsearch: client set up (" + (System.currentTimeMillis() - start) + "ms)");

					// Index
					start = System.currentTimeMillis();
					System.out.println("Elasticsearch: indexing..");
					Indexer.loadIndex(client);
					System.out.println("Elasticsearch: indexing complete (" + (System.currentTimeMillis() - start) + "ms)");

					return client;
				}
			});
		}
	}

	static class ShutDownNodeThread extends Thread {
		@Override
		public void run() {

			// Once we get the client, the server
			// is guaranteed to have been created:
			getClient().close();
			server.shutdown();
		}
	}

}
