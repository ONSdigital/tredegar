package com.github.onsdigital.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpStatus;

/**
 * 
 * Serves data files in xls or csv format
 * 
 */
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.bean.DateVal;
import com.github.onsdigital.bean.DownloadRequest;
import com.github.onsdigital.data.DataService;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;
import com.github.onsdigital.util.CSVGenerator;
import com.github.onsdigital.util.XLSXGenerator;

@Endpoint
public class Download {

	@POST
	public void post(@Context HttpServletRequest request, @Context HttpServletResponse response, DownloadRequest downloadRequest) throws IOException {
		try {
			System.out.println("Download request recieved" + downloadRequest);
			response.setHeader("Content-Disposition", "attachment; filename=\"data." + downloadRequest.type + "\"");
			response.setCharacterEncoding("UTF8");
			response.setContentType("application/" + downloadRequest.type);
			processRequest(response.getOutputStream(), downloadRequest);
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setContentType("text/plain");
			response.getWriter().write("An error occured while processing this download request");
		}
	}

	private void processRequest(OutputStream output, DownloadRequest downloadRequest) throws IOException {

		// Normally only uriList or cdidList should be present in the request,
		// but let's be lenient in what we'll accept:
		List<Timeseries> timeseries = new ArrayList<Timeseries>();
		
		// Process URIs
		if (downloadRequest.uriList != null) {
			for (String uri : downloadRequest.uriList) {
				try (InputStream input = DataService.getDataStream(uri)) {
					timeseries.add(Serialiser.deserialise(input, Timeseries.class));
				}
			}
		}

		// Process CDIDs
		if (downloadRequest.cdidList != null) {
			Map<String, Timeseries> timeseriesMap = Cdid.getTimeseries(downloadRequest.cdidList);
			for (Timeseries timeseries2 : timeseriesMap.values()) {
				timeseries.add(timeseries2);
			}
		}

		// Collate into a "grid":
		Map<String, TimeseriesValue[]> data = collateData(timeseries, downloadRequest);

		// Apply the range:
		Date from = toDate(downloadRequest.from);
		Date to = toDate(downloadRequest.to);
		data = applyRange(data, from, to);

		// Debug:
		System.out.println("Data grid:");
		System.out.println("---");
		for (Entry<String, TimeseriesValue[]> row : data.entrySet()) {
			System.out.print(row.getKey());
			for (TimeseriesValue value : row.getValue()) {
				System.out.print("\t" + (value == null ? "null" : value.value));
			}
			System.out.println();
		}
		System.out.println("---");

		switch (downloadRequest.type) {
		case "xlsx":
			new XLSXGenerator(timeseries, data).write(output);
		case "csv":
			new CSVGenerator(timeseries, data).write(output);
		default:
			break;
		}

	}

	private Date toDate(DateVal from) {
		Date result = null;
		if (from != null) {
			String date = String.valueOf(from.year);
			if (StringUtils.isNotBlank(from.month)) {
				date += " " + from.month;
			}
			if (StringUtils.isNotBlank(from.quarter)) {
				date += " " + from.quarter;
			}
			result = TimeseriesValue.toDate(date);
		}
		return result;
	}

	/**
	 * Collates data from the given timeseries into an ordered map. This
	 * provides a "data grid" suitable for writing out in tabular format.
	 * 
	 * @param timeseriesList
	 * @return
	 */
	private Map<String, TimeseriesValue[]> collateData(List<Timeseries> timeseriesList, DownloadRequest downloadRequest) {

		// We want an ordered map of date strings -> values as the result:
		Map<String, TimeseriesValue[]> result = new LinkedHashMap<>();

		boolean year = true;
		boolean quarter = true;
		boolean month = true;

		if (downloadRequest.from != null) {
			if (StringUtils.isNotBlank(downloadRequest.from.quarter)) {
				year = false;
				month = false;
			} else if (StringUtils.isNotBlank(downloadRequest.from.month)) {
				year = false;
				quarter = false;
			} else if (downloadRequest.from.year > 0) {
				quarter = false;
				month = false;
			}
		}

		if (year) {
			// Collate years:
			Map<Date, TimeseriesValue[]> years = new TreeMap<>();
			for (int l = 0; l < timeseriesList.size(); l++) {
				for (TimeseriesValue value : timeseriesList.get(l).years) {
					addValue(value, l, timeseriesList, years);
				}
			}
			addToResult(years, result);
		}

		if (quarter) {
			// Collate quarters:
			Map<Date, TimeseriesValue[]> quarters = new TreeMap<>();
			for (int l = 0; l < timeseriesList.size(); l++) {
				for (TimeseriesValue value : timeseriesList.get(l).quarters) {
					addValue(value, l, timeseriesList, quarters);
				}
			}
			addToResult(quarters, result);
		}

		if (month) {
			// Collate years:
			Map<Date, TimeseriesValue[]> months = new TreeMap<>();
			for (int l = 0; l < timeseriesList.size(); l++) {
				for (TimeseriesValue value : timeseriesList.get(l).months) {
					addValue(value, l, timeseriesList, months);
				}
			}
			addToResult(months, result);
		}

		return result;
	}

	private Map<String, TimeseriesValue[]> applyRange(Map<String, TimeseriesValue[]> data, Date from, Date to) {

		// We want an ordered map of date strings -> values as the result:
		Map<String, TimeseriesValue[]> result = new LinkedHashMap<>();

		boolean add = false;
		for (String key : data.keySet()) {
			Date date = TimeseriesValue.toDate(key);
			// Start adding if no from date has been specified:
			if ((!add && from == null) || date.equals(from)) {
				System.out.println("Starting range at " + key);
				add = true;
			}
			if (add) {
				// System.out.print(".");
				result.put(key, data.get(key));
			}
			if (date.equals(to)) {
				System.out.println("Ending range at " + key);
				break;
			}
		}

		return result;
	}

	/**
	 * Adds a single timeseries value to a data block (yearly, quarterly or
	 * monthly).
	 * 
	 * @param value
	 *            The value to be added.
	 * @param listIndex
	 *            The index of the current timeseries within the collection of
	 *            timeseries to be downloaded.
	 * @param timeseriesList
	 *            The collection of timeseries to be downloaded - used to get a
	 *            length for the array.
	 * @param data
	 *            The map into which the value will be added.
	 */
	private void addValue(TimeseriesValue value, int listIndex, List<Timeseries> timeseriesList, Map<Date, TimeseriesValue[]> data) {

		Date key = value.toDate();

		// Ensure we have a "row" for this date:
		if (!data.containsKey(key)) {
			data.put(key, new TimeseriesValue[timeseriesList.size()]);
		}

		// Put the value into the "grid":
		data.get(key)[listIndex] = value;
	}

	/**
	 * Adds a block of data to the overall result.
	 * 
	 * @param valuesMap
	 *            The block of data to be added.
	 * @param result
	 *            The overall map that the data wil be added to.
	 */
	private void addToResult(Map<Date, TimeseriesValue[]> valuesMap, Map<String, TimeseriesValue[]> result) {

		for (TimeseriesValue[] values : valuesMap.values()) {

			// Select a value to use as the overall row date:
			String date = null;
			key: for (TimeseriesValue value : values) {
				if (value != null) {
					date = value.date;
					break key;
				}
			}

			// Add this row to the result:
			result.put(date, values);
		}
	}

}
