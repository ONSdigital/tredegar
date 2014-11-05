package com.github.onsdigital.errors;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.NotFound;

public class Error404 implements NotFound {

	@Override
	public Object handle(HttpServletRequest req, HttpServletResponse res) throws IOException {

		try (Reader input = ResourceUtils.getReader("/files/404.html")) {
			res.setCharacterEncoding("UTF8");
			IOUtils.copy(input, res.getWriter());
		}
		return null;
	}

}
