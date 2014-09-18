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
		SearchConnectionManager connectionManager = new SearchConnectionManager(
				"elasticsearch", "localhost", 9300);
		try {
			String query = request.getParameter("q");
			if (StringUtils.isEmpty(query)) {
				throw new RuntimeException("No search query provided");
			}
			String type = request.getParameter("type");

			ONSQueryBuilder queryBuilder = new ONSQueryBuilder("publication")
					.setType(type).setQuery(query);
			connectionManager.openConnection();

			ElasticSearchUtil searchUtil = new ElasticSearchUtil(
					connectionManager);

			return searchUtil.search(queryBuilder);
		} finally {
			connectionManager.closeConnection();
		}

	}
}
