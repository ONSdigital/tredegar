package com.github.onsdigital.api.search;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;
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
	static String never = "Never any results";
	static String noneNow = "Currently returning no results";
	static String someNow = "Currently returning results";
	static String always = "Always returns results";

	@GET
	public Object results(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Serialiser.getBuilder().setPrettyPrinting();
		List<DBObject> queryDocuments = getQueryDocuments();
		List<QueryCount> queryCounts = countQueries(queryDocuments);
		JsonResult result = new JsonResult();
		sortQueries(queryCounts, result);
		return result;
	}

	static void save(final String query, final int page, final Object searchResult) {

		if (Timeseries.class.isAssignableFrom(searchResult.getClass())) {
			saveTimeseries(query, page, (Timeseries) searchResult);
		} else {
			saveSearchResult(query, page, (SearchResult) searchResult);
		}
	}

	static class JsonResult {

		int never;
		int always;
		int noneNow;
		int someNow;
		Map<String, List<QueryCount>> categories;

		JsonResult() {
			// Ordered Map for the sake of human-readability:
			categories = new LinkedHashMap<>();
			categories.put(SearchConsole.never, new ArrayList<SearchConsole.QueryCount>());
			categories.put(SearchConsole.noneNow, new ArrayList<SearchConsole.QueryCount>());
			categories.put(SearchConsole.someNow, new ArrayList<SearchConsole.QueryCount>());
			categories.put(SearchConsole.always, new ArrayList<SearchConsole.QueryCount>());
		}

	}

	private List<DBObject> getQueryDocuments() throws Exception {
		List<DBObject> result = new ArrayList<>();

		MongoClientURI uri = new MongoClientURI(Configuration.getMongoDbUri());
		MongoClient client = null;
		try {
			// Connect to the database:
			client = new MongoClient(uri);
			DB db = client.getDB(uri.getDatabase());

			// Get the collection:
			DBCollection searchTerms = db.getCollection("searchTerms");

			DBCursor docs = searchTerms.find();

			while (docs.hasNext()) {
				result.add(docs.next());
			}

		} catch (Exception e) {
			System.out.println("Error connecting to MongoDB at: " + mongoUri);
			System.out.println(ExceptionUtils.getStackTrace(e));
			throw e;
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return result;
	}

	private List<QueryCount> countQueries(List<DBObject> queryDocuments) {

		// Start with a map to collate queries that are the same:
		Map<String, QueryCount> queryCounts = new HashMap<>();
		for (DBObject doc : queryDocuments) {

			// Get the count for this query, creating it if necessary:
			String query = (String) doc.get("query");
			if (StringUtils.equals("inf", query)) {
				System.out.println("inf");
			}
			System.out.println();
			QueryCount count = queryCounts.get(query);
			if (count == null) {
				count = new QueryCount(query);
				queryCounts.put(query, count);
			}

			// Increment the times this query has been seen:
			count.count++;

			// Record the number of results returned by the query on that
			// occasion. Records with no date are the oldest, so we use a
			// default date value:
			Object resultCount = doc.get("results");
			Object resultDate = doc.get("date");
			Date nullDate = null;
			if (resultCount != null) {
				Date date = null;
				if (resultDate != null) {
					date = (Date) resultDate;
				} else {
					date = toDate(nullDate);
				}
				if (resultCount instanceof Integer) {
					count.results.put(date, Long.valueOf(resultCount.toString()));
				} else {
					count.results.put(date, (Long) resultCount);
				}
			}
		}

		return new ArrayList<SearchConsole.QueryCount>(queryCounts.values());
	}

	private void sortQueries(List<QueryCount> queryCounts, JsonResult result) {

		// Sort into "never any results", "always results",
		// "currently some results" and "currently no results":
		for (QueryCount queryCount : queryCounts) {

			// Work out if this query has never/always/sometimes returned
			// results:
			boolean none = false;
			boolean some = false;
			Date mostRecent = null;
			for (Date queryDate : queryCount.results.keySet()) {
				long numberOfResults = queryCount.results.get(queryDate);
				if (numberOfResults == 0) {
					none = true;
				} else {
					some = true;
				}
				if (mostRecent == null || queryDate.before(mostRecent)) {
					mostRecent = queryDate;
				}
			}
			if (none && some) {
				// Are we currently getting some results or no results
				// for this query?
				long latestResultCount = queryCount.results.get(mostRecent);
				if (latestResultCount == 0) {
					result.noneNow++;
					result.categories.get(noneNow).add(queryCount);
				} else {
					result.someNow++;
					result.categories.get(someNow).add(queryCount);
				}
			} else if (none) {
				result.never++;
				result.categories.get(never).add(queryCount);
			} else if (some) {
				result.always++;
				result.categories.get(always).add(queryCount);
			}
		}

	}

	/**
	 * Generates an approximation of when a record with a null date was created.
	 * The earliest iteration of this feature didn't include a date.
	 * 
	 * @param nullDate
	 *            Pass in the last result of this method to generate the next
	 *            date in the sequence.
	 * @return A date, on or after 2014-12-03
	 */
	private Date toDate(Date nullDate) {
		Date result;
		if (nullDate == null) {
			try {
				result = new SimpleDateFormat("yyyy-MM-dd").parse("2014-12-03");
			} catch (ParseException e) {
				throw new RuntimeException("If you see this, something amazing just happened.");
			}
		} else {
			result = new Date(nullDate.getTime() + 1000);
		}
		return result;
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
			// Timeseries results does not have lede
			result.description = lede == null ? "" : lede.toString();
			result.type = ContentType.valueOf(hit.get("type").toString());
			result.uri = URI.create(hit.get("url").toString());
			search.hits.add(result);
		}
		save(search);
		updateNoResults(query, searchResult);
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

	private static void updateNoResults(String query, SearchResult searchResult) {
		try {
			if (searchResult.getResults().size() == 0 && StringUtils.equals(StringUtils.trim(StringUtils.lowerCase(query)), "newport explorers")) {
				searchResult.setNumberOfResults(1);
				searchResult.setSuggestion("Alpha Team");
				searchResult.setSuggestionBasedResult(true);

				Map<String, Object> result = new HashMap<>();
				result.put("title", "The Newport Explorers");
				result.put("lede", "This prototype (\"Alpha\") ONS website was brobugt to you by, amongst others, a band of brothers who left kin and country "
						+ "to make this happen - and it's been great. Here's a bit more about the team..");
				result.put("type", ContentType.unknown);
				result.put("url", "http://davidcarboni.github.io/newport-explorers/");
				searchResult.getResults().add(result);
			}
		} catch (Throwable t) {
			// We don't want any exceptions propagated.
			System.out.println(ExceptionUtils.getStackTrace(t));
		}
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
			append("date", new Date());
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

	static class QueryCount implements Comparable<QueryCount> {

		int count;
		String query;
		Map<Date, Long> results = new TreeMap<>();
		Date date;

		QueryCount(String query) {
			this.query = query;
		}

		@Override
		public int compareTo(QueryCount o) {
			return o.count - count;
		}

		@Override
		public int hashCode() {
			int result = 0;
			if (query != null) {
				result = query.hashCode();
			}
			return result;
		}

		/**
		 * Ignoring null and incompatible types - not going to happen.
		 */
		@Override
		public boolean equals(Object obj) {
			return StringUtils.equals(query, ((QueryCount) obj).query);
		}

		@Override
		public String toString() {
			return query + ":" + count;
		}
	}
}
