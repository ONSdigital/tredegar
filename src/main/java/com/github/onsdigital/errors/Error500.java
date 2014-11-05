package com.github.onsdigital.errors;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.Boom;

public class Error500 implements Boom {

	@Override
	public Object handle(HttpServletRequest req, HttpServletResponse res, RequestHandler requestHandler, Throwable t) throws IOException {

		System.out.println("Error in " + requestHandler.endpointClass.getName() + " (" + requestHandler.method.getName() + ")");
		System.out.println(ExceptionUtils.getStackTrace(t));
		try (Reader input = ResourceUtils.getReader("/files/500.html")) {
			res.setCharacterEncoding("UTF8");
			IOUtils.copy(input, res.getWriter());
		}
		return null;
	}

}
