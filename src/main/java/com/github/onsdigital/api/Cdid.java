package com.github.onsdigital.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

/**
 * 
 * Serves data files in xls or csv format
 * 
 */
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.bean.CdidRequest;
import com.github.onsdigital.json.timeseries.Timeseries;

/**
 * Provides the ability to request the json for one or more CDIDs.
 * 
 * @author david
 *
 */
@Endpoint
public class Cdid {

	@POST
	public List<Timeseries> post(@Context HttpServletRequest request, @Context HttpServletResponse response, CdidRequest cdidRequest) throws IOException {
		System.out.println("Download request recieved" + cdidRequest);
		return processRequest(cdidRequest);
	}

	private List<Timeseries> processRequest(CdidRequest cdidRequest) throws IOException {

		List<Timeseries> result = new ArrayList<>();
		result.add(new Timeseries());
		return result;
	}

}
