package com.github.onsdigital.generator.data;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

/**
 * Handles the non-CDID data in the {@value #resourceName} spreadsheet.
 * <p>
 * This class and its members are package private (default visibility) because
 * the API doesn't need to be exposed to the rest of the application.
 * 
 * @author david
 *
 */
class NonCdidCSV {

	static final String resourceName = "/data/Data for non-CDID hero series.xlsx";

	static Set<String> timeseries = new HashSet<>();

	static void parse() throws IOException {

		Csv csv = new Csv(resourceName);
		csv.read();

		// Skip the first worksheet:
		for (int sheetIndex = 1; sheetIndex < csv.sheetCount(); sheetIndex++) {
			csv.read(sheetIndex);
			String[] cdidsRow = csv.row(6);
			for (int column = 1; column < cdidsRow.length; column++) {

				String cdid = cdidsRow[column];

				// Check whether we have a CDID in this column.
				// Sometimes more columns are detected than there are CDIDs.
				if (StringUtils.isEmpty(cdid)) {
					continue;
				}

				// Get the timeseries object for this CDID:
				Timeseries timeseries = Data.timeseries(cdid);
				if (timeseries == null) {
					// System.out.println("Sheet " + sheetIndex + ", column " +
					// c + " - new timeseries " + cdid);
					timeseries = Data.addTimeseries(cdid);
					timeseries.data = new ArrayList<>();
					// Clear the note1 and note2 fields
					timeseries.note1 = null;
					timeseries.note2 = null;
				}

				// Read the values from this column:
				data: for (int r = 0; r < csv.size(); r++) {
					String[] row = csv.row(r);
					if (row.length >= column) {

						String date = StringUtils.trim(row[0]);
						String figure = StringUtils.trim(row[column]);

						if (StringUtils.equalsIgnoreCase("Note", date)) {

							// End of data, there may be a note:
							timeseries.note1 = StringUtils.trim(row[1]);
							break data;

						} else if (StringUtils.isNotEmpty(date) && StringUtils.isNotEmpty(figure)) {

							TimeseriesValue timeseriesValue = new TimeseriesValue();

							// Prevent decimal points on the date for years,
							// e.g. 2014.0.
							// This is due to the way numbers come out of
							// Excel.
							if (date != null && date.matches("\\d+[\\.\\d+]?")) {
								date = String.valueOf((long) Double.parseDouble(date));
							}

							// Sanity-check that the figure really is a number:
							try {
								Double.parseDouble(figure);
							} catch (NumberFormatException e) {
								System.out.println(" ! The figure for " + cdid + " is " + figure);
							}

							// Give the figure a sensible format.
							// This is due to the way numbers come out of
							// Excel.
							String figureUpdated = figure;
							if (StringUtils.isNotBlank(figure) && figure.contains("E") && figure.contains(".")) {
								DecimalFormat format = new DecimalFormat("###,###,###,##0.00");
								figureUpdated = format.format(Double.parseDouble(figure));
								if (figureUpdated.endsWith(".00")) {
									figureUpdated = figureUpdated.substring(0, figureUpdated.length() - 3);
								}
							}
							// if (!StringUtils.equals(figure, figureUpdated)) {
							// System.out.println("Updated: " + figure + " -> "
							// + figureUpdated);
							// }

							timeseriesValue.date = date;
							timeseriesValue.value = figure;
							timeseries.data.add(timeseriesValue);
						}
					}
				}

				NonCdidCSV.timeseries.add(cdid);
			}
		}

	}

}
