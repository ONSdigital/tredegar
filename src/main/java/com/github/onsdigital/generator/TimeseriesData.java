package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.onsdigital.json.timeseries.TimeSeriesValue;

public class TimeseriesData {

	static Map<String, Set<TimeSeriesValue>> dataMaps;

	static Set<TimeSeriesValue> getData(String cdid) throws IOException {
		return getDataMaps().get(cdid);
	}

	public static Map<String, Set<TimeSeriesValue>> getDataMaps() throws IOException {

		if (dataMaps == null) {
			buildDataMaps();
		}

		return dataMaps;
	}

	private static void buildDataMaps() throws IOException {

		// We use TreeMap here so that the keys are ordered alphabetically.
		// This useful when inspecting the map during development.
		// It can probably revert to HashMap at some point.
		dataMaps = new TreeMap<>();

		Reader reader = ResourceUtils.getReader("/Timeseries data - MM23_CSDB_DS.csdb.csv");
		try (CSVReader csvReader = new CSVReader(reader)) {

			// Set up the CDID maps:
			String[] cdids = csvReader.readNext();
			for (String cdid : cdids) {
				// Skip the date column, which has no header:
				if (StringUtils.isNotBlank(cdid)) {
					// NB LinkedHashSet preserves the order of items.
					// This is useful because we want to avoid duplicates,
					// but the date values (e.g. months) don't natulally sort
					// alphabetically.
					dataMaps.put(cdid.toLowerCase(), new LinkedHashSet<TimeSeriesValue>());
				}
			}

			// Read the rows until we get a blank for the date.
			// After that blank line, the content is metadata about the CDIDs.
			String[] row;
			while ((row = csvReader.readNext()) != null && StringUtils.isNotBlank(row[0])) {

				String date = row[0];
				for (int i = 1; i < row.length; i++) {
					if (StringUtils.isNotBlank(row[i])) {
						// Store the datum in the appropriate CDID map.
						// Yep, datum is a bit of a poncy word these days.
						// Not to worry.
						TimeSeriesValue value = new TimeSeriesValue();
						value.date = date;
						value.value = row[i];
						dataMaps.get(cdids[i].toLowerCase()).add(value);
					}
				}
			}
		}
	}

	/**
	 * Builds the data maps and prints out some info about what was parsed.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		buildDataMaps();

		System.out.println("done");
		for (String cdid : dataMaps.keySet()) {
			System.out.println(cdid + ": " + dataMaps.get(cdid).size());
		}
		System.out.println("Total CDIDS: " + dataMaps.size());
	}

}
