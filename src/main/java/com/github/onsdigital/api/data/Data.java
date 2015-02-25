package com.github.onsdigital.api.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jetty.http.HttpStatus;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.util.HostHelper;
import com.github.onsdigital.util.Validator;
import sun.misc.BASE64Decoder;

@Endpoint
public class Data {

	static boolean validated;

	@GET
	public Map<String, String> getData(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		// Ensures ResourceUtils gets the right classloader when running
		// reloadable in development:
		ResourceUtils.classLoaderClass = Data.class;

		// Validate all Json so that we get a warning if
		// there's an issue with a file that's been edited.
		if (!validated) {
			Validator.validate();
			validated = true;
		}

		// Add a five-minute cache time to static files to reduce round-trips to
		// the server and increase performance whilst still allowing the system
		// to be updated quite promptly if necessary:
		if (!HostHelper.isLocalhost(request)) {
			response.addHeader("cache-control", "public, max-age=300");
		}

		// Look for a data file:
        GitHubClient client = new GitHubClient();
        client.setCredentials("carlhuk", "thisisatemp1");
        RepositoryService service = new RepositoryService(client);
        Repository repository = service.getRepository("ONSDigital", "nightingale");
        ContentsService contentsService = new ContentsService(client);

        System.out.println(request.getRequestURI());

        String path = request.getRequestURI().replace("data", "taxonomy");
        if (!path.endsWith("/"))
        {
            path += "/";
        }

        List<RepositoryContents> contents = contentsService.getContents(
                repository, path + "data.json"); //"/taxonomy/data.json");
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = decoder.decodeBuffer(contents.get(0).getContent());
        String json = new String(decodedBytes);
        StringReader data = new StringReader(json);  //DataService.getDataStream(request.getRequestURI());

		// Output directly to the response
		// (rather than deserialise and re-serialise)
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		if (data != null) {
				IOUtils.copy(data, response.getOutputStream());
			return null;
		} else {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			Map<String, String> error404 = new HashMap<>();
			error404.put("message", "These are not the data you are looking for.");
			error404.put("status", String.valueOf(HttpStatus.NOT_FOUND_404));
			return error404;
		}
	}

}
