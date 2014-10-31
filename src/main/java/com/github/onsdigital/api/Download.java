package com.github.onsdigital.api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
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
import com.github.onsdigital.json.timeseries.TimeseriesValue;

@Endpoint
public class Download {

	@POST
	public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
		response.setHeader("Content-Disposition:", "attachment; filename=data.xlsx");
		try {
			DownloadRequest downloadRequest = Serialiser.deserialise(request, DownloadRequest.class);
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
		for (String uri : downloadRequest.urlList) {
			System.out.println(uri);
			dataList.add(Serialiser.deserialise(Files.newInputStream(Data.getData(uri)), Timeseries.class));
		}

		switch (downloadRequest.type) {
		case "xlsx":
			generateExcelFile(output, dataList);
		case "csv":
		default:
			break;
		}

	}

	private File generateCsvFile(List<Timeseries> dataList) {
		for (Timeseries timeseries : dataList) {

		}

		return null;
	}

	private void generateExcelFile(OutputStream output, List<Timeseries> timeSeriesList) throws IOException {

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("data");
		generateHeaders(wb, sheet, timeSeriesList);
		generateRows(sheet, timeSeriesList);
		wb.write(output);

	}

	private void generateRows(Sheet sheet, List<Timeseries> timeseriesList) {
		// CellStyle cellStyle = wb.createCellStyle();
		// Font cellFont = wb.createFont();
		// cellFont.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		// cellStyle.setFont(cellFont);

		// Get iterator for each timeseries data
		List<Iterator<TimeseriesValue>> iterators = new ArrayList<Iterator<TimeseriesValue>>();
		for (Timeseries timeseries : timeseriesList) {
			if(timeseries.data == null) {
				//Temporary fix for timeseries with no data
				timeseries.data = Collections.emptyList();
			}
			iterators.add(timeseries.data.iterator());
		}

		int rowIndex = 2;
		while (hasMoreData(iterators)) { // Check if any of the lists has more
											// data left
			int columnIndex = 0;
			Row row = sheet.createRow(rowIndex++);
			for (Iterator<TimeseriesValue> iterator : iterators) {
				if (iterator.hasNext()) {
					TimeseriesValue timeseriesValue = iterator.next();
					Cell dateCell = row.createCell(columnIndex++);
					dateCell.setCellValue(timeseriesValue.date);
					Cell valueCell = row.createCell(columnIndex++);
					valueCell.setCellValue(timeseriesValue.value);
				} else {
					columnIndex += 2;// Leave empty time series with no value
				}
				columnIndex++;
			}
		}
	}

	private void generateHeaders(Workbook wb, Sheet sheet, List<Timeseries> timeseriesList) {

		CellStyle styleHeader = wb.createCellStyle();
		Font fontHeader = wb.createFont();
		fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeader.setFont(fontHeader);

		Row row = sheet.createRow(0); // Header row
		int j = 0;
		for (Timeseries timeseries : timeseriesList) {
			Cell dateHeader = row.createCell(j);
			dateHeader.setCellValue(timeseries.name);
			dateHeader.setCellStyle(styleHeader);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, j, ++j));
			j += 2;
		}

		row = sheet.createRow(1);
		// Add Date Value headers for earch timeseries
		for (int i = 0; i < (timeseriesList.size() * 3); i++) {
			Cell dateHeader = row.createCell(i);
			dateHeader.setCellValue("Date");
			dateHeader.setCellStyle(styleHeader);

			Cell valueHeader = row.createCell(++i);
			valueHeader.setCellValue("Value");
			valueHeader.setCellStyle(styleHeader);
			
			i++; //Empty column
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean hasMoreData(List<Iterator<TimeseriesValue>> iterators) {
		// Go through all the iterators see if any of them has any value left
		for (Iterator iterator : iterators) {
			if (iterator.hasNext()) {
				return true;
			}
		}
		return false; // No more data in non of the lists
	}
}
