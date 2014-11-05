package com.github.onsdigital.json.timeseries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class TimeseriesValue implements Comparable<TimeseriesValue> {

	public String date;
	public String value;

	// Values split out into explicit components:
	public long timestamp;
	public String year;
	public String month;
	public String quarter;

	// We don't want to serialise this,
	// but it's useful to keep a cached copy because
	// the regex and Calendar work is expensive:
	private transient Date toDate;

	@Override
	public int compareTo(TimeseriesValue o) {
		return toDate().compareTo(o.toDate());
	}

	public Date toDate() {

		if (toDate == null) {
			try {
				String standardised = StringUtils.lowerCase(StringUtils.trim(date));
				if (Timeseries.year.matcher(standardised).matches()) {
					toDate = new SimpleDateFormat("yyyy").parse(standardised);
				} else if (Timeseries.month.matcher(standardised).matches()) {
					toDate = new SimpleDateFormat("yyyy MMM").parse(standardised);
				} else if (Timeseries.quarter.matcher(standardised).matches()) {
					Date parsed = new SimpleDateFormat("yyyy").parse(standardised);
					Calendar calendar = Calendar.getInstance(Locale.UK);
					calendar.setTime(parsed);
					if (standardised.endsWith("1")) {
						calendar.set(Calendar.MONTH, Calendar.JANUARY);
					} else if (standardised.endsWith("2")) {
						calendar.set(Calendar.MONTH, Calendar.APRIL);
					} else if (standardised.endsWith("3")) {
						calendar.set(Calendar.MONTH, Calendar.JULY);
					} else if (standardised.endsWith("4")) {
						calendar.set(Calendar.MONTH, Calendar.OCTOBER);
					} else {
						throw new RuntimeException("Didn't detect quarter in " + standardised);
					}
					toDate = calendar.getTime();
				} else if (Timeseries.yearInterval.matcher(standardised).matches()) {
					toDate = new SimpleDateFormat("yyyy").parse(standardised.substring("yyyy-".length()));
				} else if (Timeseries.yearPair.matcher(standardised).matches()) {
					toDate = new SimpleDateFormat("yy").parse(standardised.substring("yyyy/".length()));
				} else if (Timeseries.yearEnd.matcher(standardised).matches()) {
					toDate = new SimpleDateFormat("MMM yy").parse(standardised.substring("YE ".length()));
				} else {
					throw new ParseException("Unknown format: '" + date + "'", 0);
				}
			} catch (ParseException e) {
				throw new RuntimeException("Error parsing date: '" + date + "'", e);
			}
		}

		return toDate;
	}
}
