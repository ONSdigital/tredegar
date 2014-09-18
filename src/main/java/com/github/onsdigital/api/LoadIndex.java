package com.github.onsdigital.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;

import com.github.davidcarboni.restolino.interfaces.Endpoint;
import com.github.onsdigital.util.LoadIndexHelper;
import com.github.onsdigital.util.SearchConnectionManager;

/**
 * Loads up indices into the search engine
 */
@Endpoint
public class LoadIndex {

	@GET
	public void get(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		SearchConnectionManager manager = new SearchConnectionManager(
				"elasticsearch", "localhost", 9300);
		try {
			manager.openConnection();

			List<String> fileNames = LoadIndexHelper.getFileNames();
			if (fileNames.isEmpty()) {
				System.out
						.println("No files located during system scan, nothing will be indexed");
			}

			indexDocuments(manager, fileNames);

		} finally {
			manager.closeConnection();
		}

	}

	private void indexDocuments(SearchConnectionManager manager,
			List<String> fileNames) throws IOException {

		int idCounter = 0;
		for (String fileName : fileNames) {
			idCounter++;

			String[] splitIndexArgs = LoadIndexHelper.getTaxonomy(fileName);
			String index = splitIndexArgs[1];
			String type = splitIndexArgs[2];

			Client client = manager.getClient();
			buildAndSubmitJson(client, idCounter, fileName, index, type);
		}
	}

	private void buildAndSubmitJson(Client client, int idCounter,
			String fileName, String index, String type) throws IOException {

		client.prepareIndex(StringUtils.lowerCase(index),
				StringUtils.lowerCase(type), String.valueOf(idCounter))
				.setSource(
						jsonBuilder().startObject()
								.field("title", "title" + idCounter)
								.field("tags", "tags" + idCounter)
								.field("theme", fileName).endObject())
				.execute().actionGet();
	}
}
