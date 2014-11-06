package com.github.onsdigital.errors;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.Boom;
import com.github.davidcarboni.restolino.framework.Endpoint;

@Endpoint
public class Error500 implements Boom {

	@GET
	public void demo(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, null, new RuntimeException("Error page demonstration."));
	}

	@Override
	public Object handle(HttpServletRequest req, HttpServletResponse res, RequestHandler requestHandler, Throwable t) throws IOException {

		System.out.println(ExceptionUtils.getStackTrace(t));

		// Ensure ResourceUtils "sees" the reloadable classloader in
		// development:
		ResourceUtils.classLoaderClass = Error500.class;
		if (requestHandler != null) {
			System.out.println("Error in " + requestHandler.endpointClass.getName() + " (" + requestHandler.method.getName() + ")");
		}
		try (Reader input = ResourceUtils.getReader("/files/500.html")) {
			res.setCharacterEncoding("UTF8");
			IOUtils.copy(input, res.getWriter());
		}
		return null;
	}

}
