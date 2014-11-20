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
import java.text.DecimalFormat;
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
					timeseries = Data.addTimeseries(header[i]);
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

						// Scale values if necessary:
						if (timeseries.cdid().equalsIgnoreCase("abmi")) {
							// System.out.println("ABMI: " +
							// timeseries.multiply());
						}
						scale(timeseriesValue, timeseries);
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

	/**
	 * Scales this timeseries value to match the unit defined in the given
	 * timeseries.
	 * <p>
	 * For example, the data value may be in thousands, but the timeseries unit
	 * may be millions, in which case the data value must be divided by 1000.
	 * 
	 * @param timeseriesValue
	 *            The value to be scaled.
	 * @param timeseries
	 *            The timeseries to match the scale of the value to.
	 */
	static void scale(TimeseriesValue timeseriesValue, Timeseries timeseries) {

		// If there's no scale, do nothing:
		if (timeseries.getScaleFactor() == 1) {
			return;
		}

		// Work out the number of decimal places for the format string:
		int decimalPlaces = 0;
		if (timeseriesValue.value.contains(".")) {
			int index = timeseriesValue.value.indexOf('.');
			decimalPlaces = timeseriesValue.value.substring(index + 1).length();
		}
		int m = timeseries.getScaleFactor();
		do {
			m /= 10;
			decimalPlaces++;
		} while (m > 1);

		// Build the format string.
		// It will be of the form: 0.00, which ensures a leading 0 if the final
		// value is less than 1 and the correct number of decimal places.
		String format = "0";
		if (decimalPlaces > 0) {
			format += ".";
			for (int i = 0; i < decimalPlaces; i++) {
				format += "0";
			}
		}

		// Parse, scale and format the value:
		double value = Double.parseDouble(timeseriesValue.value);
		value = value / timeseries.getScaleFactor();
		timeseriesValue.value = new DecimalFormat(format).format(value);
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
