package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;

public class TimeseriesData {

	static Map<String, Map<String, String>> dataMaps;

	static Map<String, String> getData(String cdid) throws IOException {
		return getDataMaps().get(cdid);
	}

	private static Map<String, Map<String, String>> getDataMaps()
			throws IOException {

		if (dataMaps == null)
			buildDataMaps();

		return dataMaps;
	}

	private static void buildDataMaps() throws IOException {

		dataMaps = new TreeMap<>();

		Reader reader = ResourceUtils
				.getReader("/Timeseries data - MM23_CSDB_DS.csdb.csv");
		try (CSVReader csvReader = new CSVReader(reader)) {

			// Set up the CDID maps:
			String[] cdids = csvReader.readNext();
			for (String cdid : cdids) {
				// Skip the date column, which has no header:
				if (StringUtils.isNotBlank(cdid)) {
					dataMaps.put(cdid.toLowerCase(),
							new TreeMap<String, String>());
				}
			}

			// Read the rows until we get a blank for the date.
			// After that blank line, the content is metadata about the CDIDs.
			String[] row;
			while ((row = csvReader.readNext()) != null
					&& StringUtils.isNotBlank(row[0])) {

				String date = row[0];
				for (int i = 1; i < row.length; i++) {
					if (StringUtils.isNotBlank(row[i])) {
						// Store the datum in the appropriate CDID map.
						// Yep, datum is a bit of a poncy word these days.
						// Not to worry.
						dataMaps.get(cdids[i].toLowerCase()).put(date, row[i]);
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
