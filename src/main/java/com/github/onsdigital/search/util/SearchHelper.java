package com.github.onsdigital.search.util;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.search.ElasticSearchServer;
import com.github.onsdigital.search.SearchService;
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
	public static SearchResult search(String searchTerm, int page, String... types) {
		if (ArrayUtils.isEmpty(types)) {
			return searchMultiple(searchTerm, page);
		} else {
			return SearchService.search(buildContentQuery(searchTerm, page, types));
		}
	}

	/**
	 * Performs timeseries search with given cdid and returns a single result if
	 * found
	 * 
	 */
	public static Timeseries searchCdid(String cdid) {
		ONSQueryBuilder cdidQuery = new ONSQueryBuilder("ons").setSearchTerm(cdid).setFields(CDID).setType(ContentType.timeseries.toString());
		SearchResult result =  SearchService.search(cdidQuery);
		
		if(result.getNumberOfResults() == 0 ) {
			return null;
		}
		
		for (Map<String, Object> timeSeriesProperties  : result.getResults()) {
			//If cdic is not exact match consider it no match
			if(!timeSeriesProperties.get("cdid").toString().toLowerCase().equals(cdid.toLowerCase())) {
				continue;
			}
			Timeseries timeseries = new Timeseries();
			timeseries.setCdid((String) timeSeriesProperties.get("cdid"));
			timeseries.name = (String) timeSeriesProperties.get("name");
			timeseries.uri = URI.create((String) timeSeriesProperties.get("url"));
			return timeseries;
		}
		
		return null;
	}

	public static SearchResult searchSuggestions(String query, int page, String[] types) throws IOException, Exception {
		TermSuggestionBuilder termSuggestionBuilder = new TermSuggestionBuilder("autocorrect").field("title").text(query).size(1);
		SuggestResponse suggestResponse = ElasticSearchServer.getClient().prepareSuggest("ons").addSuggestion(termSuggestionBuilder).execute().actionGet();
		SearchResult result = null;

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

	private static SearchResult searchMultiple(String searchTerm, int page) {
		// If no filter and first page, include one home type result at the top
		List<SearchResult> responses = SearchService.multiSearch(buildHomeQuery(searchTerm, page), buildContentQuery(searchTerm, page));
		Iterator<SearchResult> resultsIterator = responses.iterator();
		SearchResult homeResult = resultsIterator.next();
		SearchResult contentResult = resultsIterator.next();
		if(homeResult.getNumberOfResults() > 0) {
			//Add content results to home result and return
			if(page > 1) {
				homeResult.setResults(contentResult.getResults());
			} else {
				homeResult.getResults().addAll(contentResult.getResults());
			}
			homeResult.setNumberOfResults(1 + contentResult.getNumberOfResults());
			return homeResult;
		}
		return contentResult;
	}

	private static ONSQueryBuilder buildHomeQuery(String searchTerm, int page) {
		ONSQueryBuilder homeQuery = new ONSQueryBuilder("ons").setType(ContentType.home.toString()).setPage(page).setSearchTerm(searchTerm).setSize(1).setFields(TITLE, "url");
		return homeQuery;
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
}
