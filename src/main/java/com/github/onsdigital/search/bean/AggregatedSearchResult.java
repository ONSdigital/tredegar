package com.github.onsdigital.search.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * Represents results aggregated together to be serialized into JSON
 * 
 * @author brn
 *
 */
public class AggregatedSearchResult {

	// Search result of home type pages
	public SearchResult homeSearchResult;
	public SearchResult contentSearchResult;
	public long timeseriesCount;
	private boolean suggestionBasedResult;
	private String suggestion;

	public boolean isSuggestionBasedResult() {
		return suggestionBasedResult;
	}

	public void setSuggestionBasedResult(boolean suggestionBasedResult) {
		this.suggestionBasedResult = suggestionBasedResult;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public long getNumberOfResults() {
		long numberOfResults = contentSearchResult.getNumberOfResults();
		if(homeSearchResult != null) {
			return homeSearchResult.getNumberOfResults();	
		}
		return numberOfResults;
		 
	}

	public List<Map<String, Object>> getAllResults() {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		if(homeSearchResult != null) {			
			results.addAll(homeSearchResult.getResults());
		}
		results.addAll(contentSearchResult.getResults());
		return results;
	}

}
