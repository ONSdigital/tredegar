package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.helpers.QueryString;

public class DatasetMappingsCSV {

	static String resourceName = "Alpha content (completed first draft)";
	static String theme = "Theme";
	static String level2 = "Level 2";
	static String level3 = "Level 3";
	static String name = "Name";
	static String key = "Key";
	static String units = "Units";
	static String cdid = "CDID";
	static String link = "Link";
	static String notes = "Notes";
	static String[] headings = new String[] { theme, level2, level3, name, key, units, cdid, link, notes };
	static List<Map<String, String>> rows;

	static Collection<String> getCdids(String dataset) throws IOException {

		// Keep CDIDs alphabetical for convenience,
		// but ensure we de-duplicate:
		Collection<String> cdids = new TreeSet<String>();

		for (Map<String, String> row : getRows()) {
			System.out.println(row.get(link));
			URI uri = URI.create(row.get(link));
			System.out.println("Checking " + uri);
			QueryString queryString = new QueryString(uri);
			System.out.println(dataset + "?=" + queryString.get("dataset"));
			if (StringUtils.equals(dataset, queryString.get("dataset"))) {
				cdids.add(row.get(cdid));
			}
		}

		return cdids;
	}

	// static Collection<String> getDatasets(Folder taxonomyNode) throws
	// IOException {
	//
	// }

	private static List<Map<String, String>> getRows() throws IOException {

		if (rows == null) {
			parseRows();
		}

		return rows;
	}

	private static void parseRows() throws IOException {

		rows = new ArrayList<>();

		try (InputStream input = ResourceUtils.getStream("/Alpha content (completed first draft).csv")) {
			Reader reader = new InputStreamReader(input, "CP1252");
			readFile(reader);
		}
	}

	private static void readFile(Reader reader) throws IOException {

		try (CSVReader csvReader = new CSVReader(reader)) {

			// Verify the headings:
			String[] headings = csvReader.readNext();
			if (!Arrays.equals(DatasetMappingsCSV.headings, headings)) {
				throw new RuntimeException("CSV headings don't match the expected headings. Expected " + ArrayUtils.toString(DatasetMappingsCSV.headings) + " but found "
						+ ArrayUtils.toString(headings));
			}

			// Read in the CSV rows:
			String[] row;
			while ((row = csvReader.readNext()) != null) {
				Map<String, String> values = new HashMap<>();
				for (int i = 0; i < headings.length; i++) {
					values.put(DatasetMappingsCSV.headings[i], row[i]);
				}
				rows.add(values);
			}
		}
	}

}
