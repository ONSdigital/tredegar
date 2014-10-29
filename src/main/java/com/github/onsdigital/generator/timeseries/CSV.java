package com.github.onsdigital.generator.timeseries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;

/**
 * Straightforward CSV reader class.
 * 
 * @author david
 *
 */
class CSV {

	public static List<Map<String, String>> parse(String resourceName) throws IOException {
		List<Map<String, String>> result = new ArrayList<>();

		try (Reader reader = ResourceUtils.getReader(resourceName)) {
			try (CSVReader csvReader = new CSVReader(new BufferedReader(reader))) {
				String[] headings = csvReader.readNext();

				String[] row;
				while ((row = csvReader.readNext()) != null) {
					Map<String, String> values = new HashMap<>();
					for (int i = 0; i < Math.min(row.length, headings.length); i++) {
						values.put(headings[i], row[i]);
					}
					result.add(values);
				}
			}
		}

		return result;
	}
}
