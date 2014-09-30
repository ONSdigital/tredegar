package com.github.onsdigital.search.client.base;

import com.google.gson.JsonObject;

public interface SearchBuilder {

	SearchBuilder setIndices(String... indices);

	SearchBuilder setTypes(String... types);

	JsonObject execute() throws Exception;

}
