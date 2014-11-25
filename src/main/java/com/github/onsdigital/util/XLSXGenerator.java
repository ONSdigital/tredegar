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
		int startRow = generateHeaders(wb, sheet, timeseries);
		generateRows(sheet, timeseries, startRow);
		wb.write(output);
	}

	private void generateRows(Sheet sheet, List<Timeseries> timeseriesList, int startRow) {
		int rownum = startRow + 1;

		for (Entry<String, TimeseriesValue[]> rowValues : data.entrySet()) {
			int i = 0;
			Row row = sheet.createRow(rownum++);
			row.createCell(i++).setCellValue(rowValues.getKey());
			for (TimeseriesValue timeseriesValue : rowValues.getValue()) {
				row.createCell(i++).setCellValue(timeseriesValue == null ? null : timeseriesValue.value);
			}
		}
	}

	private int generateHeaders(Workbook wb, Sheet sheet, List<Timeseries> timeseriesList) {

		// Rows
		int row = 0;
		Row name = sheet.createRow(row++);
		Row cdid = sheet.createRow(row++);
		Row preUnit = sheet.createRow(row++);
		Row unit = sheet.createRow(row++);
		Row source = sheet.createRow(row++);
		Row keyNote = sheet.createRow(row++);
		Row additionalText = sheet.createRow(row++);
		Row note1 = sheet.createRow(row++);
		Row note2 = sheet.createRow(row++);

		// Labels
		int column = 0;
		name.createCell(column).setCellValue("Name");
		cdid.createCell(column).setCellValue("Series ID");
		preUnit.createCell(column).setCellValue("Pre unit");
		unit.createCell(column).setCellValue("Units");
		source.createCell(column).setCellValue("Source");
		keyNote.createCell(column).setCellValue("Note 1");
		additionalText.createCell(column).setCellValue("Note 2");
		note1.createCell(column).setCellValue("Note 3");
		note2.createCell(column).setCellValue("Note 4");
		column++;

		// Data
		for (Timeseries timeseries : this.timeseries) {
			name.createCell(column).setCellValue(timeseries.name);
			cdid.createCell(column).setCellValue(timeseries.cdid());
			preUnit.createCell(column).setCellValue(timeseries.preUnit);
			unit.createCell(column).setCellValue(timeseries.unit);
			source.createCell(column).setCellValue(timeseries.source);
			keyNote.createCell(column).setCellValue(timeseries.keyNote);
			additionalText.createCell(column).setCellValue(timeseries.additionalText);
			note1.createCell(column).setCellValue(timeseries.note1);
			note2.createCell(column).setCellValue(timeseries.note2);
			column++;
			System.out.println("Geneararing XLSX for: " + timeseries.name + " at: " + timeseries.uri);
		}

		return row;
	}
}
