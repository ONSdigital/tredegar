package com.github.onsdigital.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVWriter;

import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

/**
 * 
 * Transforms timeseries data into CSV format
 * 
 * @author Brn
 *
 */
public class CSVGenerator {

	private List<Timeseries> timeseries;
	public Map<String, TimeseriesValue[]> data;

	public CSVGenerator(List<Timeseries> timeseries, Map<String, TimeseriesValue[]> data) {
		this.timeseries = timeseries;
		this.data = data;
	}

	public void write(OutputStream output) throws IOException {
		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(output, Charset.forName("UTF8")), ',')) {
			generateCsvHeaders(writer);
			generateCsvRows(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCsvRows(CSVWriter writer) {
		String[] row;

		// Spacer row
		writer.writeNext(new String[2]);

		for (Entry<String, TimeseriesValue[]> rowData : data.entrySet()) {
			int size = rowData.getValue().length + 1;
			int i = 0;
			row = new String[size];
			row[i++] = rowData.getKey();
			for (TimeseriesValue timeseriesValue : rowData.getValue()) {
				row[i++] = (timeseriesValue ==null ? null : timeseriesValue.value);
			}
			writer.writeNext(row);
		}
	}

	private int generateCsvHeaders(CSVWriter writer) {

		int size = timeseries.size() + 1;

		// Rows
		int row = 9;
		String[] name = new String[size];
		String[] cdid = new String[size];
		String[] preUnit = new String[size];
		String[] unit = new String[size];
		String[] source = new String[size];
		String[] keyNote = new String[size];
		String[] additionalText = new String[size];
		String[] note1 = new String[size];
		String[] note2 = new String[size];

		// Labels
		int column = 0;
		name[column] = "Name";
		cdid[column] = "Series ID";
		preUnit[column] = "Pre unit";
		unit[column] = "Units";
		source[column] = "Source";
		keyNote[column] = "Note 1";
		additionalText[column] = "Note 2";
		note1[column] = "Note 3";
		note2[column] = "Note 4";
		column++;

		// Data
		for (Timeseries timeseries : this.timeseries) {
			name[column] = timeseries.name;
			cdid[column] = timeseries.cdid();
			preUnit[column] = timeseries.preUnit;
			unit[column] = timeseries.unit;
			source[column] = timeseries.source;
			keyNote[column] = timeseries.keyNote;
			additionalText[column] = timeseries.additionalText;
			note1[column] = timeseries.note1;
			note2[column] = timeseries.note2;
			column++;
			System.out.println("Geneararing CSV for: " + timeseries.name + " at: " + timeseries.uri);
		}
		writer.writeNext(name);
		writer.writeNext(cdid);
		writer.writeNext(preUnit);
		writer.writeNext(unit);
		writer.writeNext(source);
		writer.writeNext(keyNote);
		writer.writeNext(additionalText);
		writer.writeNext(note1);
		writer.writeNext(note2);

		return row;
	}
}
