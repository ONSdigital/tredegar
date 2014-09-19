package com.github.onsdigital.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

public class SearchResult {

	private long took; // milliseconds
	private long totalPages;
	private long numbeOfResults; // total result number
	private List<Map<String, Object>> results; // results

	public SearchResult(SearchResponse response, int searchSize) {
		results = new ArrayList<Map<String, Object>>();
		this.numbeOfResults = response.getHits().getTotalHits();
		this.totalPages = (long) Math
				.ceil(((double) numbeOfResults / searchSize));
		this.took = response.getTookInMillis();
		addHits(response);
	}

	private void addHits(SearchResponse response) {
		SearchHit hit;
		Iterator<SearchHit> iterator = response.getHits().iterator();
		while (iterator.hasNext()) {
			hit = iterator.next();
			results.add(hit.getSource());
		}
	}

	public long getTook() {
		return took;
	}

	public void setTook(long took) {
		this.took = took;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getNumberOfHits() {
		return numbeOfResults;
	}

	public void setNumberOfHits(long numberOfHits) {
		this.numbeOfResults = numberOfHits;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, Object>> hits) {
		this.results = hits;
	}

}
