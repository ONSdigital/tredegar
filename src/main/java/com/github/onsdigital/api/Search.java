package com.github.onsdigital.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.search.suggest.Suggest;
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
	public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		return search(extractQuery(request), extractPage(request), request.getParameter("type"));
	}

	private Object search(String query, int page, String type) throws Exception {
		ONSQueryBuilder queryBuilder = new ONSQueryBuilder("ons").setType(type)
				.setPage(page).setSearchTerm(query)
				.setFields(getTitle(), "path");
		SearchResult searchResult = new SearchHelper(ElasticSearchServer.getClient()).search(queryBuilder);
		if (searchResult.getNumberOfResults() == 0 && type == null) {//If type is set don't search for timeseries
			System.out.println("Attempting search against timeseries type as no results found for: " + query);
			ONSQueryBuilder timeSeriesQueryBuilder = new ONSQueryBuilder("ons").setType(ContentType.timeseries.name()).setPage(page).setSearchTerm(query).setFields(getTitle(), "path");
			searchResult = new SearchHelper(ElasticSearchServer.getClient()).search(timeSeriesQueryBuilder);

			// if still no results then use term suggester for autocorrect
			if (searchResult.getNumberOfResults() == 0) {
				System.out.println("No results found from timeseries so using suggestions ...");
				TermSuggestionBuilder termSuggestionBuilder = new TermSuggestionBuilder("autocorrect").field("title").text(query).size(1);
				SuggestResponse suggestResponse = ElasticSearchServer.getClient().prepareSuggest("ons").addSuggestion(termSuggestionBuilder).execute().actionGet();

				StringBuffer suggestionsBuffer = new StringBuffer();
				for (Suggest.Suggestion.Entry entry : suggestResponse.getSuggest().getSuggestion("autocorrect").getEntries()) {
					if (!entry.getOptions().isEmpty()) {
						Suggest.Suggestion.Entry.Option option = (Suggest.Suggestion.Entry.Option) entry.getOptions().get(0);
						System.out.println("option.text: " + option.getText());
						suggestionsBuffer.append(option.getText());
						suggestionsBuffer.append(" ");
					}
				}
				if (StringUtils.isEmpty(suggestionsBuffer.toString())) {
					System.out.println("All search steps failed to discover suitable match");
				} else {
					ONSQueryBuilder suggestionsQueryBuilder = new ONSQueryBuilder("ons").setType(type).setPage(page).setSearchTerm(suggestionsBuffer.toString()).setFields(getTitle(), "path");
					searchResult = new SearchHelper(ElasticSearchServer.getClient()).search(suggestionsQueryBuilder);
					searchResult.setSuggestionBasedResult(true);
					searchResult.setSuggestion(suggestionsBuffer.toString());
				}
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
			throw new RuntimeException("Search query can only contain alphanumeric characters");
		}

		return query;
	}

	private String getTitle() {
		String titleBoost = (String) ElasticSearchProperties.INSTANCE.getProperty(TITLE);
		return ElasticSearchFieldUtil.getBoost(TITLE, titleBoost);
	}
}
