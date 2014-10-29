package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
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

	public static Set<TimeSeriesValue> getData(String cdid) throws IOException {
		return getDataMaps().get(StringUtils.lowerCase(cdid));
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

		for (Path path : getFiles()) {
			// String charsetName = "UTF8";
			String charsetName = "windows-1252";
			// We have some dodgy characters in this file:
			// String name = path.getFileName().toString();
			// if (name.equals("BB.csv") || name.equals("SDQ7.csv")) {
			// continue;
			// // charsetName = "ASCII";
			// }
			try (Reader reader = Files.newBufferedReader(path, Charset.forName(charsetName))) {
				System.out.println("Reading " + path);
				readFile(reader);
			}
		}

		try (Reader reader = ResourceUtils.getReader("/data/Timeseries data - MM23_CSDB_DS.csdb.csv")) {
			readFile(reader);
		}
	}

	private static Collection<Path> getFiles() throws IOException {
		Set<Path> result = new HashSet<>();

		try {
			URL resource = TimeseriesData.class.getResource("/data");
			Path folder = Paths.get(resource.toURI());

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.csv")) {

				// Iterate the paths in this directory:
				for (Path item : stream) {
					result.add(item);
				}

			}

		} catch (URISyntaxException e) {
			throw new IOException(e);
		}

		return result;
	}

	private static void readFile(Reader reader) throws IOException {

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
