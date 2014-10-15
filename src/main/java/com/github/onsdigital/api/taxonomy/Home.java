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
import com.github.onsdigital.json.taxonomy.TaxonomyNode;
import com.github.onsdigital.json.timeseries.TimeSeries;

@Endpoint
public class Home {

	public Home() {
		Serialiser.getBuilder().setPrettyPrinting();
	}

	@GET
	public Object serveTemplate(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Home.class;
		URI uri = URI.create(request.getRequestURI());
		String path = uri.getPath().toLowerCase();

		// Read the JSON:
		Object data = getData(path);
		if (isDataRequest(request)) {
			return data;
		}

		// Select the correct resource name:
		String templateResourceName = null;
		if (TaxonomyNode.class.isAssignableFrom(data.getClass()))
			templateResourceName = selectTaxonomyLevel((TaxonomyNode) data);
		else if (TimeSeries.class.isAssignableFrom(data.getClass()))
			templateResourceName = "/files/timeseries.html";

		// Output the template to the response:
		if (templateResourceName != null) {
			try (InputStream html = ResourceUtils
					.getStream(templateResourceName)) {
				response.setContentType("text/html");
				response.setCharacterEncoding("UTF8");
				IOUtils.copy(html, response.getOutputStream());
			}
		}

		return null;
	}

	private String selectTaxonomyLevel(TaxonomyNode data) {
		String templateResourceName;

		// Handle crawler request:
		// https://developers.google.com/webmasters/ajax-crawling/
		// if (CrawlerHandler.handle(request, response))
		// return null;

		if (StringUtils.equals(data.level, "t1")) {
			templateResourceName = "/files/t1.html";
		} else if (StringUtils.equals(data.level, "t2")) {
			templateResourceName = "/files/t2.html";
		} else {
			templateResourceName = "/files/t3.html";
		}
		return templateResourceName;
	}

	/**
	 * Accesses the data.json file for this node.
	 * 
	 * @param path
	 * @return A {@link TaxonomyNode} representation.
	 * @throws IOException
	 */
	private Object getData(String path) throws IOException {

		String taxonomyPath = Configuration.getTaxonomyPath();

		// Look for a taxonomy node:
		File taxonomyNode = new File(taxonomyPath, addFile(path, "data.json"));
		if (taxonomyNode.exists()) {
			String json = FileUtils.readFileToString(taxonomyNode);
			return Serialiser.deserialise(json, TaxonomyNode.class);
		}

		// Look for timeseries data:
		File fileData = new File(taxonomyPath, addExtension(path, ".json"));
		if (fileData.exists()) {
			File parent = fileData.getParentFile();
			String json = FileUtils.readFileToString(fileData);
			if (StringUtils.equals("timeseries", parent.getName()))
				return Serialiser.deserialise(json, TimeSeries.class);
			// else the other types...
		}

		// Not something we recognise
		return null;
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
	private static String addFile(String path, String file) {
		String result;
		if (StringUtils.endsWith(path, "/")) {
			result = path + file;
		} else {
			result = path + "/" + file;
		}
		if (!StringUtils.startsWith(result, "/")) {
			result = "/" + result;
		}
		return result;
	}

	/**
	 * We could optimise this by using StringBuilder.
	 * 
	 * @param path
	 * @param file
	 * @return
	 */
	private static String addExtension(String path, String extension) {
		String result;
		if (StringUtils.endsWith(path, "/")) {
			result = path.substring(0, path.length() - 1) + extension;
		} else {
			result = path + extension;
		}
		if (!StringUtils.startsWith(result, "/")) {
			result = "/" + result;
		}
		return result;
	}
}
