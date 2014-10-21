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
import com.github.davidcarboni.restolino.framework.Endpoint;

@Endpoint
public class Static {

	@GET
	public void serveIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Static.class;
		URI uri = URI.create(request.getRequestURI());
		String path = uri.getPath().toLowerCase();
		path = join(path, "index.html");
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		path = "/files" + path;

		try (InputStream html = ResourceUtils.getStream(path)) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF8");
			IOUtils.copy(html, response.getOutputStream());
		}
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
