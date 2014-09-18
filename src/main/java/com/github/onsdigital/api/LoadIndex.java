package com.github.onsdigital.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.elasticsearch.client.Client;

import com.github.davidcarboni.restolino.interfaces.Endpoint;
import com.github.onsdigital.util.ScanFileSystem;
import com.github.onsdigital.util.SearchConnectionManager;

@Endpoint
public class LoadIndex {
	@GET
	public void get(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		Client client = null;
		try {
			client = SearchConnectionManager.getClient();

			List<String> fileNames = getFileNames();
			if (fileNames.isEmpty()) {
				System.out
						.println("No files located during system scan, nothing will be indexed");
			}

			indexDocuments(client, fileNames);

		} finally {
			SearchConnectionManager.close(client);
		}

	}

	private void indexDocuments(Client client, List<String> fileNames)
			throws IOException {
		int idCounter = 0;
		for (String fileName : fileNames) {
			idCounter++;
			client.prepareIndex("publication", "bulletin",
					String.valueOf(idCounter))
					.setSource(
							jsonBuilder().startObject()
									.field("title", "title" + idCounter)
									.field("tags", "tags" + idCounter)
									.field("theme", fileName).endObject())
					.execute().actionGet();
		}
	}

	private List<String> getFileNames() throws IOException {
		List<String> fileNames = new ArrayList<String>();
		String rootSearch = "src/main/resources";
		final Path rootDir = Paths.get(rootSearch);
		fileNames = ScanFileSystem.getFileNames(fileNames, rootDir);
		return fileNames;
	}
}
