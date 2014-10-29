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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.onsdigital.json.timeseries.TimeSeries;

public class TimeseriesMetadata {

	static Map<String, TimeSeries> timeseries;

	public static TimeSeries getData(String cdid) throws IOException {
		return loadTimeseriesMetadata().get(StringUtils.lowerCase(cdid));
	}

	static Map<String, TimeSeries> loadTimeseriesMetadata() throws IOException {

		if (timeseries == null) {
			buildDataMaps();
		}

		return timeseries;
	}

	private static void buildDataMaps() throws IOException {

		timeseries = new TreeMap<>();

		for (Path path : getFiles()) {
			String charsetName = "UTF8";
			// We have some dodgy characters in this file:
			String name = path.getFileName().toString();
			if (name.equals("SDQ7.csv")) {
				continue;
				// charsetName = "ASCII";
			}
			try (Reader reader = Files.newBufferedReader(path, Charset.forName(charsetName))) {
				System.out.println("Reading " + path);
				readFile(reader);
			}
		}

		// Ensure the spreadsheet that Rob manually produced is always fully
		// represented by overwriting any values that may have been replaced:
		try (Reader reader = ResourceUtils.getReader("/data/Metadata/TimeseriesMetadata - CPI.csv")) {
			readFile(reader);
		}
	}

	private static Collection<Path> getFiles() throws IOException {
		Set<Path> result = new HashSet<>();

		try {
			URL resource = TimeseriesMetadata.class.getResource("/data/Metadata");
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
			String[] headings = csvReader.readNext();

			// Read the rows until we get a blank for the date.
			// After that blank line, the content is metadata about the
			// CDIDs.
			String[] row;
			while ((row = csvReader.readNext()) != null) {

				TimeSeries item = new TimeSeries();
				for (int i = 0; i < Math.min(row.length, headings.length); i++) {
					if (StringUtils.isNotBlank(row[i]) && StringUtils.isNotBlank(headings[i])) {
						if (headings[i].trim().toLowerCase().equals("Name".toLowerCase())) {
							item.name = row[i];
						} else if (headings[i].trim().toLowerCase().equals("CDID".toLowerCase())) {
							item.cdid = row[i];
							item.fileName = StringUtils.lowerCase(row[i]);
						} else if (headings[i].trim().toLowerCase().equals("Seasonal adjustment".toLowerCase())) {
							item.seasonalAdjustment = row[i];
						} else if (headings[i].trim().toLowerCase().equals("Units".toLowerCase())) {
							item.units = row[i];
						} else if (headings[i].trim().toLowerCase().equals("Main measure".toLowerCase())) {
							item.mainMeasure = row[i];
						} else if (headings[i].trim().toLowerCase().equals("Description".toLowerCase())) {
							item.description = row[i];
						} else if (headings[i].trim().toLowerCase().equals("Note 1".toLowerCase())) {
							item.note1 = row[i];
						} else if (headings[i].trim().toLowerCase().equals("Note 2".toLowerCase())) {
							item.note2 = row[i];
						}
					}
				}

				// Add to the collection:
				timeseries.put(StringUtils.lowerCase(item.cdid), item);
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
		// for (String cdid : timeseries.keySet()) {
		// System.out.println(cdid + ": " + timeseries.get(cdid).name);
		// }
		System.out.println("Total timeseries: " + timeseries.size());

		System.out.println();

		System.out.println("CDIDs in common:");
		int count = 0;
		for (String cdid : timeseries.keySet()) {
			if (TimeseriesData.getData(cdid) != null) {
				System.out.println(cdid + ": " + timeseries.get(cdid).name);
				count++;
			}
		}
		System.out.println(count);
	}
}
