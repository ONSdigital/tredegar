package com.github.onsdigital.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.search.ElasticSearchServer;
import com.github.onsdigital.util.LoadIndexHelper;

/**
 * Loads up indices into the search engine
 */
@Endpoint
public class LoadIndex {

	@GET
	public void get(@Context HttpServletRequest httpServletRequest,
			@Context HttpServletResponse httpServletResponse)
			throws IOException, Exception {

		List<String> absoluteFilePaths = LoadIndexHelper.getAbsoluteFilePaths();
		if (absoluteFilePaths.isEmpty()) {
			System.out
					.println("No files located during system scan, nothing will be indexed");
		}

		indexDocuments(ElasticSearchServer.getClient(), absoluteFilePaths);

	}

	// private void indexDocuments(Client client, List<String>
	// absoluteFilePaths)
	// throws IOException {
	//
	// int idCounter = 0;
	// for (String absoluteFilePath : absoluteFilePaths) {
	// idCounter++;
	//
	// System.out.println("LoadIndex submitting record to index: "
	// + absoluteFilePath);
	// buildAndSubmitJson(client,
	// LoadIndexHelper.getDocumentMap(absoluteFilePath), idCounter);
	// }
	// }
	//
	// private void buildAndSubmitJson(Client client,
	// Map<String, String> documentMap, int idCounter) throws IOException {
	//
	// client.prepareIndex(StringUtils.lowerCase("ons"),
	// StringUtils.lowerCase(documentMap.get("type")),
	// String.valueOf(idCounter))
	// .setSource(
	// jsonBuilder().startObject()
	// .field("title", documentMap.get("title"))
	// .field("url", documentMap.get("url"))
	// .field("path", documentMap.get("tags"))
	// .endObject()).execute().actionGet();
	// }

	private void indexDocuments(Client client, List<String> absoluteFilePaths)
			throws Exception {

		buildSettings(client);

		int idCounter = 0;
		for (String absoluteFilePath : absoluteFilePaths) {
			idCounter++;

			System.out
					.println("LoadIndex about to submit: " + absoluteFilePath);
			buildDocument(client,
					LoadIndexHelper.getDocumentMap(absoluteFilePath), idCounter);
		}
	}

	private void buildDocument(Client client, Map<String, String> documentMap,
			int idCounter) throws Exception {

		String source = jsonBuilder().startObject()
				.field("title", documentMap.get("title"))
				.field("url", documentMap.get("url"))
				.field("path", documentMap.get("tags")).endObject().string();

		// Index index = new Index.Builder(source).index("ons")
		// .type(documentMap.get("type")).id(String.valueOf(idCounter))
		// .build();
		client.admin().indices().prepareCreate("ons")
				.setSettings(buildSettings(client)).execute();

		client.prepareIndex(StringUtils.lowerCase("ons"),
				StringUtils.lowerCase(documentMap.get("type")),
				String.valueOf(idCounter))
				.setSource(
						jsonBuilder().startObject()
								.field("title", documentMap.get("title"))
								.field("url", documentMap.get("url"))
								.field("path", documentMap.get("tags"))
								.endObject()).execute().actionGet();
	}

	private Map<String, String> buildSettings(Client client) throws Exception {
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings
				.settingsBuilder();

		String[] filters = { "lowercase", "ons_synonym_filter" };
		settingsBuilder.putArray("analysis.analyzer.ons_synonyms.filter",
				filters);

		InputStream inputStream = getClass()
				.getResourceAsStream("/synonym.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		List<String> synonymList = new ArrayList<String>();
		String contents = null;
		while ((contents = reader.readLine()) != null) {
			synonymList.add(contents);
		}
		String[] synonyms = new String[synonymList.size()];
		synonymList.toArray(synonyms);

		settingsBuilder.putArray("analysis.filter.ons_synonym_filter.synonyms",
				synonyms);

		Map<String, String> settings = new HashMap<>();
		settings.put("analysis.analyzer.ons_synonyms.tokenizer", "standard");
		settings.put("analysis.filter.ons_synonym_filter.type", "synonym");
		settingsBuilder.put(settings);

		return settingsBuilder.build().getAsMap();
	}

}
