package com.github.onsdigital.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.util.NavigationUtil;

/**
 * Navigation end point that returns taxonomy leves
 * 
 * @author Bren
 */

@Endpoint
public class Navigation {

	@GET
	public static Object get(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {

		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		
		return NavigationUtil.getNavigationNodes();

	}

}
