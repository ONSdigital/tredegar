package com.github.onsdigital.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.prerender.PreGenerated;
import com.github.onsdigital.prerender.PrerenderIo;
import org.apache.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.net.URL;

@Api
public class Static {

	@GET
	public void serveIndex(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		// Set up the response:
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF8");
		// Cache for an 6 hours. These pages will only be updated daily, so this
		// should ensure a sensible reload window:
		response.setHeader("cache-control", "public, max-age=3600");
		
		URL url = new URL(request.getRequestURL().toString());
		//If static search convert get parameter to path parameter and redirect
		if (url.getPath().equals("/static/search")) {
			String searchTerm = request.getParameter("q");
			response.sendRedirect("/static/search/" + searchTerm);
			return;
		}

		
		if (!PrerenderIo.handle(request, response) && !PreGenerated.handle(request, response)) {
			response.setStatus(HttpStatus.SC_NOT_FOUND);
			response.getWriter().write("Apologies, for some reason it seems that page can't be generated.");
		}
	}
}
