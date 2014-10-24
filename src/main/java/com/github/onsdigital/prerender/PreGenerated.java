package com.github.onsdigital.prerender;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.ResourceUtils;

public class PreGenerated {

	public static boolean handle(HttpServletRequest request, HttpServletResponse response) {
		boolean result;

		URI requestUri = URI.create(request.getRequestURI());

		// Construct the path to the (potential) locally-generated file:
		String path = requestUri.getPath().toLowerCase();
		path = join(path, "index.html");
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		path = "/files" + path;

		// Attempt to serve the file if it exists:
		try (InputStream html = ResourceUtils.getStream(path)) {
			IOUtils.copy(html, response.getOutputStream());
			result = true;
		} catch (IOException e) {
			result = false;
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
