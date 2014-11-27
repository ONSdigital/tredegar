package com.github.onsdigital.errors;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.io.IOUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.framework.NotFound;

@Endpoint
public class Error404 implements NotFound {

	@GET
	public void demo(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res);
	}

	@Override
	public Object handle(HttpServletRequest req, HttpServletResponse res) throws IOException {

		// Ensure ResourceUtils "sees" the reloadable classloader in
		// development:
		ResourceUtils.classLoaderClass = Error404.class;
		try (Reader input = ResourceUtils.getReader("/files/404.html")) {
			res.setContentType("text/html");
			res.setCharacterEncoding("UTF8");
			IOUtils.copy(input, res.getWriter());
		}
		return null;
	}

}
