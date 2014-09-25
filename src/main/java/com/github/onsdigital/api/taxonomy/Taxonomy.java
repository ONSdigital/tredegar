package com.github.onsdigital.api.taxonomy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.json.Serialiser;

public abstract class Taxonomy {

	static class TaxonomyNode {
		String name;
		String fileName;
		List<TaxonomyNode> breadcrumb = new ArrayList<>();
		List<TaxonomyNode> children = new ArrayList<>();
	}

	@GET
	public void goToIndex(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {
		URI uri = URI.create(request.getRequestURI());

		// Get the data for this node:
		String jsonResourceName = "files" + join(uri.getPath(), "data.json");
		TaxonomyNode taxonomyNode;
		try (InputStream json = Taxonomy.class.getClassLoader()
				.getResourceAsStream(jsonResourceName)) {
			taxonomyNode = Serialiser.deserialise(json, TaxonomyNode.class);
		}

		// Serve T2 or T3 depending on whether there are children:
		String templateResourceName;
		if (taxonomyNode.children == null || taxonomyNode.children.size() == 0) {
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

	private static String join(String path, String file) {
		if (!StringUtils.endsWith(path, "/")) {
			return path + "/" + file;
		}
		return path + file;
	}
}
