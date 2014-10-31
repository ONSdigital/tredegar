package com.github.onsdigital.api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
		response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");
		try {
//			DownloadRequest downloadRequest = Serialiser.deserialise(request, DownloadRequest.class);
			response.setContentType("application/xlsx");
			processRequest(response.getOutputStream(),  null);
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setContentType("text/plain");
			response.getWriter().write("An error occured while processing download request");
		}
	}

	private void processRequest(OutputStream output, DownloadRequest downloadRequest) throws IOException {
		List<Timeseries> dataList = new ArrayList<Timeseries>();
		List<String> uriList = new ArrayList<>();
		uriList.add("/economy/inflationandpriceindices/timeseries/d7g7"); 
		for (String uri : uriList) {
			dataList.add(Serialiser.deserialise(Files.newInputStream(Data.getData(uri)), Timeseries.class));
		}

//		switch (downloadRequest.type) {
//		case "xls":
			generateExcelFile(output, dataList);
//		case "csv":
//			return generateCsvFile(dataList);
//		default:
//			break;
//		}
//		return null;

	}

	private File generateCsvFile(List<Timeseries> dataList) {
		for (Timeseries timeseries : dataList) {

		}

		return null;
	}

	private void generateExcelFile(OutputStream output, List<Timeseries> dataList) throws IOException {
		System.out.println("Generating excel file");
		Row row = null;
		Cell cell = null;
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		row = sheet.createRow(0);
		int i = 0;
		for (Timeseries timeseries : dataList) {
			cell = row.createCell(i);
			System.out.println(timeseries.name);
			cell.setCellValue(timeseries.name);
			i++;
		}
		
//		int j = 1;
//		for (TimeseriesValue data : dataList.get(0).data) {
//			row = sheet.createRow(j);
//			cell = row.createCell(0);
//			cell.setCellValue(data.date);
//			cell = row.createCell(1);
//			cell.setCellValue(data.value);
//			j++;
//		}

		wb.write(output);

	}
}
