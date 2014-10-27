package com.github.onsdigital.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares the date tokens with the taxonomy file system where the date toekn
 * is in the format of yyyy-MM-dd
 */
public class TaxonomyDateComparator implements Comparator<Object> {

	@Override
	public int compare(Object first, Object second) {
		String firstFileName = String.valueOf(first);
		int firstDateIndex = firstFileName.lastIndexOf('/');
		String secondFileName = String.valueOf(second);
		int secondDateIndex = secondFileName.lastIndexOf('/');

		String firstDateAsString = firstFileName.substring((firstDateIndex - 10), firstDateIndex);
		String secondDateAsString = secondFileName.substring((secondDateIndex - 10), secondDateIndex);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date firstDate;
		Date secondDate;
		try {
			firstDate = dateFormat.parse(firstDateAsString);
			secondDate = dateFormat.parse(secondDateAsString);
		} catch (ParseException pe) {
			throw new RuntimeException(pe);
		}
		return firstDate.compareTo(secondDate);
	}
}
