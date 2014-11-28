package com.github.onsdigital.api;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Home;

public class HomePage implements Home {

	@Override
	public Object get(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = HomePage.class;

		// Cache for five minutes - helps if someone refreshes the page:
		response.setHeader("cache-control", "public, max-age=300");
		response.setCharacterEncoding("UTF8");
		response.setContentType("text/html");
		// Serve up the angular template
		try (Reader index = ResourceUtils.getReader("/files/index.html")) {
			IOUtils.copy(index, response.getWriter());
		}

		// response.sendRedirect("/home");

		return null;
	}

}
