package com.github.onsdigital.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

import com.github.onsdigital.search.bean.SearchResult;
import com.github.onsdigital.search.util.ONSQueryBuilder;

/**
 * 
 * Centralized service to search
 * 
 * @author brn
 *
 */
public class SearchService {

	private SearchService() {

	}

	/**
	 * Performs the search and returns documents as a list of maps that contains
	 * key-value pairs
	 * 
	 * @param queryBuilder
	 * @return {@link SearchResult}
	 * @throws Exception
	 */
	public static SearchResult search(ONSQueryBuilder queryBuilder) {
		System.out.println("Searcing For:" + ReflectionToStringBuilder.toString(queryBuilder));
		return new SearchResult(execute(queryBuilder));
	}

	/**
	 * Performs multi search and returns results
	 * 
	 * @return
	 */
	public static List<SearchResult> multiSearch(ONSQueryBuilder... queryBuilders) {
		System.out.println("Multiple Searcing For:" + getQueries(queryBuilders));
		List<SearchResult> results = new ArrayList<SearchResult>();

		MultiSearchResponse response = execute(queryBuilders);
		Item[] responses = response.getResponses();
		for (int i = 0; i < responses.length; i++) {
			Item item = responses[i];
			if (!item.isFailure()) {
				results.add(new SearchResult(item.getResponse()));
			} else {
				System.out.println("Warning: Search failed for " + ReflectionToStringBuilder.toString(queryBuilders));
			}
		}
		
		return results;

	}

	private static String getQueries(ONSQueryBuilder... querybuilders) {
		StringBuilder builder =  new StringBuilder();
		for (ONSQueryBuilder queryBuilder : querybuilders) {
			builder.append(ReflectionToStringBuilder.toString(queryBuilder));
		}
		return builder.toString();
	}

	private static SearchResponse execute(ONSQueryBuilder queryBuilder) {
		return buildRequest(queryBuilder).get();
	}

	private static MultiSearchResponse execute(ONSQueryBuilder... queryBuilders) {
		return buildMultiSearchRequest(queryBuilders).get();
	}

	private static SearchRequestBuilder buildRequest(ONSQueryBuilder queryBuilder) {
		SearchRequestBuilder searchBuilder = getClient().prepareSearch(queryBuilder.getIndex());
		String[] types = queryBuilder.getTypes();
		searchBuilder.setTypes(types);
		searchBuilder.setExtraSource(queryBuilder.buildQuery());
		return searchBuilder;
	}

	private static MultiSearchRequestBuilder buildMultiSearchRequest(ONSQueryBuilder... builders) {
		MultiSearchRequestBuilder multiSearchRequestBuilder = getClient().prepareMultiSearch();
		for (int i = 0; i < builders.length; i++) {
			multiSearchRequestBuilder.add(buildRequest(builders[i]));
		}
		return multiSearchRequestBuilder;
	}

	private static Client getClient() {
		return ElasticSearchServer.getClient();
	}

}
