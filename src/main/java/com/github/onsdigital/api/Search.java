package com.github.onsdigital.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.interfaces.Endpoint;
import com.github.onsdigital.util.SearchUtil;

@Endpoint
public class Search {
	final static String jsonMime = "application/json";

	@GET
	public Object get(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String query = request.getParameter("q");

		if (StringUtils.isEmpty(query)) {
			throw new RuntimeException("No search query");
		}

		return SearchUtil.search(query);

	}
}
