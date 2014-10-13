package com.github.onsdigital.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Home;

public class HomePage implements Home {

	@Override
	public Object get(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = HomePage.class;
		response.sendRedirect("/home");
		return null;
	}

}
