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
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.index.LoadIndexHelper;
import com.github.onsdigital.search.ElasticSearchServer;

/**
 * Loads up indices into the search engine
 */
@Endpoint
public class LoadIndex {

	static {
		try {
			System.out.println("Loading Search Index ...");
			loadIndex();
			System.out.println("Completed load of Search Index");
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to load Search index at startup");
		}
	}

	@GET
	public Object get(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) throws IOException {

		try {
			loadIndex();
		} catch (Exception e) {
			e.printStackTrace();
			return "LoadIndex failed";
		}
		return "LoadIndex succeeded";

	}

	public static void loadIndex() throws IOException {
		List<String> absoluteFilePaths = LoadIndexHelper.getAbsoluteFilePaths(Configuration.getTaxonomyPath());
		if (absoluteFilePaths.isEmpty()) {
			throw new IllegalStateException("No items were found for indexing");
		}
		indexDocuments(ElasticSearchServer.getClient(), absoluteFilePaths);
	}

	private static void indexDocuments(Client client, List<String> absoluteFilePaths) throws IOException {

		// System.out.println("Creating index");
		// // Disable indexing for lede field
		// XContentBuilder builder = jsonBuilder().startObject("ons")
		// .startObject("dataset").startObject("properties").startObject("lede")
		// .field("type", "string").field("index", "no").endObject()
		// .endObject().endObject().endObject();

		// Set up the synonyms
		CreateIndexRequestBuilder indexBuilder = client.admin().indices().prepareCreate("ons").setSettings(buildSettings());
		indexBuilder.execute();

		int idCounter = 0;
		for (String absoluteFilePath : absoluteFilePaths) {
			idCounter++;

			Map<String, String> documentMap = LoadIndexHelper.getDocumentMap(absoluteFilePath);
			if (documentMap != null && StringUtils.isNotEmpty(documentMap.get("title"))) {
				buildDocument(client, documentMap, idCounter);
			}
		}
	}

	private static void buildDocument(Client client, Map<String, String> documentMap, int idCounter) throws IOException {

		XContentBuilder source = jsonBuilder().startObject().field("title", documentMap.get("title")).field("url", documentMap.get("url")).field("path", documentMap.get("tags"))
				.field("lede", documentMap.get("lede")).endObject();
		client.prepareIndex(StringUtils.lowerCase("ons"), StringUtils.lowerCase(documentMap.get("type")), String.valueOf(idCounter)).setSource(source).execute().actionGet();

	}

	private static Map<String, String> buildSettings() throws IOException {
		ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();

		List<String> synonymList = getSynonyms(settingsBuilder);
		getSettingsBuilder(settingsBuilder, synonymList);

		return settingsBuilder.build().getAsMap();
	}

	private static void getSettingsBuilder(ImmutableSettings.Builder settingsBuilder, List<String> synonymList) {
		String[] synonyms = new String[synonymList.size()];
		synonymList.toArray(synonyms);

		settingsBuilder.putArray("analysis.filter.ons_synonym_filter.synonyms", synonyms);

		Map<String, String> settings = new HashMap<>();
		settings.put("analysis.analyzer.ons_synonyms.tokenizer", "standard");
		settings.put("analysis.filter.ons_synonym_filter.type", "synonym");
		settingsBuilder.put(settings);
	}

	private static List<String> getSynonyms(ImmutableSettings.Builder settingsBuilder) throws IOException {
		String[] filters = { "lowercase", "ons_synonym_filter" };
		settingsBuilder.putArray("analysis.analyzer.ons_synonyms.filter", filters);

		InputStream inputStream = LoadIndex.class.getResourceAsStream("/synonym.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		List<String> synonymList = new ArrayList<String>();
		String contents = null;
		while ((contents = reader.readLine()) != null) {
			synonymList.add(contents);
		}
		return synonymList;
	}

}
