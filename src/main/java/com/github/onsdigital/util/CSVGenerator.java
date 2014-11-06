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

		for (Entry<String, TimeseriesValue[]> rowData : data.entrySet()) {
			int size = rowData.getValue().length + 1;
			int i = 0;
			row = newRow(size);
			row[i++] = rowData.getKey();
			for (TimeseriesValue timeseriesValue : rowData.getValue()) {
				row[i++] = timeseriesValue.value;
			}
			writer.writeNext(row);
		}
	}

	private void generateCsvHeaders(CSVWriter writer) {

		int size = timeseries.size() + 1;
		String[] names = newRow(size);
		String[] cdids = newRow(size);

		int i = 0;
		names[i] = "";
		cdids[i++] = "Date";
		for (Timeseries timeseries : this.timeseries) {
			names[i] = timeseries.name;
			cdids[i++] = timeseries.cdid();
			System.out.println("Geneararing CSV for: " + timeseries.name + " at: " + timeseries.uri);
		}
		writer.writeNext(names);
		writer.writeNext(cdids);
	}

	private String[] newRow(int size) {
		return new String[size];
	}
}
