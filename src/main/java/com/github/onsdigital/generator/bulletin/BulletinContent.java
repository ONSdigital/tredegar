package com.github.onsdigital.generator.bulletin;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.data.Csv;
import com.github.onsdigital.json.bulletin.Bulletin;

public class BulletinContent {
	static final String resourceName = "/Alpha content master.xlsx";
	private static Csv rows;

	// public static List<Bulletin> getBulletins(Folder folder) throws
	// IOException {
	// List<Bulletin> result = null;
	//
	// // Parse the data:
	// if (rows == null) {
	// parseCsv();
	// }
	//
	// BulletinNode node = getNode(folder);
	// if (node != null) {
	// result = node.bulletinList().bulletins;
	// }
	//
	// return result;
	// }
	//
	// public static Bulletin getHeadlineBulletin(Folder folder) throws
	// IOException {
	// Bulletin result = null;
	//
	// if (rows == null) {
	// parseCsv();
	// }
	//
	// BulletinNode node = getNode(folder);
	// if (node != null) {
	// result = node.bulletinList().headline;
	// }
	//
	// return result;
	// }
	//
	// private static BulletinNode getNode(Folder folder) {
	// BulletinNode result = null;
	//
	// // Recurse up the hierarchy to the root node:
	// BulletinNode parentNode = null;
	// if (folder.parent == null) {
	// parentNode = BulletinData.rootNode;
	// } else {
	// parentNode = getNode(folder.parent);
	// }
	//
	// // Get the matching node:
	// if (parentNode != null) {
	// result = parentNode.getChild(folder.name);
	// }
	//
	// return result;
	// }

	/**
	 * This method is no longer responsible for generating bulletins during
	 * taxonomy generation.
	 * <p>
	 * It now just parses the CSV and generates markdown for the bulletins.
	 * <p>
	 * This should only be a one-time process, so you should never need to call
	 * this unless you want to update the initial format of the bulletin
	 * markdown.
	 * 
	 * @throws IOException
	 */
	public static void parseCsv() throws IOException {
		rows = new Csv(resourceName);
		rows.read(2);
		rows.getHeadings();
		// String[] headings = { "Theme", "Level 2", "Level 3", "Name", "Key",
		// "Units", "CDID", "Path", "Link", "Notes" };

		for (Map<String, String> row : rows) {

			// There are blank rows separating the themes:
			if (StringUtils.isBlank(row.get("Theme"))) {
				continue;
			}

			// Get to the folder in question:
			BulletinNode node = BulletinData.rootNode.getChild(row.get("Theme"));
			if (StringUtils.isNotBlank(row.get("Level 2"))) {
				node = node.getChild(row.get("Level 2"));
			}
			if (StringUtils.isNotBlank(row.get("Level 3"))) {
				node = node.getChild(row.get("Level 3"));
			}

			Bulletin bulletin = new Bulletin();
			bulletin.name = StringUtils.trim(row.get("Name"));
			bulletin.title = bulletin.name;
			bulletin.fileName = bulletin.name.toLowerCase();

			// boolean isHeadline = false;
			if (StringUtils.isNotBlank(row.get("Headline1"))) {
				// isHeadline = true;
				bulletin.headline1 = row.get("Headline1");
			}

			if (StringUtils.isNotBlank(row.get("Headline2"))) {
				bulletin.headline2 = row.get("Headline2");
			}

			if (StringUtils.isNotBlank(row.get("Headline3"))) {
				bulletin.headline3 = row.get("Headline3");
			}

			if (StringUtils.isNotBlank(row.get("Summary"))) {
				bulletin.summary = row.get("Summary");
			}

			StringBuilder properties = new StringBuilder();
			addProperty("Theme", row.get("Theme"), properties);
			addProperty("Level 2", row.get("Level 2"), properties);
			addProperty("Level 3", row.get("Level 3"), properties);
			addProperty("Lede", "", properties);
			addProperty("More", "", properties);
			addProperty("Summary", row.get("Summary"), properties);
			addProperty("Headline 1", row.get("Headline1"), properties);
			addProperty("Headline 2", row.get("Headline2"), properties);
			addProperty("Headline 3", row.get("Headline3"), properties);
			addProperty("Contact name", "Jukesie", properties);
			addProperty("Contact email", "jukesie@hotmail.com", properties);
			addProperty("Next release", row.get("Period"), properties);
			properties.append("\n");
			properties.append("# ");
			properties.append(row.get("Name"));
			properties.append("\n\n## Introduction");
			properties.append("\n\nLorem ipsum dolor sit amet.");
			properties.append("\n\n## Section two");
			properties.append("\n\nLorem ipsum dolor sit amet.");
			properties.append("\n\n## Section three");
			properties.append("\n\nLorem ipsum dolor sit amet.");

			String filename = toFilename(bulletin);
			if (StringUtils.isNotBlank(filename)) {
				Path path = Paths.get("src/main/resources/bulletins/" + filename + ".md");
				System.out.println("Writing bulletin to: " + path);
				try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path), "UTF8")) {
					Reader reader = new StringReader(properties.toString());
					IOUtils.copy(reader, writer);
				}
			}

			// node.addBulletin(bulletin, isHeadline);
			// BulletinData.bulletins.add(bulletin);
		}
		System.out.println(BulletinData.bulletins.size());
	}

	static void addProperty(String key, String value, StringBuilder result) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			result.append(key);
			result.append(": ");
			result.append(value);
			result.append("\n");
		}
	}

	/**
	 * Sanitises a bulletin name to <code>[a-zA-Z0-9]</code>.
	 * 
	 * @param name
	 *            The string to be sanitised.
	 * @return A sanitised string.
	 */
	public static String toFilename(Bulletin bulletin) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < bulletin.name.length(); i++) {
			String character = bulletin.name.substring(i, i + 1);
			if (character.matches("[a-zA-Z0-9]")) {
				result.append(character);
			}
		}
		return result.toString().toLowerCase();
	}

	// public static void main(String[] args) throws IOException {
	// Folder theme = new Folder();
	// theme.name = "Economy";
	// Folder level2 = new Folder();
	// level2.name = "Inflation and Price Indices";
	// level2.parent = theme;
	// System.out.println(getBulletins(level2));
	// }
}
