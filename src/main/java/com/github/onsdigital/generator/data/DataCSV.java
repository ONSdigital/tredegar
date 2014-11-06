package com.github.onsdigital.generator.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

/**
 * Handles the data CSVs under the {@value #resourceName} folder.
 * <p>
 * This class and its members are package private (default visibility) because
 * the API doesn't need to be exposed to the rest of the application.
 * 
 * @author david
 *
 */
public class DataCSV {

	static final String resourceName = "/data";

	static ExecutorService executorService = Executors.newCachedThreadPool();

	public static void parse() throws IOException {
		Collection<Path> files = getFiles();

		for (Path file : files) {
			read(file);
		}

		readManuallyEditedCsv();

		// // Now sort and arrange all the data:
		// for (Timeseries timeseries : new Data()) {
		//
		// // Get the values:
		// Map<String, TimeseriesValue> dataMap = new HashMap<>();
		// for (TimeseriesValue value : timeseries.data) {
		// dataMap.put(StringUtils.lowerCase(StringUtils.trim(value.date)),
		// value);
		// }
		//
		// // Now [re]initialise all the sets:
		// timeseries.data = new LinkedHashSet<>();
		// timeseries.years = new LinkedHashSet<>();
		// timeseries.quarters = new LinkedHashSet<>();
		// timeseries.months = new LinkedHashSet<>();
		//
		// // Add the keys in order, from years to quarters, to months:
		// // System.out.println("Sorting years..");
		// for (Entry<String, String> year : Data.years.entrySet()) {
		// String key =
		// StringUtils.lowerCase(StringUtils.trim(year.getValue()));
		// if (dataMap.containsKey(key)) {
		// TimeseriesValue value = dataMap.get(key);
		// timeseries.data.add(value);
		// timeseries.years.add(value);
		// }
		// }
		// // System.out.println("Sorting quarters..");
		// for (Entry<String, String> quarter : Data.quarters.entrySet()) {
		// String key =
		// StringUtils.lowerCase(StringUtils.trim(quarter.getValue()));
		// if (dataMap.containsKey(key)) {
		// TimeseriesValue value = dataMap.get(key);
		// timeseries.data.add(value);
		// timeseries.quarters.add(value);
		// }
		// }
		// // System.out.println("Sorting months..");
		// for (Entry<String, String> month : Data.months.entrySet()) {
		// String key =
		// StringUtils.lowerCase(StringUtils.trim(month.getValue()));
		// if (dataMap.containsKey(key)) {
		// TimeseriesValue value = dataMap.get(key);
		// timeseries.data.add(value);
		// timeseries.months.add(value);
		// }
		// }
		// }
	}

	private static void readManuallyEditedCsv() throws IOException {
		try {
			// Now apply the data from the manually-prepared CSV:
			URL resource = DataCSV.class.getResource(resourceName + "/" + "Timeseries data - MM23_CSDB_DS.csdb.csv");
			Path manuallyEditedCsv = Paths.get(resource.toURI());
			read(manuallyEditedCsv, "MM23");
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	private static void read(Path file, String... alternativeName) throws IOException {
		try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(Files.newInputStream(file), Charset.forName("CP1252"))))) {

			String name;
			if (alternativeName.length > 0) {
				name = alternativeName[0];
			} else {
				name = FilenameUtils.getBaseName(file.getFileName().toString());
			}
			Set<Timeseries> dataset = Data.dataset(name);
			if (dataset == null) {
				dataset = Data.addDataset(name);
			}

			// Check all the CDIDs in the header row:
			// int duplicates = 0;
			String[] header = csvReader.readNext();
			for (int i = 1; i < header.length; i++) {
				Timeseries timeseries = Data.timeseries(header[i]);
				if (timeseries == null) {
					timeseries = Data.addTimeseries(header[i], name);
				}
				dataset.add(timeseries);
			}

			// Now read the data - each row *may* contain one additional value
			// for each timeseries:
			String[] row;
			rows: while ((row = csvReader.readNext()) != null) {

				// There is a blank line between the data and the
				// additional information below, so stop reading there:
				if (row.length == 0 || StringUtils.isBlank(row[0])) {
					break rows;
				}

				// Add data to timeseries:
				String date = row[0];
				Data.addDateOption(date);
				for (int i = 1; i < Math.min(header.length, row.length); i++) {
					if (StringUtils.isNotBlank(header[i]) && StringUtils.isNotBlank(row[i])) {
						Timeseries timeseries = Data.timeseries(header[i]);
						String cdid = header[i];
						if (cdid == null) {
							// This one was marked as a duplicate
							continue;
						}
						String value = row[i];
						TimeseriesValue timeseriesValue = new TimeseriesValue();
						timeseriesValue.date = StringUtils.trim(date);
						timeseriesValue.value = StringUtils.trim(value);
						timeseries.add(timeseriesValue);
					}
				}
			}

			// Print out some sanity-check information:
			for (int i = 1; i < header.length; i++) {
				if (StringUtils.isNotBlank(header[i])) {
					Timeseries timeseries = Data.timeseries(header[i]);
					if (timeseries.years.size() == 0 && timeseries.quarters.size() == 0 && timeseries.months.size() == 0) {
						System.out.println(timeseries + " has no data.");
					}
				}
			}
			// if (duplicates > 0) {
			// System.out.println(name + " contains " + dataset.size() +
			// " timeseries (" + duplicates + " duplicates)");
			// } else {
			System.out.println(name + " contains " + dataset.size() + " timeseries");
			// }
		}
	}

	private static Collection<Path> getFiles() throws IOException {
		Set<Path> result = new TreeSet<>();

		try {
			URL resource = DataCSV.class.getResource(resourceName);
			Path folder = Paths.get(resource.toURI());

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.csv")) {

				// Iterate the paths in this directory:
				for (Path item : stream) {
					if (!StringUtils.equals(item.getFileName().toString(), "Timeseries data - MM23_CSDB_DS.csdb.csv")) {
						result.add(item);
					}
				}

			}

		} catch (URISyntaxException e) {
			throw new IOException(e);
		}

		return result;
	}
}
