package com.github.onsdigital.api.data;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.bean.DataListRequest;
import com.github.onsdigital.data.DataService;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * Returns requested data as a single list.
 * 
 * @author brn
 *
 */
@Api
public class DataList {

	@POST
	public void post(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {
		try {
			DataListRequest dataListRequest = Serialiser.deserialise(request,
					DataListRequest.class);
			System.out.println("Data list request recieved" + dataListRequest);
			response.setCharacterEncoding("UTF8");
			response.setContentType("application/json");
			
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add("/economy/inflationandpriceindices/timeseries/d7g7");
			arrayList.add("/economy/inflationandpriceindices");
			
			response.getWriter().write(DataService.getDataAsString(dataListRequest.uriList).toString());
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setContentType("text/plain");
			response.getWriter().write(
					"An error occured while processing data request");
		}
	}
}
