package com.github.onsdigital.generator.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.timeseries.TimeSeries;

public class Data {

	private static Set<Folder> folders;
	private static Map<String, Set<TimeSeries>> datasets = new HashMap<>();
	private static Map<String, TimeSeries> timeserieses = new HashMap<>();

	public static void setTaxonomy(Set<Folder> folders) throws IOException {
		Data.folders = folders;
		// Now we have the taxonomy, parse the data:
		parse();
	}

	private static void parse() throws IOException {
		DataCSV.parse();
		MetadataCSV.parse();
		AlphaContentCSV.parse();
	}

	public static Folder getNode(String theme, String level2, String level3) {
		Folder result = null;

		Folder themeFolder = null;
		Folder level2Folder = null;
		Folder level3Folder = null;

		// Locate the theme folder
		for (Folder folder : folders) {
			if (StringUtils.equalsIgnoreCase(folder.name, theme)) {
				themeFolder = folder;
				result = folder;
			}
		}
		if (themeFolder == null) {
			throw new RuntimeException(theme + " is not a theme folder in the taxonomy.");
		}

		if (StringUtils.isNotBlank(level2)) {
			// Locate the level 2 folder
			for (Folder folder : themeFolder.getChildren()) {
				if (StringUtils.equalsIgnoreCase(folder.name, level2)) {
					level2Folder = folder;
					result = folder;
				}
			}
			if (level2Folder == null) {
				throw new RuntimeException(theme + " is not a level 2 folder in the taxonomy.");
			}
		}

		if (StringUtils.isNotBlank(level3)) {
			// Locate the level 2 folder
			for (Folder folder : level2Folder.getChildren()) {
				if (StringUtils.equalsIgnoreCase(folder.name, level3)) {
					level3Folder = folder;
					result = folder;
				}
			}
			if (level3Folder == null) {
				throw new RuntimeException(theme + " is not a level 3 folder in the taxonomy.");
			}
		}

		return result;
	}

	public static Set<TimeSeries> dataset(String name) {
		return datasets.get(toKey(name));
	}

	public static TimeSeries timeseries(String cdid) {
		return timeserieses.get(toKey(cdid));
	}

	public static void addTimeseries(TimeSeries timeseries) {
		if (timeserieses.containsKey(toKey(timeseries.cdid()))) {
			throw new IllegalArgumentException("Duplicate timeseries: " + timeseries);
		}
		timeserieses.put(toKey(timeseries.cdid()), timeseries);
	}

	public static void addDataset(String name, Set<TimeSeries> dataset) {
		if (datasets.containsKey(toKey(name))) {
			throw new IllegalArgumentException("Duplicate dataset: " + name);
		}
		datasets.put(StringUtils.lowerCase(name), dataset);
	}

	private static String toKey(String string) {
		return StringUtils.lowerCase(StringUtils.trim(string));
	}

}
