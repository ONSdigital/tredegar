package com.github.onsdigital.generator.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
public class Data {

	private static Set<Folder> folders;
	private static Map<String, Set<Timeseries>> datasets = new HashMap<>();
	private static Map<String, Timeseries> timeserieses = new HashMap<>();

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
	public static void addTimeseries(Timeseries timeseries) {
		if (timeserieses.containsKey(toKey(timeseries.cdid()))) {
			throw new IllegalArgumentException("Duplicate timeseries: " + timeseries);
		}
		timeserieses.put(toKey(timeseries.cdid()), timeseries);
	}

	/**
	 * Creates and adds a new timeseries. If the timeseries is already present,
	 * an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param cdid
	 *            The timeseries CDID.
	 * @return The new timeseries.
	 */
	public static Timeseries addTimeseries(String cdid) {
		Timeseries timeseries = new Timeseries();
		timeseries.setCdid(cdid);
		addTimeseries(timeseries);
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
		datasets.put(StringUtils.lowerCase(name), dataset);
	}

	/**
	 * @return The total number of timeseries currently held.
	 */
	public static int size() {
		return timeserieses.size();
	}

	public static Collection<String> getDateLabels() {
		Set<String> result = new TreeSet<>();

		for (Timeseries timeseries : timeserieses.values()) {
			if (timeseries.data != null) {
				for (TimeseriesValue value : timeseries.data) {
					result.add(value.date);
				}
			}
		}

		List<String> list = new ArrayList<String>(result);
		Collections.sort(list, new Comparator<String>() {

			String[] shortMonth = { "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
			String[] fullMonth = { "january", "febuary", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december" };
			String year = "\\d{4}(\\.\\d)?";
			String yearMonth = "\\d{4} \\w{3}";

			@Override
			public int compare(String o1, String o2) {

				String o1Standard = o1.toLowerCase();
				String o2Standard = o2.toLowerCase();

				if (o1Standard.matches(year)) {
					o1Standard = "year: " + o1Standard;
				}
				if (o2Standard.matches(year)) {
					o2Standard = "year: " + o2Standard;
				}
				if (o1Standard.matches(yearMonth)) {
					o1Standard = "year month: " + o1Standard;
				}
				if (o2Standard.matches(yearMonth)) {
					o2Standard = "year month: " + o2Standard;
				}

				o1Standard = replaceMonth(o1Standard);
				o2Standard = replaceMonth(o2Standard);

				return o1Standard.compareTo(o2Standard);
			}

			private String replaceMonth(String date) {
				String result = date;
				for (int i = 0; i < 12; i++) {
					String number = String.valueOf(i);
					if (number.length() < 2) {
						number = "0" + number;
					}
					if (result.contains(fullMonth[i])) {
						result.replace(fullMonth[i], number);
					}
					if (result.contains(shortMonth[i])) {
						result.replace(shortMonth[i], number);
					}
				}
				return result;
			}

		});

		return list;
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

}
