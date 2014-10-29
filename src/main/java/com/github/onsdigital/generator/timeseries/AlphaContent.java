package com.github.onsdigital.generator.timeseries;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.timeseries.TimeSeries;

public class AlphaContent {

	private static List<Map<String, String>> rows;

	public static List<TimeSeries> getTimeSeries(Folder folder) throws IOException {
		List<TimeSeries> result = null;

		// Parse the data:
		if (rows == null) {
			parseCsv();
		}

		Node node = getNode(folder);
		if (node != null) {
			result = node.timeseriesList().timeserieses;
		}

		return result;
	}

	public static TimeSeries getHeadlineTimeSeries(Folder folder) throws IOException {
		TimeSeries result = null;

		// Parse the data:
		if (rows == null) {
			parseCsv();
		}

		Node node = getNode(folder);
		if (node != null) {
			result = node.timeseriesList().headline;
		}

		return result;
	}

	private static Node getNode(Folder folder) {
		Node result = null;

		// Recurse up the hierarchy to the root node:
		Node parentNode = null;
		if (folder.parent == null) {
			parentNode = Data.rootNode;
		} else {
			parentNode = getNode(folder.parent);
		}

		// Get the matching node:
		if (parentNode != null) {
			result = parentNode.getChild(folder.name);
		}

		return result;
	}

	private static void parseCsv() throws IOException {
		rows = CSV.parse("/Alpha content master.csv");
		// String[] headings = { "Theme", "Level 2", "Level 3", "Name", "Key",
		// "Units", "CDID", "Path", "Link", "Notes" }; + "Figure"

		for (Map<String, String> row : rows) {

			// There are blank rows separating the themes:
			if (StringUtils.isBlank(row.get("Theme"))) {
				continue;
			}

			// Get to the folder in question:
			Node node = Data.rootNode.getChild(row.get("Theme"));
			if (StringUtils.isNotBlank(row.get("Level 2"))) {
				node = node.getChild(row.get("Level 2"));
			}
			if (StringUtils.isNotBlank(row.get("Level 3"))) {
				node = node.getChild(row.get("Level 3"));
			}

			// Now get the Timeseries details:
			boolean isHeadline = BooleanUtils.toBoolean(row.get("Key"));
			TimeSeries timeseries = new TimeSeries();
			timeseries.cdid = StringUtils.trim(row.get("CDID"));
			timeseries.fileName = timeseries.cdid.toLowerCase();
			timeseries.name = row.get("Name");
			timeseries.unit = row.get("Units");
			if (StringUtils.isNotBlank(row.get("Figure"))) {
				timeseries.number = row.get("Figure");
			}
			node.addTimeseries(timeseries, isHeadline);
			Data.timeseries.add(timeseries);
		}
	}

	public static void main(String[] args) throws IOException {
		Folder theme = new Folder();
		theme.name = "Business, Industry and Trade";
		Folder level2 = new Folder();
		level2.name = "Retail Industry";
		level2.parent = theme;
		System.out.println(getTimeSeries(level2));
	}
}
