package com.github.onsdigital.errors;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.api.RequestHandler;
import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.framework.ServerError;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;

@Api
public class Error500 implements ServerError {

	@GET
	public void demo(HttpServletRequest req, HttpServletResponse res) throws IOException {
		handle(req, res, null, new RuntimeException("Error page demonstration."));
	}

	@Override
	public Object handle(HttpServletRequest req, HttpServletResponse res, RequestHandler requestHandler, Throwable t) throws IOException {

		// Ensure ResourceUtils "sees" the reloadable classloader in
		// development:
		ResourceUtils.classLoaderClass = Error500.class;
		if (requestHandler != null) {
			System.out.println("Error in " + requestHandler.endpointClass.getName() + " (" + requestHandler.method.getName() + ")");
		}
		System.out.println(ExceptionUtils.getStackTrace(t));
//		try (Reader input = ResourceUtils.getReader("/files/500.html")) {
//			res.setContentType("text/html");
//			res.setCharacterEncoding("UTF8");
//			IOUtils.copy(input, res.getWriter());
//		} catch (IOException e) {
//			// A [partial] response may have been written before the error
//			// occurred, or it may not be possible to access the 500 content.
//			System.out.println("Error sending error page to response.");
//		}
//		res.sendRedirect("/#!/500");
		res.setStatus(500);
		return null;
	}

}
