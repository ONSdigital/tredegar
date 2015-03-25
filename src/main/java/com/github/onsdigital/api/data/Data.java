package com.github.onsdigital.api.data;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.util.HostHelper;
import com.github.onsdigital.util.Validator;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.mashape.unirest.http.Unirest.get;

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

        String collection = "";
        String authenticationToken = "";
        final String authenticationHeader = "X-Florence-Token";

        System.out.println("Checking cookies...");

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("collection")) {
                    System.out.println("Found collection cookie: " + cookie.getValue());
                    collection = cookie.getValue();
                }
                if (cookie.getName().equals("access_token")) {
                    System.out.println("Found access_token cookie: " + cookie.getValue());
                    authenticationToken = cookie.getValue();
                }
            }
        }

        InputStream data;
        if (StringUtils.isEmpty(collection))
        {
            data = DataService.getDataStream(request.getRequestURI());
        }
        else {
            URI uri = URI.create(request.getRequestURI());
            String uriPath = DataService.cleanPath(uri);

            if (uriPath.length() > 0)
            {
                uriPath += "/";
            }

            uriPath += "data.json";

            try {
                String url = Configuration.getZebedeeUrl() + "/content/" + collection;

                System.out.println("Calling zebedee: " + url + "for path " + uriPath + " with token: " + authenticationToken);
                String dataString =  get(Configuration.getZebedeeUrl() + "/content/" + collection)
                        .header(authenticationHeader, authenticationToken)
                        .queryString("uri", uriPath).asString().getBody();
                data = IOUtils.toInputStream(dataString);

            } catch (UnirestException e) {
                // Look for a data file:
                data = DataService.getDataStream(request.getRequestURI());
            }
        }

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
            error404.put("message", "These are not the data you are looking for.");
            error404.put("status", String.valueOf(HttpStatus.NOT_FOUND_404));
            return error404;
        }
    }

}
