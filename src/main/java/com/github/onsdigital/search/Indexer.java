package com.github.onsdigital.search;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.json.ContentType;

public class Indexer {

	public static void loadIndex(Client client) throws IOException {
		List<String> absoluteFilePaths = LoadIndexHelper.getAbsoluteFilePaths(Configuration.getTaxonomyPath());
		if (absoluteFilePaths.isEmpty()) {
			throw new IllegalStateException("No items were found for indexing");
		}
		createIndex(client, absoluteFilePaths);
		indexDocuments(client, absoluteFilePaths);
	}

	private static void createIndex(Client client, List<String> absoluteFilePaths) throws IOException {

		System.out.println("Creating index ons");
		// Disable indexing for lede field

		// Set up the synonyms
		CreateIndexRequestBuilder indexBuilder = client.admin().indices().prepareCreate("ons").setSettings(buildSettings());
		System.out.println("Adding document mappings");

		indexBuilder.addMapping(ContentType.dataset.toString(), getMappingProperties(ContentType.dataset.toString()));
		indexBuilder.addMapping(ContentType.home.toString(), getMappingProperties(ContentType.home.toString()));
		indexBuilder.addMapping(ContentType.bulletin.toString(), getMappingProperties(ContentType.bulletin.toString()));
		indexBuilder.addMapping(ContentType.article.toString(), getMappingProperties(ContentType.article.toString()));
		indexBuilder.addMapping(ContentType.methodology.toString(), getMappingProperties(ContentType.methodology.toString()));

		System.out.println("Adding timeseries mappings");
		indexBuilder.addMapping(ContentType.timeseries.toString(), getTimeseriesMappingProperties(ContentType.timeseries.toString()));

		indexBuilder.execute();
	}

	// Mapping for field properties. To decide which field will be indexed
	private static XContentBuilder getMappingProperties(String type) throws IOException {

		XContentBuilder builder = jsonBuilder().startObject().startObject(type).startObject("properties");
		builder.startObject("lede").field("type", "string").field("index", "no").endObject();
		builder.startObject("title").field("type", "string").field("index", "analyzed").endObject();
		builder.startObject("url").field("type", "string").field("index", "analyzed").endObject();
		builder.startObject("path").field("type", "string").field("index", "analyzed").endObject();
		builder.endObject().endObject().endObject();
		return builder;
	}

	// Mapping for timeseries field properties.
	private static XContentBuilder getTimeseriesMappingProperties(String type) throws IOException {
		XContentBuilder builder = jsonBuilder().startObject().startObject(type).startObject("properties");
		// cdid not analyzed for exact match
		builder.startObject("cdid").field("type", "string").field("index", "analyzed").endObject();
		builder.startObject("title").field("type", "string").field("index", "analyzed").endObject();
		builder.startObject("url").field("type", "string").field("index", "analyzed").endObject();
		builder.startObject("path").field("type", "string").field("index", "analyzed").endObject();
		builder.endObject().endObject().endObject();
		System.out.println(builder.string() + "\n\n");
		return builder;
	}

	private static void indexDocuments(Client client, List<String> absoluteFilePaths) throws IOException {
		AtomicInteger idCounter = new AtomicInteger();
		for (String absoluteFilePath : absoluteFilePaths) {

			Map<String, String> documentMap = LoadIndexHelper.getDocumentMap(absoluteFilePath);
			if (documentMap != null && StringUtils.isNotEmpty(documentMap.get("title"))) {
				if (documentMap.get("type").equals(ContentType.timeseries.toString())) {
					buildTimeseries(client, documentMap, idCounter.getAndIncrement());
				} else {
					buildDocument(client, documentMap, idCounter.getAndIncrement());
				}
			}
		}
	}

	private static void buildTimeseries(Client client, Map<String, String> documentMap, int idCounter) throws IOException {
		XContentBuilder source = jsonBuilder().startObject().field("title", documentMap.get("title")).field("url", documentMap.get("url")).field("path", documentMap.get("tags"))
				.field("cdid", documentMap.get("cdid")).endObject();
		build(client, documentMap, idCounter, source);
	}

	private static void buildDocument(Client client, Map<String, String> documentMap, int idCounter) throws IOException {

		XContentBuilder source = jsonBuilder().startObject().field("title", documentMap.get("title")).field("url", documentMap.get("url")).field("path", documentMap.get("tags"))
				.field("lede", documentMap.get("lede")).endObject();
		build(client, documentMap, idCounter, source);
	}

	private static void build(Client client, Map<String, String> documentMap, int idCounter, XContentBuilder source) {
		String name = "ons";
		String type = StringUtils.lowerCase(documentMap.get("type"));
		String id = String.valueOf(idCounter);
		IndexRequestBuilder index = client.prepareIndex(name, type, id);
		index.setSource(source);
		ListenableActionFuture<IndexResponse> execution = index.execute();
		execution.actionGet();
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

		InputStream inputStream = Indexer.class.getResourceAsStream("/synonym.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		List<String> synonymList = new ArrayList<String>();
		String contents = null;
		while ((contents = reader.readLine()) != null) {
			synonymList.add(contents);
		}
		reader.close();
		return synonymList;
	}
}
