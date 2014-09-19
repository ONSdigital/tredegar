package com.github.onsdigital.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.interfaces.Endpoint;
import com.github.onsdigital.util.ElasticSearchUtil;
import com.github.onsdigital.util.ONSQueryBuilder;
import com.github.onsdigital.util.SearchConnectionManager;

@Endpoint
public class Search {
	final static String jsonMime = "application/json";

	@GET
	public Object get(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		return search(extractQuery(request), extractPage(request),
				request.getParameter("type"));

	}

	private Object search(String query, int page, String type) {
		SearchConnectionManager connectionManager = new SearchConnectionManager(
				"elasticsearch", "localhost", 9300);
		try {

			ONSQueryBuilder queryBuilder = new ONSQueryBuilder("ons")
					.setType(type).setPage(page).setQuery(query)
					.setFields("title", "path");
			connectionManager.openConnection();

			ElasticSearchUtil searchUtil = new ElasticSearchUtil(
					connectionManager);

			return searchUtil.search(queryBuilder);
		} finally {
			connectionManager.closeConnection();
		}
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
			throw new RuntimeException("No search query provided");
		}
		return query;
	}
}
