package com.github.onsdigital.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public static final char CSV_DELIMTER = ',';
	public List<Timeseries> timeseriesList;

	public CSVGenerator(List<Timeseries> timeseriesList) {
		this.timeseriesList = timeseriesList;
	}

	public void write(OutputStream output) throws IOException {
		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(output, Charset.forName("UTF8")), CSV_DELIMTER)) {
			generateCsvHeaders(writer);
			generateCsvRows(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCsvRows(CSVWriter writer) {
		List<Iterator<TimeseriesValue>> iterators = getIterators(timeseriesList);
		int size = timeseriesList.size() * 3;
		String[] row;

		while (hasMoreData(iterators)) { // Check if any of the lists has more
			// data left
			int i = 0;
			row = newRow(size);
			for (Iterator<TimeseriesValue> iterator : iterators) {
				if (iterator.hasNext()) {
					TimeseriesValue timeseriesValue = iterator.next();
					System.out.println(timeseriesValue.date + "\t" + timeseriesValue.value);
					row[i++] = timeseriesValue.date;
					row[i++] = timeseriesValue.value;
				} else {
					row[i++] = "";
					row[i++] = "";
				}
				row[i++] = "";
			}
			writer.writeNext(row);
			System.out.println("\n");
		}
	}

	@SuppressWarnings("unused")
	private void generateCsvHeaders(CSVWriter writer) {
		int size = timeseriesList.size() * 3;
		String[] row = newRow(size);
		int i = 0;
		for (Timeseries timeseries : timeseriesList) {
			row[i++] = timeseries.name;
			row[i++] = "";
			row[i++] = "";
			System.out.println("Geneararing CSV for: " + timeseries.name + " at: " + timeseries.uri);
		}
		writer.writeNext(row);

		row = newRow(size);
		i = 0;
		for (Timeseries timeseries : timeseriesList) {
			row[i++] = "Date";
			row[i++] = "Value";
			row[i++] = "";
		}
		writer.writeNext(row);

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

	private String[] newRow(int size) {
		return new String[size];
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
