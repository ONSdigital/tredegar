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

import com.google.gson.JsonObject;

/**
 * Puts information returned from search operation together to be converted into
 * Json format
 * 
 * 
 * @author Bren
 *
 */
public class SearchResult {

	private long took; // milliseconds
	private long numberOfResults; // total number of hits
	private List<Map<String, Object>> results; // results

	/**
	 * Create search results using {@link JsonObject}
	 * 
	 * @param result
	 */
	public SearchResult(SearchResponse response) {
		results = new ArrayList<Map<String, Object>>();
		this.took = response.getTookInMillis();
		this.numberOfResults = response.getHits().getTotalHits();
		resolveHits(response);
	}

	void resolveHits(SearchResponse response) {
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

	Map<? extends String, ? extends Object> extractHihglightedFields(
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
