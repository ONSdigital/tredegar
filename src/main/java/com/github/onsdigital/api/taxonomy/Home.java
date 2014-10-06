package com.github.onsdigital.api.taxonomy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.json.Data;
import com.github.onsdigital.json.TaxonomyNode;

@Endpoint
public class Home {

	@GET
	public Object serveTemplate(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Home.class;
		URI uri = URI.create(request.getRequestURI());
		String path = uri.getPath().toLowerCase();
		String templateResourceName;

		Data data = getNodeData(path);
		if (isDataRequest(request)) {
			return data;
		}
		if (StringUtils.equals(data.level, "t1")) {
			templateResourceName = "/files/t1.html";
		} else if (StringUtils.equals(data.level, "t2")) {
			templateResourceName = "/files/t2.html";
		} else {
			templateResourceName = "/files/t3.html";
		}
		try (InputStream html = ResourceUtils.getStream(templateResourceName)) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF8");
			IOUtils.copy(html, response.getOutputStream());
		}
		return null;
	}

	/**
	 * Accesses the data.json file for this node.
	 * 
	 * @param path
	 * @return A {@link TaxonomyNode} representation.
	 * @throws IOException
	 */
	private Data getNodeData(String path) throws IOException {

		String taxonomyPath = Configuration.getTaxonomyPath();
		// Get the data for this node:
		String json = FileUtils.readFileToString(new File(taxonomyPath + join(path, "data.json")));
		return Serialiser.deserialise(json, Data.class);
	}

	private boolean isDataRequest(HttpServletRequest request) {
		return request.getParameter("data") != null;
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
