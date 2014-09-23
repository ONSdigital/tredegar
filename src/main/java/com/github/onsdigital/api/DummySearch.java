package com.github.onsdigital.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.bean.SearchResult;

@Endpoint
public class DummySearch {

	final static String jsonMime = "application/json";

	@GET
	public Object get(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		SearchResult result = new SearchResult();
		result.setTotalPages(100);
		result.setTook(23);
		result.setNumberOfResults(1004);
		result.setResults(buildResults());
		return result;
	}

	private List<Map<String, Object>> buildResults() {
		List<Map<String, Object>> results = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			Map<String, Object> result = new HashMap<String, Object>();

			result.put("title", "Title " + i);
			result.put("tags", "Tag " + i);
			results.add(result);
		}

		return results;

	}

}
