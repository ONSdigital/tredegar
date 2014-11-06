package com.github.onsdigital.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

public class XLSXGenerator {

	private List<Timeseries> timeseries;
	public Map<String, TimeseriesValue[]> data;

	public XLSXGenerator(List<Timeseries> timeseries, Map<String, TimeseriesValue[]> data) {
		this.timeseries = timeseries;
		this.data = data;
	}

	public void write(OutputStream output) throws IOException {

		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("data");
		generateHeaders(wb, sheet, timeseries);
		generateRows(sheet, timeseries);
		wb.write(output);
	}

	private void generateRows(Sheet sheet, List<Timeseries> timeseriesList) {
		int rownum = 2;

		for (Entry<String, TimeseriesValue[]> rowValues : data.entrySet()) {
			int i = 0;
			Row row = sheet.createRow(rownum++);
			row.createCell(i++).setCellValue(rowValues.getKey());
			for (TimeseriesValue timeseriesValue : rowValues.getValue()) {
				row.createCell(i++).setCellValue(timeseriesValue.value);
			}
		}

	}

	private void generateHeaders(Workbook wb, Sheet sheet, List<Timeseries> timeseriesList) {

		Row names = sheet.createRow(0);
		Row cdids = sheet.createRow(1);

		int i = 0;
		names.createCell(i).setCellValue("");
		cdids.createCell(i++).setCellValue("Date");
		for (Timeseries timeseries : this.timeseries) {
			names.createCell(i).setCellValue(timeseries.name);
			cdids.createCell(i++).setCellValue(timeseries.cdid());
			System.out.println("Geneararing XLSX for: " + timeseries.name + " at: " + timeseries.uri);
		}
	}
}
