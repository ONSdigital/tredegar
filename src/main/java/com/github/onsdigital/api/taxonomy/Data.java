package com.github.onsdigital.api.taxonomy;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.configuration.Configuration;

@Endpoint
public class Data {

	@GET
	public void getData(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Data.class;

		// Standardise the path:
		URI uri = URI.create(request.getRequestURI());
		String uriPath = cleanPath(uri);
		Path taxonomy = FileSystems.getDefault().getPath(
				Configuration.getTaxonomyPath());

		// Look for a data.json file, or
		// fall back to adding a .json file extension
		Path data = taxonomy.resolve(uriPath).resolve("data.json");
		if (!Files.exists(data)) {
			data = taxonomy.resolve(uriPath + ".json");
		}

		// Output directly to the response
		// (rather than deserialise and re-serialise)
		if (Files.exists(data)) {
			System.out.println("found");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF8");
			IOUtils.copy(Files.newInputStream(data), response.getOutputStream());
		} else {
			System.out.println("not found");
			response.setStatus(HttpStatus.NOT_FOUND_404);
		}
	}

	/**
	 * @param uri
	 *            The URI to get a standardised path from.
	 * @return The URI path, lowercasted, without the endpoint name or trailing
	 *         slash.
	 */
	String cleanPath(URI uri) {

		// It would be nice to use StringBuilder,
		// but it doesn't have the manipulation methods we need
		String result = uri.getPath();

		// Remove slashes:
		if (result.startsWith("/"))
			result = result.substring(1);
		if (result.endsWith("/"))
			result = result.substring(0, result.length() - 1);

		// Remove endpoint name:
		String endpointName = getClass().getSimpleName().toLowerCase() + "/";
		if (result.startsWith(endpointName))
			result = result.substring(endpointName.length());

		// Lowercase
		result = result.toLowerCase();

		return result;
	}
}
