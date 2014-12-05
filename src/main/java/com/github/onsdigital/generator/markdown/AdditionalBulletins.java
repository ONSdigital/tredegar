package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Csv;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Bulletin;
import com.github.onsdigital.json.timeseries.Timeseries;

public class AdditionalBulletins {

	static final String resourceName = "/Alpha content master.xlsx";

	static String THEME = "Theme";
	static String LEVEL2 = "Level 2";
	static String LEVEL3 = "Level 3";
	static String ADDITIONAL_BULLETIN = "additional bulletin";
	static String[] columns = { THEME, LEVEL2, LEVEL3, ADDITIONAL_BULLETIN };

	static Csv sheet;

	/**
	 * Parses the CSV and validates headings.
	 * 
	 * @throws IOException
	 */
	public static void parse() throws IOException {

		// Read the first worksheet - "Data":
		sheet = new Csv(resourceName);
		sheet.read(2);
		String[] headings = sheet.getHeadings();

		// Verify the headings:
		for (String column : columns) {
			if (!ArrayUtils.contains(headings, column)) {
				throw new RuntimeException("Expected a " + column + " column in " + resourceName);
			}
		}

		// Process the rows
		for (Map<String, String> row : sheet) {

			// There are blank lines in the CSV that separate theme sections:
			if (StringUtils.isBlank(row.get(THEME))) {
				continue;
			}

			// Get the folder:
			Folder folder = Data.getFolder(row.get(THEME), row.get(LEVEL2), row.get(LEVEL3));

			// Get the bulletin:
			Bulletin bulletin = BulletinMarkdown.bulletins.get(row.get(ADDITIONAL_BULLETIN));

			folder.additonalBulletin = bulletin;
		}
	}

	static URI toUri(Folder folder, Timeseries timeseries) {
		URI result = null;

		if (timeseries != null) {
			if (timeseries.uri == null) {
				String baseUri = "/" + folder.filename();
				Folder parent = folder.parent;
				while (parent != null) {
					baseUri = "/" + parent.filename() + baseUri;
					parent = parent.parent;
				}
				baseUri += "/timeseries";
				timeseries.uri = URI.create(baseUri + "/" + StringUtils.trim(timeseries.fileName));
			}
			result = timeseries.uri;
		}

		return result;
	}
}
