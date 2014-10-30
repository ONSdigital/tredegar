package com.github.onsdigital.generator.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

public class Csv implements Iterable<Map<String, String>> {

	private Path path;
	private String[] headings;
	private List<String[]> rows;

	public Csv(Path path) {
		this.path = path;
	}

	public Csv(String resourceName) {
		URL resource = Csv.class.getResource(resourceName);
		try {
			this.path = Paths.get(resource.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void read() throws IOException {
		try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(Files.newInputStream(path))))) {
			rows = csvReader.readAll();
		}
	}

	public String[] getHeadings() {
		if (headings == null) {
			headings = rows.remove(0);
		}
		for (int i = 0; i < headings.length; i++) {
			headings[i] = StringUtils.trim(headings[i]);
		}
		return headings;
	}

	public void setHeadings(String[] headings) {
		this.headings = headings;
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		return new Iterator<Map<String, String>>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < rows.size();
			}

			@Override
			public Map<String, String> next() {
				String[] headings = getHeadings();
				String[] row = rows.get(index++);
				Map<String, String> result = new HashMap<>();
				for (int i = 0; i < Math.min(headings.length, row.length); i++) {
					result.put(headings[i], row[i]);
				}
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
