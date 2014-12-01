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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.onsdigital.json.timeseries.Timeseries;

/**
 * Handles the metadata CSVs under the {@value #resourceName} folder.
 * <p>
 * This class and its members are package private (default visibility) because
 * the API doesn't need to be exposed to the rest of the application.
 * 
 * @author david
 *
 */
public class MetadataCSV {

	static final String resourceName = "/data/Metadata";

	static ExecutorService executorService = Executors.newCachedThreadPool();

	static void parse() throws IOException {
		Collection<Path> files = getFiles();

		// Read the files containing basic info:
		for (Path file : files) {
			read(file);
		}

		// Read Rob's manually-created file:
		readRobsFile();

		// Report on "other":
		Set<Timeseries> other = Data.dataset("other");
		if (other != null) {
			System.out.println("'other' dataset contains " + other.size() + " CDIDs.");
		}
	}

	private static void readRobsFile() throws IOException {

		Csv csv = new Csv(resourceName + "/TimeseriesMetadata - CPI.csv");
		csv.read();
		csv.getHeadings();
		for (Map<String, String> row : csv) {
			String cdid = row.get("CDID");
			Timeseries timeseries = Data.timeseries(cdid);
			if (timeseries == null) {
				timeseries = Data.addTimeseries(cdid);
			}
			timeseries.name = row.get("Name");
			timeseries.seasonalAdjustment = row.get("Seasonal adjustment");
			timeseries.mainMeasure = row.get("Main measure");
			timeseries.description = row.get("Description");
			timeseries.note1 = StringUtils.defaultIfBlank(row.get("Note 1"), timeseries.note1);
			timeseries.note2 = StringUtils.defaultIfBlank(row.get("Note 2"), timeseries.note2);
		}
	}

	private static void read(Path file) throws IOException {
		try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(Files.newInputStream(file), Charset.forName("UTF8"))))) {

			String datasetName = FilenameUtils.getBaseName(file.getFileName().toString()).replace("_", "");
			Set<Timeseries> dataset = Data.dataset(datasetName);
			if (dataset == null) {
				dataset = Data.addDataset(datasetName);
			}

			// There are no header rows in these CSVs.
			// Column 0 is CDID, column 1 is the name:
			String[] row;
			int ok = 0;
			while ((row = csvReader.readNext()) != null) {
				// Add the name to each timeseries:
				String cdid = row[0];
				String name = row[1];
				Timeseries timeseries = Data.timeseries(cdid);
				if (timeseries == null) {
					timeseries = Data.addTimeseries(cdid);
				} else {
					ok++;
				}
				dataset.add(timeseries);
				// System.out.println(file.getFileName() + ": " + name);
				timeseries.name = name;
			}
			System.out.println("Updated " + ok + " timeseries.");
		}
	}

	private static Collection<Path> getFiles() throws IOException {
		Set<Path> result = new TreeSet<>();

		try {
			URL resource = MetadataCSV.class.getResource(resourceName);
			Path folder = Paths.get(resource.toURI());

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.csv")) {

				// Iterate the paths in this directory:
				for (Path item : stream) {
					if (!StringUtils.equals("TimeseriesMetadata - CPI.csv", item.getFileName().toString())) {
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
