package com.github.onsdigital.generator.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

/**
 * This class provides a structure for holding data used in building the
 * taxonomy.
 * <p>
 * There are quite a number of CSV files that need to be parsed, so this enables
 * the information from all those files to be collected and layered into a
 * single set of objects, ready to be written to disk by the taxonomy generator.
 * 
 * @author david
 *
 */
public class Data implements Iterable<Timeseries> {

	static Map<String, String> years = new TreeMap<>();
	static Map<String, String> yearEnds = new TreeMap<>();
	static Map<String, String> yearIntervals = new TreeMap<>();
	static Map<String, String> yearPairs = new TreeMap<>();
	static Map<String, String> months = new TreeMap<>();
	static Map<String, String> quarters = new TreeMap<>();

	private static Set<Folder> folders;
	private static Map<String, Set<Timeseries>> datasets = new TreeMap<>();
	private static Map<String, Timeseries> timeserieses = new HashMap<>();
	private static Set<String> mappedDatasets = new HashSet<>();
	private static Map<String, String> timeseriesDates = new TreeMap<>();

	public static void addDateOption(String date) {

		String key = toKey(date);
		TimeseriesValue value = new TimeseriesValue();
		value.date = date;

		// Pick the correct list for this option:
		if (Timeseries.year.matcher(key).matches()) {
			years.put(key, date);
		} else if (Timeseries.yearEnd.matcher(key).matches()) {
			yearEnds.put(key, date);
		} else if (Timeseries.yearInterval.matcher(key).matches()) {
			yearIntervals.put(key, date);
		} else if (Timeseries.yearPair.matcher(key).matches()) {
			yearPairs.put(key, date);
		} else if (Timeseries.month.matcher(key).matches()) {
			months.put(key, date);
		} else if (Timeseries.quarter.matcher(key).matches()) {
			quarters.put(key, date);
		} else {
			throw new IllegalArgumentException("Unknow date format: '" + date + "'");
		}
	}

	/**
	 * Sets the taxonomy folder structure and triggers parsing of CSV data.
	 * 
	 * @param folders
	 *            The taxonomy folder structure.
	 * @throws IOException
	 *             If an error occurs in parsing CSVs.
	 */
	public static void setTaxonomy(Set<Folder> folders) throws IOException {
		Data.folders = folders;
		// Now we have the taxonomy, parse the data:
		parse();
	}

	/**
	 * Triggers parsing of {@link NonCdidCSV}, {@link DataCSV},
	 * {@link MetadataCSV} and {@link AlphaContentCSV}.
	 * 
	 * @throws IOException
	 *             If an error occurs during parsing.
	 */
	private static void parse() throws IOException {
		NonCdidCSV.parse();
		DataCSV.parse();
		MetadataCSV.parse();
		AlphaContentCSV.parse();
		DatasetMappingsCSV.parse();
		System.out.println("Parsing complete.");
	}

