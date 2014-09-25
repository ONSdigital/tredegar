package com.github.onsdigital.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.util.LoadIndexHelper;

/**
 * Loads up indices into the search engine
 */
@Endpoint
public class LoadIndexHTTP {
	private static final String DEFAULT_URL = "http://localhost:9200";
	private static final String BONSAI_URL = "BONSAI_URL";

	@GET
	public void get(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse)
			throws Exception {

		// Construct a new Jest client according to configuration via factory
		String connectionUrl = System.getenv(BONSAI_URL);
		if (StringUtils.isEmpty(connectionUrl)) {
			connectionUrl = DEFAULT_URL;
		}
		System.out.println("LoadIndexHTTP using connectionUrl: " + connectionUrl);

		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(connectionUrl).multiThreaded(true).build());
		JestClient client = factory.getObject();

		List<String> absoluteFilePaths = LoadIndexHelper.getAbsoluteFilePaths();
		if (absoluteFilePaths.isEmpty()) {
			System.out.println("No files located during system scan, nothing will be indexed");
		}

		indexDocuments(client, absoluteFilePaths);
	}

	private void indexDocuments(JestClient client, List<String> absoluteFilePaths) throws Exception {

		int idCounter = 0;
		for (String absoluteFilePath : absoluteFilePaths) {
			idCounter++;

			System.out.println("LoadIndexHTTP about to submit: " + absoluteFilePath);
			buildAndSubmitJson(client, LoadIndexHelper.getDocumentMap(absoluteFilePath), idCounter);
		}
	}

	private void buildAndSubmitJson(JestClient client, Map<String, String> documentMap, int idCounter) throws Exception {

		client.execute(new CreateIndex.Builder("ons").build());
		String source = jsonBuilder().startObject().field("title", documentMap.get("title"))
				.field("url", documentMap.get("url")).field("path", documentMap.get("tags")).endObject().string();

		Index index = new Index.Builder(source).index("ons").type(documentMap.get("type"))
				.id(String.valueOf(idCounter)).build();
		client.execute(index);
	}
}
