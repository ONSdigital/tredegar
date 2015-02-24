package com.github.onsdigital.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.FileUtils;
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
		
		URL url = new URL(request.getRequestURL().toString());
		//If static search convert get parameter to path parameter and redirect
		if (url.getPath().equals("/static/search")) {
			String searchTerm = request.getParameter("q");
			response.sendRedirect("/static/search/" + searchTerm);
			return;
		}


        if (!PreGenerated.handle(request, response)) {

            String content = PrerenderIo.handle(request, response);
            if (content == null) {
                response.setStatus(HttpStatus.SC_NOT_FOUND);
                response.getWriter().write("Apologies, for some reason it seems that page can't be generated.");
            }
            else {
                System.out.println("Content prerendered. Saving to file ... " + request.getRequestURL().toString());

//                File file = new File(PreGenerated.createStaticFilePath(request));
//                file.getParentFile().mkdirs();
//                FileUtils.writeStringToFile(file, content, Charset.forName("utf8"));
            }
        }
        else
            System.out.println("Found static pre-generated copy of " + request.getRequestURL().toString());


	}
}
