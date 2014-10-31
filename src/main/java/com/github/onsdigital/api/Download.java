package com.github.onsdigital.api;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 
 * Serves data files in xls or csv format
 * 
 */
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.api.taxonomy.Data;
import com.github.onsdigital.bean.DownloadRequest;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.util.CSVGenerator;
import com.github.onsdigital.util.XLSXGenerator;

@Endpoint
public class Download {


	@POST
	public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
		try {
			DownloadRequest downloadRequest = Serialiser.deserialise(request, DownloadRequest.class);
			response.setHeader("Content-Disposition", "attachment; filename=data." + downloadRequest.type);
			response.setCharacterEncoding("UTF8");
			response.setContentType("application/" + downloadRequest.type);
			processRequest(response.getOutputStream(), downloadRequest);
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setContentType("text/plain");
			response.getWriter().write("An error occured while processing download request");
		}
	}

	private void processRequest(OutputStream output, DownloadRequest downloadRequest) throws IOException {
		List<Timeseries> dataList = new ArrayList<Timeseries>();
		for (String uri : downloadRequest.uriList) {
			dataList.add(Serialiser.deserialise(Files.newInputStream(Data.getData(uri)), Timeseries.class));
		}

		switch (downloadRequest.type) {
		case "xlsx":
			new XLSXGenerator(dataList).write(output);
		case "csv":
		    new CSVGenerator(dataList).write(output);
		default:
			break;
		}

	}
	
}
