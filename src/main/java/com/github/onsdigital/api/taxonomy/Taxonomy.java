package com.github.onsdigital.api.taxonomy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.json.Data;
import com.github.onsdigital.json.TaxonomyNode;

public abstract class Taxonomy {

	@GET
	public void goToIndex(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		URI uri = URI.create(request.getRequestURI());
		Data data = getNodeData(uri);

		// Serve T2 or T3 depending on whether there are children:
		String templateResourceName;
		if (data.children == null || data.children.size() == 0) {
			// t3
			templateResourceName = "files/t3.html";
		} else {
			// t2
			templateResourceName = "files/t2.html";
		}
		try (InputStream html = Taxonomy.class.getClassLoader()
				.getResourceAsStream(templateResourceName)) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF8");
			IOUtils.copy(html, response.getOutputStream());
		}

		// response.sendRedirect(join(uri.getPath(), "index.html"));
	}

	/**
	 * Accesses the data.json file for this node.
	 * 
	 * @param uri
	 * @return A {@link TaxonomyNode} representation.
	 * @throws IOException
	 */
	private Data getNodeData(URI uri) throws IOException {

		// Get the data for this node:
		String jsonResourceName = "files" + join(uri.getPath(), "data.json");
		Data data;
		try (InputStream json = ResourceUtils.getStream(jsonResourceName)) {
			data = Serialiser.deserialise(json, Data.class);
		}

		return data;
	}

	private static String join(String path, String file) {
		if (!StringUtils.endsWith(path, "/")) {
			return path + "/" + file;
		}
		return path + file;
	}
}
