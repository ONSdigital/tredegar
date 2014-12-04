package com.github.onsdigital.api.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.timeseries.Timeseries;
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
	public Map<String, List<Count>> results(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Serialiser.getBuilder().setPrettyPrinting();
		Map<String, List<Count>> result = new HashMap<>();
		result.put("No results", queryNoResults());
		result.put("Most Searched", querySearches());
		return result;
	}

	private List<Count> queryNoResults() throws Exception {

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

			DBCursor docs = searchTerms.find(findQuery);

			Map<String, Count> counts = new HashMap<>();
			while (docs.hasNext()) {
				DBObject doc = docs.next();
				String query = String.valueOf(doc.get("query"));
				Count count = counts.get(query);
				if (count == null) {
					count = new Count(query);
					counts.put(query, count);
				}
				count.count++;
			}

			List<Count> result = new ArrayList<>(counts.values());
			Collections.sort(result);
			return result;

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

	private List<Count> querySearches() throws Exception {

		MongoClientURI uri = new MongoClientURI(Configuration.getMongoDbUri());
		MongoClient client = null;
		try {
			// Connect to the database:
			client = new MongoClient(uri);
			DB db = client.getDB(uri.getDatabase());

			// Get the collection:
			DBCollection searchTerms = db.getCollection("searchTerms");

			// Get the results:
			BasicDBObject findQuery = new BasicDBObject("results", new BasicDBObject("$gt", 0));

			DBCursor docs = searchTerms.find(findQuery);

			Map<String, Count> counts = new HashMap<>();
			while (docs.hasNext()) {
				DBObject doc = docs.next();
				String query = String.valueOf(doc.get("query"));
				Count count = counts.get(query);
				if (count == null) {
					count = new Count(query);
					counts.put(query, count);
				}
				count.count++;
			}

			List<Count> result = new ArrayList<>(counts.values());
			Collections.sort(result);
			return result;

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

	static void save(final String query, final int page, final Object searchResult) {

		if (Timeseries.class.isAssignableFrom(searchResult.getClass())) {
			saveTimeseries(query, page, (Timeseries) searchResult);
		} else {
			saveSearchResult(query, page, (SearchResult) searchResult);
		}
	}

	private static void saveTimeseries(String query, int page, Timeseries timeseries) {

		Search search = new Search();
		search.query = query;
		search.page = page;

		// Single hit:
		Result result = new Result();
		result.name = timeseries.cdid();
		result.description = timeseries.name;
		result.type = ContentType.timeseries;
		result.uri = timeseries.uri;
		search.hits.add(result);

		save(search);
	}

	private static void saveSearchResult(String query, int page, SearchResult searchResult) {

		Search search = new Search();
		search.query = query;
		search.page = page;

		// Add the hits:
		for (Map<String, Object> hit : searchResult.getResults()) {
			Result result = new Result();
			result.name = hit.get("title").toString();
			Object lede = hit.get("lede");
			//Timeseries results does not have lede
			result.description = lede == null ? "" : lede.toString();
			result.type = ContentType.valueOf(hit.get("type").toString());
			result.uri = URI.create(hit.get("url").toString());
			search.hits.add(result);
		}
		save(search);
	}

	private static void save(final Search search) {
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
					searchTerms.insert(search.build());
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

	static class Search extends BasicDBObject {
		private static final long serialVersionUID = 2138332036592544966L;
		String query;
		int page;
		long results;
		// Ordered list of results - enables the ranking to be seen
		List<Result> hits = new ArrayList<SearchConsole.Result>();

		DBObject build() {
			append("query", query);
			append("page", page);
			append("results", hits.size());
			for (Result hit : hits) {
				hit.build();
				append("hits", hits);
			}
			return this;
		}
	}

	static class Result extends BasicDBObject {
		static final long serialVersionUID = 7760752367684896714L;
		String name;
		String description;
		URI uri;
		ContentType type;

		void build() {
			append("name", name);
			append("description", description);
			append("uri", uri.toString());
			append("type", type.toString());
		}
	}
}
