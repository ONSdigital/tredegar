package com.github.onsdigital.api;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Home;

public class HomePage implements Home {
	@Override
	public Object get(HttpServletRequest reqquest, HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = HomePage.class;
		try (InputStream html = ResourceUtils.getStream("/files/index.html")) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF8");
			IOUtils.copy(html, response.getOutputStream());
		}

		return null;
	}

}
