package com.github.onsdigital.search.util;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.github.onsdigital.search.client.base.SearchBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * Wrapper for Elastic Search {@link SearchRequestBuilder}
 * 
 * @author Bren
 *
 */
public class TransportSearchBuilder implements SearchBuilder {

	private SearchRequestBuilder builder;

	public TransportSearchBuilder(Client client, String query) {
		// TODO; Any assertion utility for null checks ?
		if (client == null) {
			throw new IllegalArgumentException("Client can not be null");
		}
		this.builder = new SearchRequestBuilder(client);
		builder.setQuery(query);
	}

	@Override
	public SearchBuilder setIndices(String... indices) {
		this.builder.setIndices(indices);
		return this;
	}

	@Override
	public SearchBuilder setTypes(String... types) {
		this.builder.setTypes(types);
		return this;
	}

	@Override
	public JsonObject execute() throws Exception {
		XContentBuilder jsonBuilder = XContentFactory.jsonBuilder()
				.startObject();
		jsonBuilder = builder.get().toXContent(jsonBuilder,
				ToXContent.EMPTY_PARAMS);
		jsonBuilder.endObject();
		return (JsonObject) new JsonParser().parse(jsonBuilder.string());

	}
}
