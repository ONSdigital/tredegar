//package com.github.onsdigital.generator;
//
//import java.io.IOException;
//import java.io.Reader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//import org.apache.commons.lang3.StringUtils;
//
//import au.com.bytecode.opencsv.CSVReader;
//
//import com.github.davidcarboni.ResourceUtils;
//import com.github.onsdigital.json.timeseries.TimeSeries;
//
//public class AlphaContent {
//
//	static Map<String, List<TimeSeries>> timeseries;
//
//	static Map<String, List<TimeSeries>> getTimeseriesMetadata() throws IOException {
//
//		if (timeseries == null) {
//			buildDataMaps();
//		}
//
//		return timeseries;
//	}
//
//	private static void buildDataMaps() throws IOException {
//
//		timeseries = new TreeMap<>();
//		try (Reader reader = ResourceUtils.getReader("/Alpha content master.csv")) {
//			readFile(reader);
//		}
//	}
//
//	private static void readFile(Reader reader) throws IOException {
//
//		try (CSVReader csvReader = new CSVReader(reader)) {
//
//			// Set up the CDID maps:
//			String[] headings = csvReader.readNext();
//
//			// Read the rows until we get a blank for the date.
//			// After that blank line, the content is metadata about the
//			// CDIDs.
//			String[] row;
//			while ((row = csvReader.readNext()) != null) {
//
//				String path = null;
//				TimeSeries item = new TimeSeries();
//				for (int i = 0; i < Math.min(row.length, headings.length); i++) {
//					if (StringUtils.isNotBlank(row[i]) && StringUtils.isNotBlank(headings[i])) {
//						if (headings[i].trim().toLowerCase().equals("Name".toLowerCase())) {
//							item.name = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("CDID".toLowerCase())) {
//							item.cdid = row[i];
//							item.fileName = StringUtils.lowerCase(row[i]);
//						} else if (headings[i].trim().toLowerCase().equals("Seasonal adjustment".toLowerCase())) {
//							item.seasonalAdjustment = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("Units".toLowerCase())) {
//							item.units = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("Main measure".toLowerCase())) {
//							item.mainMeasure = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("Description".toLowerCase())) {
//							item.description = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("Note 1".toLowerCase())) {
//							item.note1 = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("Note 2".toLowerCase())) {
//							item.note2 = row[i];
//						} else if (headings[i].trim().toLowerCase().equals("Path".toLowerCase())) {
//							path = row[i];
//						}
//					}
//				}
//
//				// Add to the collection:
//				List<TimeSeries> collection;
//				if (!timeseries.containsKey(path)) {
//					collection = new ArrayList<>();
//					timeseries.put(path, collection);
//				} else {
//					collection = timeseries.get(path);
//				}
//				collection.add(item);
//			}
//		}
//	}
//
// }
