package com.github.onsdigital.api;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;

/**
 * 
 * Serves data files in xls or csv format
 * 
 */
import com.github.davidcarboni.restolino.framework.Endpoint;

@Endpoint
public class Download {

	@POST
	public void get(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) throws IOException {
		httpServletResponse.setCharacterEncoding("UTF8");
		httpServletResponse.setHeader("Content-Disposition", "attachment; filename=data.xls");
		IOUtils.copy(new StringReader("Downloaded content"), httpServletResponse.getOutputStream());

	}

}
