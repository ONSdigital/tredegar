package com.github.onsdigital.generator.datasets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.CSV;
import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.Dataset;

public class DatasetContent {

	private static List<Map<String, String>> rows;

	public static List<Dataset> getDatasets(Folder folder) throws IOException {
		List<Dataset> result = null;

		// Parse the data:
		if (rows == null) {
			parseCsv();
		}

		DatasetNode node = getNode(folder);
		if (node != null) {
			result = node.datasetList().datasets;
		}

		return result;
	}

	private static DatasetNode getNode(Folder folder) {
		DatasetNode result = null;

		// Recurse up the hierarchy to the root node:
		DatasetNode parentNode = null;
		if (folder.parent == null) {
			parentNode = DatasetData.rootNode;
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
		rows = CSV.parse("/Alpha dataset content.csv");
		// String[] headings = { "Theme", "Level 2", "Level 3", "Name", "Key",
		// "Units", "CDID", "Path", "Link", "Notes" };

		for (Map<String, String> row : rows) {

			// There are blank rows separating the themes:
			if (StringUtils.isBlank(row.get("Theme"))) {
				continue;
			}

			// Get to the folder in question:
			DatasetNode node = DatasetData.rootNode.getChild(row.get("Theme"));
			if (StringUtils.isNotBlank(row.get("Level 2"))) {
				node = node.getChild(row.get("Level 2"));
			}
			if (StringUtils.isNotBlank(row.get("Level 3"))) {
				node = node.getChild(row.get("Level 3"));
			}

			Dataset dataset = new Dataset();
			dataset.name = StringUtils.trim(row.get("Name"));
			dataset.title = dataset.name;
			dataset.fileName = dataset.name.toLowerCase();
			node.addDataset(dataset);
			DatasetData.datasets.add(dataset);
		}
		System.out.println(DatasetData.datasets.size());
	}

	public static void main(String[] args) throws IOException {
		Folder theme = new Folder();
		theme.name = "Business, Industry and Trade";
		Folder level2 = new Folder();
		level2.name = "Business Activity, Size and Location";
		level2.parent = theme;
		System.out.println(getDatasets(level2));
	}
}
