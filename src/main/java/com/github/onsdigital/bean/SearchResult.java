package com.github.onsdigital.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SearchResult {

	private long took; // milliseconds
	private long numberOfResults; // total result number
	private List<Map<String, Object>> results; // results

	public SearchResult(SearchResponse response) {
		results = new ArrayList<Map<String, Object>>();
		this.numberOfResults = response.getHits().getTotalHits();
		this.took = response.getTookInMillis();
		addHits(response);
	}

	public SearchResult(io.searchbox.core.SearchResult result) {
		results = new ArrayList<Map<String, Object>>();
		JsonObject jestResult = result.getJsonObject();
		this.took = jestResult.get("took").getAsLong();
		this.numberOfResults = jestResult.get("hits").getAsJsonObject()
				.get("total").getAsLong();
		addHits(jestResult);

	}

	private void addHits(SearchResponse response) {
		SearchHit hit;
		Iterator<SearchHit> iterator = response.getHits().iterator();
		while (iterator.hasNext()) {
			hit = iterator.next();
			Map<String, Object> item = new HashMap<String, Object>(
					hit.getSource());
			item.put("type", hit.getType());
			item.putAll(extractHihglightedFields(hit));
			results.add(item);
		}
	}

	private void addHits(JsonObject jestResult) {
		JsonElement hit;
		Iterator<JsonElement> iterator = jestResult.get("hits")
				.getAsJsonObject().get("hits").getAsJsonArray().iterator();
		while (iterator.hasNext()) {
			hit = iterator.next();
			Map<String, Object> item = new HashMap<>();
			JsonObject object = hit.getAsJsonObject();
			item.put("type", object.get("_type").getAsString());
			item.put("title",
					object.get("_source").getAsJsonObject().get("title"));
			item.put("path", object.get("_source").getAsJsonObject()
					.get("path"));
			item.put("url", object.get("_source").getAsJsonObject().get("url"));
			// item.putAll(extractHihglightedFields(hit));
			results.add(item);
		}
	}

	private Map<? extends String, ? extends Object> extractHihglightedFields(
			SearchHit hit) {

		HashMap<String, Object> highlightedFields = new HashMap<>();

		for (Entry<String, HighlightField> entry : hit.getHighlightFields()
				.entrySet()) {
			Text[] fragments = entry.getValue().getFragments();
			if (fragments != null) {
				for (Text text : fragments) {
					highlightedFields.put(entry.getKey(), text.toString());
				}
			}
		}

		return highlightedFields;
	}

	public long getTook() {
		return took;
	}

	public void setTook(long took) {
		this.took = took;
	}

	public long getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(long numberOfHits) {
		this.numberOfResults = numberOfHits;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, Object>> hits) {
		this.results = hits;
	}

}
