package com.github.onsdigital.api.taxonomy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import com.github.davidcarboni.restolino.framework.Endpoint;

/**
 * This endpoint has been retired and only exists as a convenience redirect to
 * <code>/static/</code>.
 * 
 * @author david
 *
 */
@Endpoint
@Deprecated
public class Home {

	@GET
	public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = request.getRequestURL().toString();
		response.sendRedirect(url.replace("/home/", "/static/"));
	}

}
