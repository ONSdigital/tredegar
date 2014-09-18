package com.github.onsdigital.api;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.interfaces.Endpoint;

@Endpoint
public class Taxonomy {

	@GET
	public void goToIndex(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		URI uri = URI.create(request.getRequestURI());
		response.sendRedirect(join(uri.getPath(), "index.html"));
	}

	private static String join(String path, String file) {
		if (!StringUtils.endsWith(path, "/")) {
			return path + "/" + file;
		}
		return path + file;
	}
}
