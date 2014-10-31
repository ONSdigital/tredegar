package com.github.onsdigital.api;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

@Endpoint
public class Download {

	@POST
	public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF8");
		response.setHeader("Content-Disposition", "attachment; filename=data.xls");
		try {
			IOUtils.copy(new StringReader("Downloaded content"), response.getOutputStream());
			DownloadRequest downloadRequest = Serialiser.deserialise(request, DownloadRequest.class);
			processRequest(downloadRequest);
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setContentType("text/plain");
			response.getWriter().write("An error occured while processing download request");
		}
	}

	private void processRequest(DownloadRequest downloadRequest) throws IOException {
		List<Timeseries> dataList = new ArrayList<Timeseries>();
		for (String uri : downloadRequest.urlList) {
			dataList.add(Serialiser.deserialise(Files.newInputStream(Data.getData(uri)), Timeseries.class));
		}
		generateFiles(dataList);
	}

	private void generateFiles(List<Timeseries> dataList) {
		for (Timeseries timeseries : dataList) {
			System.out.println(ToStringBuilder.reflectionToString(timeseries.name));
		}
	}

}
