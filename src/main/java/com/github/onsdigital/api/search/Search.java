package com.github.onsdigital.api.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.search.bean.AggregatedSearchResult;
import com.github.onsdigital.search.util.SearchHelper;

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

	@GET
	public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {

		String query = extractQuery(request);
		Object searchResult = null;
		if (StringUtils.isNotBlank(request.getParameter("q"))) {
			int page = extractPage(request);
			String[] types = extractTypes(request);
			searchResult = search(query, page, types);
			// This is a search result page.
			// Autocomplete requests pass a parameter of "term".
			SearchConsole.save(query, page, searchResult);
		} else if (StringUtils.isNotBlank(request.getParameter("term"))) {
			searchResult = autoComplete(query);
		}

		return searchResult;
	}

	private Object search(String query, int page, String[] types) throws Exception {

		/*
		 * Search uses a number of steps to discover any appropriates matches:-
		 * 1. Search core content types of home pages, bulletins etc. 2. If
		 * nothing found then, and only then, search for single timeseries for
		 * cdid search 3. If still no result, then use a 'term' suggestion, that
		 * catches possible typos
		 */
		// don't use naturalLanguage for initial search so we get PHRASE_PREFIX
		// capability
		AggregatedSearchResult searchResult = SearchHelper.search(query, page, types);
		if (searchResult.getNumberOfResults() == 0 && types == null) {
			System.out.println("Attempting search against timeseries as no results found for: " + query);
			Timeseries timeseries = SearchHelper.searchCdid(query);
			// if still no results then use term suggester for auto correct
			if (timeseries == null) {
				System.out.println("No results found from timeseries so using suggestions for: " + query);
				AggregatedSearchResult suggestionResult = SearchHelper.searchSuggestions(query, page, types);
				if (suggestionResult != null) {
					return suggestionResult;
				}
			} else {
				return timeseries;
			}
		}
		return searchResult;
	}
	
	public Object autoComplete(String query) {
		return SearchHelper.autocomplete(query);
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
		if (query.length() > 100) {
			throw new RuntimeException("Search query contains too many characters");
		}
		return query;
	}

}
