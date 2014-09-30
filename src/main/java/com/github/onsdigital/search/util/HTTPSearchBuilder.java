package com.github.onsdigital.search.util;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search.Builder;

import com.github.onsdigital.search.client.base.SearchBuilder;
import com.google.gson.JsonObject;

/**
 * 
 * Wrapper for JEST {@link Builder}
 * 
 * @author Bren
 *
 */
public class HTTPSearchBuilder implements SearchBuilder {

	private Builder builder;
	private JestClient client;

	public HTTPSearchBuilder(JestClient client, String query) {
		this.builder = new Builder(query);
		this.client = client;
	}

	@Override
	public SearchBuilder setIndices(String... indices) {
		for (String index : indices) {
			this.builder.addIndex(index);
		}
		return this;
	}

	@Override
	public SearchBuilder setTypes(String... types) {
		for (String type : types) {
			this.builder.addType(type);
		}
		return this;
	}

	@Override
	public JsonObject execute() throws Exception {
		io.searchbox.core.SearchResult result = client.execute(builder.build());
		if (result.isSucceeded() == false) {
			throw new RuntimeException(result.getErrorMessage());
		}

		return result.getJsonObject();

	}

}
