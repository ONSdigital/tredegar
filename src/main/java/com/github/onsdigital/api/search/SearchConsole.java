package com.github.onsdigital.api.search;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.search.bean.SearchResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Endpoint
public class SearchConsole {

	static String mongoUri = Configuration.getMongoDbUri();
	static ExecutorService pool = Executors.newCachedThreadPool();

	@GET
	public Map<String, Integer> results(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Serialiser.getBuilder().setPrettyPrinting();
		return query();
	}

	private Map<String, Integer> query() throws Exception {
		Map<String, Integer> counts = new TreeMap<>();

		MongoClientURI uri = new MongoClientURI(Configuration.getMongoDbUri());
		MongoClient client = null;
		try {
			// Connect to the database:
			client = new MongoClient(uri);
			DB db = client.getDB(uri.getDatabase());

			// Get the collection:
			DBCollection searchTerms = db.getCollection("searchTerms");

			// Get the results:
			BasicDBObject findQuery = new BasicDBObject("results", 0);
			BasicDBObject orderBy = new BasicDBObject("query", 1);

			DBCursor docs = searchTerms.find(findQuery).sort(orderBy);

			while (docs.hasNext()) {
				DBObject doc = docs.next();
				String query = String.valueOf(doc.get("query"));
				Integer count = counts.get(query);
				if (count == null) {
					count = Integer.valueOf(0);
				}
				counts.put(query, Integer.valueOf(count.intValue() + 1));
			}

			return counts;

		} catch (Exception e) {
			System.out.println("Error connecting to MongoDB at: " + mongoUri);
			System.out.println(ExceptionUtils.getStackTrace(e));
			throw e;
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	static void save(final String query, final int page, final String[] types, final SearchResult search) {

		// Submit to be saved asynchronously.
		// This minimises response time and we're not too worried about whether
		// the data get committed - we're mainly after a sample:
		pool.execute(new Runnable() {

			@Override
			public void run() {

				MongoClientURI uri = new MongoClientURI(Configuration.getMongoDbUri());
				MongoClient client = null;
				try {
					// Connect to the database:
					client = new MongoClient(uri);
					DB db = client.getDB(uri.getDatabase());

					// Get the collection:
					DBCollection searchTerms = db.getCollection("searchTerms");

					// Save the record:
					BasicDBObject record = new BasicDBObject();
					record.append("query", query);
					record.append("page", page);
					record.append("types", types);
					record.append("results", search.getNumberOfResults());
					searchTerms.insert(record);
					System.out.println("Total: " + searchTerms.getCount());

				} catch (Exception e) {
					System.out.println("Error connecting to MongoDB at: " + mongoUri);
					System.out.println(ExceptionUtils.getStackTrace(e));
				} finally {
					if (client != null) {
						client.close();
					}
				}

			}
		});
	}
}
