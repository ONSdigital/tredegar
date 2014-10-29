package com.github.onsdigital.generator.timeseries;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.timeseries.TimeSeries;

class Data {

	static Node rootNode = new Node("root", null);
	private static Map<String, TimeSeries> timeseries = new HashMap<>();
	static Set<Dataset> datasets = new HashSet<>();

	public static TimeSeries timeseries(String cdid) throws IOException {
		TimeSeries result = null;
		String key = StringUtils.lowerCase(cdid);

		if (cdid != null) {

			// Prefer the map:
			result = timeseries.get(key);

			// Fall back to TimeseriesMetadataCSV
			if (result == null) {
				result = TimeseriesMetadataCSV.getData(key);
				put(key, result);
			}

			// Otherwise go further afield
			if (result == null) {
				result = TimeseriesMetadataCSV.getData(key);
				put(key, result);
			}

			// As a last resort, create a new instance:
			if (result == null) {
				result = new TimeSeries();
				result.setCdid(cdid);
				put(key, result);
			}
		}

		return result;
	}

	/**
	 * Adds a timeseries to {@link #timeseries}, providing the key does not
	 * exist already and the value is not null.
	 * 
	 * @param key
	 * @param value
	 */
	private static void put(String key, TimeSeries value) {

		if (value != null) {
			// This is getting mixed up during the loading process, so sort it
			// out
			// here for now.
			// TODO: Need to parse CSVs in a more orderly way:
			if (StringUtils.isBlank(value.cdid()) && StringUtils.isNotBlank(value.fileName)) {
				value.setCdid(value.fileName);
			} else if (StringUtils.isNotBlank(value.cdid()) && StringUtils.isBlank(value.fileName)) {
				value.fileName = value.cdid();
			}

			if (!timeseries.containsKey(key) && value != null) {
				System.out.println(Data.class.getSimpleName() + ": NEW timeseries: " + key + " " + value);
				timeseries.put(key, value);
			} else {
				System.out.println(Data.class.getSimpleName() + ": Attempt to add duplicate timeseries: " + key + " " + value);
			}
		}
	}
}
