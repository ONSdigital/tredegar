package com.github.onsdigital.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

public class XLSXGenerator {
	public List<Timeseries> timeseriesList;

	public XLSXGenerator(List<Timeseries> timeseriesList) {
		this.timeseriesList = timeseriesList;
	}

	public void write(OutputStream output) throws IOException {

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("data");
		generateHeaders(wb, sheet, timeseriesList);
		generateRows(sheet, timeseriesList);
		wb.write(output);

	}

	private void generateRows(Sheet sheet, List<Timeseries> timeseriesList) {
		// Get iterator for each timeseries data
		List<Iterator<TimeseriesValue>> iterators = getIterators(timeseriesList);
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

			i++; // Empty column
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

	private List<Iterator<TimeseriesValue>> getIterators(List<Timeseries> timeseriesList) {
		List<Iterator<TimeseriesValue>> iterators = new ArrayList<Iterator<TimeseriesValue>>();
		for (Timeseries timeseries : timeseriesList) {
			List<TimeseriesValue> values = new ArrayList<TimeseriesValue>();
			values.addAll(timeseries.years);
			values.addAll(timeseries.quarters);
			values.addAll(timeseries.months);
			iterators.add(values.iterator());
		}
		return iterators;
	}
}