	/**
	 * Gets a {@link Folder} from the taxonomy, basend on a specification of
	 * theme, level2 and level3 folder names.
	 * 
	 * @param theme
	 *            The theme folder.
	 * 
	 * @param level2
	 *            The level 2 folder. Can be null if you wish to get a theme
	 *            folder.
	 * @param level3
	 *            The level 3 folder. Can be null if you wish to get a level 3
	 *            folder.
	 * @return
	 */
	public static Folder getFolder(String theme, String level2, String level3) {
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
				throw new RuntimeException(level2 + " is not a level 2 folder in the taxonomy.");
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
				throw new RuntimeException(level3 + " is not a level 3 folder in the taxonomy.");
			}
		}

		return result;
	}

	/**
	 * Gets the specified dataset.
	 * 
	 * @param name
	 *            The name of the dataset.
	 * @return The specified dataset, or null if it is not present.
	 */
	public static Set<Timeseries> dataset(String name) {

		return datasets.get(toKey(name));
	}

	/**
	 * Gets the specified timeseries.
	 * 
	 * @param cdid
	 *            The CDID identifier for the timeseries.
	 * @return The specified timeseries, or null if it is not present.
	 */
	public static Timeseries timeseries(String cdid) {
		return timeserieses.get(toKey(cdid));
	}

	/**
	 * Adds a new timeseries. If the timeseries is already present, an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param timeseries
	 *            The timeseries.
	 */
	public static void addTimeseries(Timeseries timeseries, String datasetName) {
		if (timeserieses.containsKey(toKey(timeseries.cdid()))) {
			throw new IllegalArgumentException("Duplicate timeseries: " + timeseries);
		}
		timeserieses.put(toKey(timeseries.cdid()), timeseries);

		Set<Timeseries> dataset;
		if (datasetName != null) {
			dataset = dataset(datasetName);
			if (dataset == null) {
				throw new RuntimeException("There's no dataset called " + datasetName + " to add " + timeseries + " to.");
			}
		} else {
			dataset = (dataset("other") == null) ? addDataset("other") : dataset("other");
		}
		dataset.add(timeseries);
	}

	/**
	 * Creates and adds a new timeseries. If the timeseries is already present,
	 * an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param cdid
	 *            The timeseries CDID.
	 * @return The new timeseries.
	 */
	public static Timeseries addTimeseries(String cdid, String datasetName) {
		Timeseries timeseries = new Timeseries();
		timeseries.setCdid(cdid);
		addTimeseries(timeseries, datasetName);
		return timeseries;
	}

	/**
	 * Adds a new dataset. If the dataset is already present, an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param name
	 *            The dataset name.
	 * @param dataset
	 *            The dataset.
	 */
	public static void addDataset(String name, Set<Timeseries> dataset) {
		if (datasets.containsKey(toKey(name))) {
			throw new IllegalArgumentException("Duplicate dataset: " + name);
		}
		if (name.equalsIgnoreCase("am")) {
			System.out.println("Adding am as " + dataset);
		}
		datasets.put(StringUtils.lowerCase(name), dataset);
	}

	/**
	 * Creates and adds a new dataset. If the dataset is already present, an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param name
	 *            The dataset name.
	 * @return
	 */
	public static Set<Timeseries> addDataset(String name) {
		Set<Timeseries> dataset = new HashSet<>();
		addDataset(name, dataset);
		return dataset;
	}

	public static void addMappedDataset(String name) {
		mappedDatasets.add(toKey(name));
	}

	public static Set<String> unmappedDatasets() {
		Set<String> result = new HashSet<>(datasets.keySet());
		for (String mapped : mappedDatasets) {
			result.remove(toKey(mapped));
		}
		return result;
	}

	/**
	 * @return The total number of timeseries currently held.
	 */
	public static int size() {
		return timeserieses.size();
	}

	/**
	 * @return The total number of timeseries currently held.
	 */
	public static int sizeDatasets() {
		Set<String> cdids = new HashSet<>();
		for (String dataset : datasets.keySet()) {
			cdids.add(dataset);
		}
		return timeserieses.size();
	}

	/**
	 * @return The total number of timeseries currently held.
	 */
	public static int sizeDatasetsCount() {
		return datasets.size();
	}

	/**
	 * Standardises the given value.
	 * 
	 * @param string
	 *            The value - typically a CDID.
	 * @return The value, trimmed and lowercased, or null if null was passed in.
	 */
	private static String toKey(String string) {
		return StringUtils.lowerCase(StringUtils.trim(string));
	}

	@Override
	public Iterator<Timeseries> iterator() {
		return new Iterator<Timeseries>() {

			int index = 0;
			List<String> items = new ArrayList<>(timeserieses.keySet());

			@Override
			public boolean hasNext() {
				return index < items.size();
			}

			@Override
			public Timeseries next() {
				return timeserieses.get(items.get(index++));
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
