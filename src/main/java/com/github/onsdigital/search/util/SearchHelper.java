package com.github.onsdigital.search.util;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.search.ElasticSearchServer;
import com.github.onsdigital.search.SearchService;
import com.github.onsdigital.search.bean.AggregatedSearchResult;
import com.github.onsdigital.search.bean.SearchResult;

public class SearchHelper {

	private final static String TITLE = "title";
	private final static String CDID = "cdid";

	/**
	 * Performs search, if first page with no type filtering returns a single
	 * most relevant home type result at the top
	 * 
	 * @param searchTerm
	 * @param page
	 * @param types
	 * @return
	 */
	public static AggregatedSearchResult search(String searchTerm, int page, String... types) {
		if (ArrayUtils.isEmpty(types) && page < 2) {
			return searchMultiple(searchTerm, page);
		} else {
			return doSearch(searchTerm, page, types);
		}
	}

	public static SearchResult autocomplete(String searchTerm) {
		ONSQueryBuilder builder = buildAutocompleteQuery(searchTerm);
		return SearchService.search(builder);
	}

	/**
	 * Performs timeseries search with given cdid and returns a single result if
	 * found
	 * 
	 */
	public static Timeseries searchCdid(String cdid) {

		cdid = cdid.toUpperCase();

		TermFilterBuilder termFilterBuilder = new TermFilterBuilder(CDID, cdid);

		SearchRequestBuilder searchRequestBuilder = (ElasticSearchServer.getClient().prepareSearch("ons")).setQuery(termFilterBuilder.buildAsBytes());

		SearchResult result = new SearchResult(searchRequestBuilder.get());

		if (result.getNumberOfResults() == 0) {
			return null;
		}

		Map<String, Object> timeSeriesProperties = result.getResults().iterator().next();
		Timeseries timeseries = new Timeseries();
		timeseries.setCdid((String) timeSeriesProperties.get("cdid"));
		timeseries.name = (String) timeSeriesProperties.get("name");
		timeseries.uri = URI.create((String) timeSeriesProperties.get("url"));
		return timeseries;

	}

	public static AggregatedSearchResult searchSuggestions(String query, int page, String[] types) throws IOException, Exception {
		TermSuggestionBuilder termSuggestionBuilder = new TermSuggestionBuilder("autocorrect").field("title").text(query).size(2);
		SuggestResponse suggestResponse = ElasticSearchServer.getClient().prepareSuggest("ons").addSuggestion(termSuggestionBuilder).execute().actionGet();
		AggregatedSearchResult result = null;

		StringBuffer suggestionsBuffer = new StringBuffer();
		Iterator<? extends Entry<? extends Option>> iterator = suggestResponse.getSuggest().getSuggestion("autocorrect").getEntries().iterator();
		while (iterator.hasNext()) {
			Entry<? extends Option> entry = iterator.next();
			if (entry.getOptions().isEmpty()) {
				suggestionsBuffer.append(entry.getText());
			} else {
				Text text = entry.getOptions().get(0).getText();
				suggestionsBuffer.append(text);
			}
			if (iterator.hasNext()) {
				suggestionsBuffer.append(" ");
			}
		}

		String suggestionsBufferAsString = suggestionsBuffer.toString();
		if (StringUtils.isEmpty(suggestionsBufferAsString)) {
			System.out.println("All search steps failed to discover suitable match");
		} else {
			result = search(suggestionsBufferAsString, page, types);
			result.setSuggestionBasedResult(true);
			result.setSuggestion(suggestionsBufferAsString);
			System.out.println("Failed to find any results for[" + query + "] so will use suggestion of [" + suggestionsBufferAsString + "]");
		}
		return result;
	}

	private static AggregatedSearchResult doSearch(String searchTerm, int page, String... types) {
		SearchResult searchResult = SearchService.search(buildContentQuery(searchTerm, page, types));

		AggregatedSearchResult result = new AggregatedSearchResult();
		result.contentSearchResult = searchResult;
		return result;
	}

	
	private static AggregatedSearchResult searchMultiple(String searchTerm, int page) {
		// If no filter and first page, include one home type result at the top
		List<SearchResult> responses = SearchService.multiSearch(buildHomeQuery(searchTerm, page), buildContentQuery(searchTerm, page));
		long timeseriesCount = SearchService.count(buildTimeseriesCountQuery(searchTerm));
		Iterator<SearchResult> resultsIterator = responses.iterator();
		AggregatedSearchResult result = new AggregatedSearchResult();
		result.homeSearchResult = resultsIterator.next();
		result.contentSearchResult =  resultsIterator.next();
		result.timeseriesCount = timeseriesCount;
		return result;
	}

	private static ONSQueryBuilder buildHomeQuery(String searchTerm, int page) {
		ONSQueryBuilder homeQuery = new ONSQueryBuilder("ons").setType(ContentType.home.toString()).setPage(page).setSearchTerm(searchTerm).setSize(1).setFields(TITLE, "url");
		return homeQuery;
	}
	
	private static ONSQueryBuilder buildTimeseriesCountQuery(String searchTerm) {
		ONSQueryBuilder timeseriesCountQuery = new ONSQueryBuilder("ons").setType(ContentType.timeseries.toString()).setSearchTerm(searchTerm).setFields(TITLE, "url");
		return timeseriesCountQuery;
	}

	private static ONSQueryBuilder buildContentQuery(String searchTerm, int page, String... types) {
		ONSQueryBuilder contentQuery = new ONSQueryBuilder("ons").setSearchTerm(searchTerm).setFields(TITLE, "url");
		if (ArrayUtils.isEmpty(types)) {
			contentQuery.setTypes(ContentType.bulletin.toString(), ContentType.dataset.toString(), ContentType.methodology.toString(), ContentType.article.toString()).setPage(page);

		} else {
			contentQuery.setTypes(types);
		}

		return contentQuery;
	}

	private static ONSQueryBuilder buildAutocompleteQuery(String searchTerm) {
		ONSQueryBuilder autocompleteQuery = new ONSQueryBuilder("ons").setSearchTerm(searchTerm).setFields(TITLE, "url");
		autocompleteQuery.setTypes(ContentType.timeseries.toString(), ContentType.bulletin.toString(), ContentType.dataset.toString(), ContentType.methodology.toString(),
				ContentType.article.toString());
		return autocompleteQuery;
	}
}
