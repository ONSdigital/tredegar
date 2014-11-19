package com.github.onsdigital.generator.data;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.timeseries.Timeseries;

/**
 * Handles the {@value #resourceName} CSV file.
 * <p>
 * This class and its members are package private (default visibility) because
 * the API doesn't need to be exposed to the rest of the application.
 * 
 * @author david
 *
 */
class AlphaContentCSV {

	static final String resourceName = "/Alpha content master.xlsx";

	static String THEME = "Theme";
	static String LEVEL2 = "Level 2";
	static String LEVEL3 = "Level 3";
	static String NAME = "Name";
	static String KEY = "Key";
	static String PREUNIT = "Pre unit";
	static String UNITS = "Units";
	static String FIGURE = "Figure";
	static String SCALE_FACTOR = "csv scale factor";
	static String PERIOD = "Period";
	static String CDID = "CDID";
	static String RELATED_CDID = "Related CDID";
	static String SOURCE = "Source";
	static String KEY_NOTE = "key note";
	static String[] columns = { THEME, LEVEL2, LEVEL3, NAME, KEY, PREUNIT, UNITS, FIGURE, SCALE_FACTOR, PERIOD, CDID, RELATED_CDID, SOURCE, KEY_NOTE };

	static Csv sheet;

	/**
	 * Parses the CSV and validates headings.
	 * 
	 * @throws IOException
	 */
	public static void parse() throws IOException {

		// Read the first worksheet - "Data":
		sheet = new Csv(resourceName);
		sheet.read(0);
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

			String cdid = row.get(CDID);

			// Get the timeseries to work with:
			Timeseries timeseries = Data.timeseries(cdid);
			if (timeseries == null) {
				// We haven't seen this one before, so add it:
				timeseries = Data.addTimeseries(cdid);
			}

			// Set the URI if necessary:
			Folder folder = Data.getFolder(row.get(THEME), row.get(LEVEL2), row.get(LEVEL3));
			if (timeseries.uri == null) {
				timeseries.uri = toUri(folder, timeseries);
			}

			// Set the other properties:
			timeseries.name = row.get(NAME);
			if (BooleanUtils.toBoolean(row.get(KEY))) {
				folder.headline = timeseries;
			}
			folder.timeserieses.add(timeseries);
			timeseries.preUnit = row.get(PREUNIT);
			timeseries.unit = row.get(UNITS);

			// Clean up numbers - this is because of the way they come out of
			// Excel.
			String date = row.get(PERIOD);
			if (date.endsWith(".0")) {
				date = date.substring(0, date.length() - 2);
			}
			// else {
			// System.out.println(date);
			// }
			timeseries.date = date;

			// Give the figure a sensible format.
			// This is due to the way numbers come out of
			// Excel.
			String figure = row.get(FIGURE);
			if (StringUtils.isNotBlank(figure) && figure.contains("E") && figure.contains(".")) {
				DecimalFormat format = new DecimalFormat("###,###,###,##0.00");
				figure = format.format(Double.parseDouble(figure));
			}
			if (figure.endsWith(".00")) {
				figure = figure.substring(0, figure.length() - 3);
			}
			if (figure.endsWith(".0")) {
				figure = figure.substring(0, figure.length() - 2);
			}
			timeseries.number = figure;
			String scaleFactor = row.get(SCALE_FACTOR);
			if (StringUtils.isNotBlank(scaleFactor)) {
				timeseries.setScaleFactor(Integer.parseInt(scaleFactor));
			}

			String relatedCdidColumn = row.get(RELATED_CDID);
			if (StringUtils.isNotBlank(relatedCdidColumn)) {
				String[] relatedCdidTokens = relatedCdidColumn.split(",");
				List<Timeseries> relatedTimeserieses = new ArrayList<Timeseries>();
				for (String relatedCdid : relatedCdidTokens) {
					Timeseries relatedTimeseries = Data.timeseries(relatedCdid);
					if (relatedTimeseries == null) {
						// We haven't seen this one before, so add it:
						relatedTimeseries = Data.addTimeseries(relatedCdid.trim());
					}
					relatedTimeserieses.add(relatedTimeseries);
				}
				Data.addRelatedTimeseries(timeseries, relatedTimeserieses);
			}

			String source = row.get(SOURCE);
			if (StringUtils.isNotBlank(source)) {
				timeseries.source = source;
			}

			String keyNote = row.get(KEY_NOTE);
			if (StringUtils.isNotBlank(keyNote)) {
				timeseries.keyNote = keyNote;
			}
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
