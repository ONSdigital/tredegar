package com.github.onsdigital.api.taxonomy;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.data.DataService;

@Endpoint
public class Data {

	static boolean validated;

	@GET
	public Map<String, String> getData(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Data.class;

		// Validate all Json so that we get a warning if
		// there's an issue with a file that's been edited.
		if (!validated) {
			Validator.validate();
			validated = true;
		}

		// Look for a data file:
		InputStream data = DataService.getDataStream(request.getRequestURI());

		// Output directly to the response
		// (rather than deserialise and re-serialise)
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		if (data != null) {
			try (InputStream input = data) {
				IOUtils.copy(input, response.getOutputStream());
			}
			return null;
		} else {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			Map<String, String> error404 = new HashMap<>();
			error404.put("message",
					"These are not the data you are looking for.");
			error404.put("status", String.valueOf(HttpStatus.NOT_FOUND_404));
			return error404;
		}
	}

}
