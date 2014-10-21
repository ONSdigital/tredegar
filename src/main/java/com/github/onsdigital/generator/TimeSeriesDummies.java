package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;

public class TimeSeriesDummies {

	static Map<String, String> cdids = new TreeMap<>();
	static Map<String, Map<String, String>> firstletters = new TreeMap<>();

	// /**
	// * Creates dummy timeseries data.
	// *
	// * @param folder
	// * @param file
	// * @throws IOException
	// */
	// private static void createTimeseries(Folder folder, File file)
	// throws IOException {
	//
	// // Create the timeseries directory:
	// File timeseries = new File(file, "timeseries");
	// timeseries.mkdir();
	//
	// // Select a subset of the 14+K CDIDs we have:
	// String firstletter = file.getName().substring(0, 1).toLowerCase();
	// // Aint no Ps in the CDIDs:
	// if (firstletter.equals("p"))
	// firstletter = "q";
	// // System.out.println("Getting " + firstletter + " from "
	// // + firstletters.keySet());
	// Map<String, String> cdids = firstletters.get(firstletter);
	//
	// // Generate dummy timeseries for each CDID in the subset:
	// for (String cdid : cdids.keySet()) {
	// TimeSeries series = new TimeSeries();
	// series.name = cdids.get(cdid);
	// String json = Serialiser.serialise(series);
	// FileUtils.writeStringToFile(new File(timeseries, cdid + ".json"),
	// json);
	// }
	// }

	/**
	 * Creates dummy timeseries data.
	 * 
	 * @param folder
	 * @param file
	 * @throws IOException
	 */
	static void loadTimeseries() throws IOException {

		// Read in the CDIDs and names:
		URI csvUri;
		try {
			csvUri = Csv.class.getClassLoader().getResource("data/Metadata").toURI();
		} catch (URISyntaxException e) {
			throw new IOException("Error getting URI for CDID resource folder.", e);
		}
		Path csvPath = Paths.get(csvUri);
		// Creating a DirectoryStream inside a try-with-resource block
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(csvPath, "*.csv")) {
			for (Path p : stream) {

				// Iterate over the paths:
				Serialiser.getBuilder().setPrettyPrinting();
				Reader reader = ResourceUtils.getReader("/data/Metadata/" + p.getFileName());

				try (CSVReader csvReader = new CSVReader(reader)) {

					// Read the rows
					String[] row;
					while ((row = csvReader.readNext()) != null) {
						if (row.length > 1 && StringUtils.isNotBlank(row[0]) && StringUtils.isNotBlank(row[1])) {
							cdids.put(row[0], row[1]);
							System.out.println("Total: " + cdids.size());
						} else {
							System.out.println("Rejected: " + p.getFileName() + " row: " + ArrayUtils.toString(row));
						}
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Now organise into subsets, using the first letter as a useful
		// "bucket":
		for (String cdid : cdids.keySet()) {
			String firstletter = cdid.substring(0, 1).toLowerCase();
			if (!firstletters.containsKey(firstletter)) {
				Map<String, String> newMap = new TreeMap<String, String>();
				firstletters.put(firstletter, newMap);
				newMap.put(cdid, cdids.get(cdid));
			} else {
				firstletters.get(firstletter).put(cdid, cdids.get(cdid));
			}
		}
		// System.out.println(firstletters);
	}
}
