package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.onsdigital.json.TimeSeries;

public class TimeseriesMetadata {

	static Map<String, TimeSeries> timeseries;

	static TimeSeries getData(String cdid) throws IOException {
		return loadTimeseriesMetadata().get(cdid);
	}

	static Map<String, TimeSeries> loadTimeseriesMetadata() throws IOException {

		if (timeseries == null)
			buildDataMaps();

		return timeseries;
	}

	private static void buildDataMaps() throws IOException {

		timeseries = new TreeMap<>();

		Reader reader = ResourceUtils
				.getReader("/TimeseriesMetadata - CPI.csv");
		try (CSVReader csvReader = new CSVReader(reader)) {

			// Set up the CDID maps:
			String[] headings = csvReader.readNext();

			// Read the rows until we get a blank for the date.
			// After that blank line, the content is metadata about the CDIDs.
			String[] row;
			while ((row = csvReader.readNext()) != null) {

				TimeSeries item = new TimeSeries();
				for (int i = 0; i < Math.min(row.length, headings.length); i++) {
					if (StringUtils.isNotBlank(row[i])
							&& StringUtils.isNotBlank(headings[i])) {
						if (headings[i].trim().toLowerCase()
								.equals("Name".toLowerCase()))
							item.name = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("CDID".toLowerCase()))
							item.cdid = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("Seasonal adjustment".toLowerCase()))
							item.seasonalAdjustment = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("Units".toLowerCase()))
							item.units = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("Main measure".toLowerCase()))
							item.mainMeasure = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("Description".toLowerCase()))
							item.description = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("Note 1".toLowerCase()))
							item.note1 = row[i];
						else if (headings[i].trim().toLowerCase()
								.equals("Note 2".toLowerCase()))
							item.note2 = row[i];
					}
				}

				// Add to the collection:
				timeseries.put(item.cdid.toLowerCase(), item);
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
