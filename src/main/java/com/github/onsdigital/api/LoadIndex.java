package com.github.onsdigital.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;

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
			throws IOException {

		List<String> absoluteFilePaths = LoadIndexHelper.getAbsoluteFilePaths();
		if (absoluteFilePaths.isEmpty()) {
			System.out
					.println("No files located during system scan, nothing will be indexed");
		}

		indexDocuments(ElasticSearchServer.getClient(), absoluteFilePaths);

	}

	private void indexDocuments(Client client, List<String> absoluteFilePaths)
			throws IOException {

		int idCounter = 0;
		for (String absoluteFilePath : absoluteFilePaths) {
			idCounter++;

			System.out.println("LoadIndex submitting record to index: "
					+ absoluteFilePath);
			buildAndSubmitJson(client,
					LoadIndexHelper.getDocumentMap(absoluteFilePath), idCounter);
		}
	}

	private void buildAndSubmitJson(Client client,
			Map<String, String> documentMap, int idCounter) throws IOException {

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
}
