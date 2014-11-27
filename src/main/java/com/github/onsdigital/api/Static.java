package com.github.onsdigital.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.http.HttpStatus;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.prerender.PreGenerated;
import com.github.onsdigital.prerender.PrerenderIo;

@Endpoint
public class Static {

	@GET
	public void serveIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		// Set up the response:
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF8");
		// Cache for an 6 hours. These pages will only be updated daily, so this
		// should ensure a sensible reload window:
		response.setHeader("cache-control", "public, max-age=3600");

		if (!PrerenderIo.handle(request, response) && !PreGenerated.handle(request, response)) {
			response.setStatus(HttpStatus.SC_NOT_FOUND);
			response.getWriter().write("Apologies, for some reason it seems that page can't be generated.");
		}
	}
}
