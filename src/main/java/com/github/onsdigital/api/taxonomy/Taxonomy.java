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
	public void serveTemplate(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Taxonomy.class;
		URI uri = URI.create(request.getRequestURI());
		Data data = getNodeData(uri);
		String templateResourceName;
		if (StringUtils.equals(data.level, "t2"))
			templateResourceName = "/files/t2.html";
		else
			templateResourceName = "/files/t3.html";
		try (InputStream html = ResourceUtils.getStream(templateResourceName)) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF8");
			IOUtils.copy(html, response.getOutputStream());
		}
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
		String jsonResourceName = "/files" + join(uri.getPath(), "data.json");
		Data data;
		try (InputStream json = ResourceUtils.getStream(jsonResourceName)) {
			data = Serialiser.deserialise(json, Data.class);
		}

		return data;
	}

	/**
	 * We could optimise this by using StringBuilder.
	 * 
	 * @param path
	 * @param file
	 * @return
	 */
	private static String join(String path, String file) {
		String result;
		if (!StringUtils.endsWith(path, "/")) {
			result = path + "/" + file;
		} else {
			result = path + file;
		}
		if (!StringUtils.startsWith(result, "/")) {
			result = "/" + result;
		}
		return result;
	}
}
