package com.github.onsdigital.generator.datasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Csv;
import com.github.onsdigital.json.dataset.Dataset;
import com.github.onsdigital.json.dataset.DownloadSection;

public class DatasetContent {
	static final String resourceName = "/Alpha content master.xlsx";
	private static Csv rows;

	static String THEME = "Theme";
	static String LEVEL2 = "Level 2";
	static String LEVEL3 = "Level 3";
	static String NAME = "Name";
	static String SUMMARY = "Summary";
	static String DATASET_TYPE = "Dataset type";
	static String DESCRIPTION = "Description";
	static String SERIES = "Series";
	static String[] DOWNLOAD = new String[] { "download1", "download2", "download3" };
	static String[] DOWNLOAD_XLS = new String[] { "download1xls", "download2xls", "download3xls" };
	static String[] DOWNLOAD_CSV = new String[] { "download1csv", "download2csv", "download3csv" };
	static String NATIONAL_STATISTIC = "ns";
	static String[] columns = { THEME, LEVEL2, LEVEL3, NAME, SUMMARY, DATASET_TYPE, DESCRIPTION, SERIES, DOWNLOAD[0], DOWNLOAD[1], DOWNLOAD[2], DOWNLOAD_XLS[0], DOWNLOAD_XLS[1], DOWNLOAD_XLS[2],
			DOWNLOAD_CSV[0], DOWNLOAD_CSV[1], DOWNLOAD_CSV[2], NATIONAL_STATISTIC };

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
		rows = new Csv(resourceName);
		rows.read(1);
		String[] headings = rows.getHeadings();

		// Verify the headings:
		for (String column : columns) {
			if (!ArrayUtils.contains(headings, column)) {
				throw new RuntimeException("Expected a " + column + " column in " + resourceName);
			}
		}

		for (Map<String, String> row : rows) {

			// There are blank rows separating the themes:
			if (StringUtils.isBlank(row.get(THEME))) {
				continue;
			}

			// Get to the folder in question:
			DatasetNode node = DatasetData.rootNode.getChild(row.get(THEME));
			if (StringUtils.isNotBlank(row.get(LEVEL2))) {
				node = node.getChild(row.get(LEVEL2));
			}
			if (StringUtils.isNotBlank(row.get(LEVEL3))) {
				node = node.getChild(row.get(LEVEL3));
			}

			Dataset dataset = new Dataset();
			dataset.name = StringUtils.trim(row.get(NAME));
			dataset.title = dataset.name;
			dataset.fileName = sanitise(dataset.name.toLowerCase());
			if (StringUtils.isNotBlank(row.get(SUMMARY))) {
				dataset.summary = row.get(SUMMARY);
			}

			if (StringUtils.isNotBlank(row.get(SERIES))) {

				DownloadSection downloadSection = new DownloadSection();
				downloadSection.title = dataset.name;
				downloadSection.cdids = new ArrayList<String>();
				dataset.download.add(downloadSection);

				// Extract CDIDs
				// (four-character sequences of letters and numbers):
				String cdidList = row.get(SERIES);
				Pattern pattern = Pattern.compile("[A-Za-z0-9]{4}");
				Matcher matcher = pattern.matcher(cdidList);
				while (matcher.find()) {
					downloadSection.cdids.add(matcher.group());
				}
			} else if (StringUtils.isNotBlank(row.get(DOWNLOAD[0]))) {

				for (int s = 0; s < 3; s++) {
					if (StringUtils.isNotBlank(row.get(DOWNLOAD[s]))) {
						DownloadSection downloadSection = new DownloadSection();
						downloadSection.title = row.get(DOWNLOAD[s]);
						String xls = row.get(DOWNLOAD_XLS[s]);
						String csv = row.get(DOWNLOAD_CSV[s]);
						if (StringUtils.isNotBlank(xls)) {
							downloadSection.xls = xls;
						}
						if (StringUtils.isNotBlank(csv)) {
							downloadSection.csv = csv;
						}
						dataset.download.add(downloadSection);
					}
				}

			} else if (StringUtils.isNotBlank(row.get("Link (latest)"))) {
				DownloadSection downloadSection = new DownloadSection();
				downloadSection.title = dataset.name;
				downloadSection.xls = row.get("Link (latest)");
				dataset.download.add(downloadSection);
			}

			if (StringUtils.isNotBlank(row.get(DESCRIPTION))) {
				dataset.description = row.get(DESCRIPTION);
			}

			node.addDataset(dataset);
			DatasetData.datasets.add(dataset);
		}
		System.out.println(DatasetData.datasets.size());
	}

	/**
	 * <a href=
	 * "http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars/13293384#13293384"
	 * >http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-
	 * java-method-to-remove-filename-special-chars/13293384#13293384</a>
	 * 
	 * @param name
	 * @return
	 */
	private static String sanitise(String name) {
		StringBuilder result = new StringBuilder();
		for (char c : name.toCharArray()) {
			if (c == '.' || Character.isJavaIdentifierPart(c)) {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static void main(String[] args) {
		String cdidList = "chaw, A9ER, cpsk";

		Pattern pattern = Pattern.compile("[A-Za-z0-9]{4}");
		Matcher matcher = pattern.matcher(cdidList);
		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.print(" End index: " + matcher.end() + " ");
			System.out.println(matcher.group());
		}
	}
}
