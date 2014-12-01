package com.github.onsdigital.api.search;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.configuration.ElasticSearchProperties;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.search.ElasticSearchServer;
import com.github.onsdigital.search.bean.SearchResult;
import com.github.onsdigital.search.util.ElasticSearchFieldUtil;
import com.github.onsdigital.search.util.ONSQueryBuilder;
import com.github.onsdigital.search.util.SearchHelper;
import com.github.onsdigital.util.ValidatorUtil;

/**
 * 
 * Search endpoint that invokes appropriate search engine
 * 
 * @author Bren
 *
 */
@Endpoint
public class Search {
	final static String jsonMime = "application/json";
	final static String BONSAI_URL = System.getenv("BONSAI_URL");
	private final static String TITLE = "title";

	@GET
	public Object get(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		return search(extractQuery(request), extractPage(request),
				extractTypes(request));
	}

	private Object search(String query, int page, String[] types)
			throws Exception {
		ONSQueryBuilder queryBuilder = new ONSQueryBuilder("ons")
				.setTypes(types).setPage(page).setSearchTerm(query)
				.setFields("title", "url");

		/*
		 * Search uses a number of steps to discover any appropriates matches:-
		 * 1. Search core content types of home pages, bulletins etc. 2. If
		 * nothing found then, and only then, search timeseries 3. If still no
		 * result, then use a 'term' suggestion, that catches possible typos
		 */
		SearchResult searchResult = new SearchHelper(
				ElasticSearchServer.getClient()).search(queryBuilder);
		if (searchResult.getNumberOfResults() == 0 && types == null) {// If type
																		// is
																		// set
																		// don't
																		// search
																		// for
																		// timeseries
			searchResult = searchTimeseries(query, page);
			// if still no results then use term suggester for autocorrect
			if (searchResult.getNumberOfResults() == 0) {
				searchResult = searchSuggestions(query, page, types,
						searchResult);
			}
		}
		return searchResult;
	}

	private int extractPage(HttpServletRequest request) {
		String page = request.getParameter("page");
		if (StringUtils.isNotEmpty(page) && StringUtils.isNumeric(page)) {
			int pageNumber = Integer.parseInt(page);
			return pageNumber < 1 ? 1 : pageNumber;
		}
		return 1;
	}

	private String[] extractTypes(HttpServletRequest request) {
		String[] types = request.getParameterValues("type");
		return ArrayUtils.isNotEmpty(types) ? types : null;
	}

	private String extractQuery(HttpServletRequest request) {
		String query = request.getParameter("q");

		if (StringUtils.isEmpty(query)) {
			// check to see if this is part of search's autocomplete
			query = request.getParameter("term");
			if (StringUtils.isEmpty(query)) {
				throw new RuntimeException("No search query provided");
			}
		}
		if (ValidatorUtil.isIllegalCharacter(query)) {
			throw new RuntimeException(
					"Search query can only contain alphanumeric characters");
		}

		return query;
	}

	private String getTitle() {
		String titleBoost = (String) ElasticSearchProperties.INSTANCE
				.getProperty(TITLE);
		return ElasticSearchFieldUtil.getBoost(TITLE, titleBoost);
	}

	private SearchResult searchSuggestions(String query, int page,
			String[] types, SearchResult searchResult) throws IOException,
			Exception {
		System.out
				.println("No results found from timeseries so using suggestions for: "
						+ query);
		TermSuggestionBuilder termSuggestionBuilder = new TermSuggestionBuilder(
				"autocorrect").field("title").text(query).size(1);
		SuggestResponse suggestResponse = ElasticSearchServer.getClient()
				.prepareSuggest("ons").addSuggestion(termSuggestionBuilder)
				.execute().actionGet();

		StringBuffer suggestionsBuffer = new StringBuffer();
		Iterator<? extends Entry<? extends Option>> iterator = suggestResponse
				.getSuggest().getSuggestion("autocorrect").getEntries()
				.iterator();
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
			System.out
					.println("All search steps failed to discover suitable match");
		} else {
			ONSQueryBuilder suggestionsQueryBuilder = new ONSQueryBuilder("ons")
					.setTypes(types).setPage(page)
					.setSearchTerm(suggestionsBufferAsString)
					.setFields(getTitle(), "url");
			searchResult = new SearchHelper(ElasticSearchServer.getClient())
					.search(suggestionsQueryBuilder);
			searchResult.setSuggestionBasedResult(true);
			searchResult.setSuggestion(suggestionsBufferAsString);
			System.out.println("Failed to find any results for[" + query
					+ "] so will use suggestion of ["
					+ suggestionsBufferAsString + "]");
		}
		return searchResult;
	}

	private SearchResult searchTimeseries(String query, int page)
			throws Exception, IOException {
		System.out
				.println("Attempting search against timeseries as no results found for: "
						+ query);
		ONSQueryBuilder timeSeriesQueryBuilder = new ONSQueryBuilder("ons")
				.setType(ContentType.timeseries.name()).setPage(page)
				.setSearchTerm(query).setFields(getTitle(), "url");
		SearchResult searchResult = new SearchHelper(
				ElasticSearchServer.getClient()).search(timeSeriesQueryBuilder);
		return searchResult;
	}
}
