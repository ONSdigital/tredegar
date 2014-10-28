package com.github.onsdigital.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaxonomyDateComparatorTest {

	@Test
	public void testCompare() {
		String date1AsString = "target/taxonomy/economy/inflationandpriceindices/bulletins/producerpriceinflation/2014-1-21/data.json";
		String date2AsString = "target/taxonomy/economy/inflationandpriceindices/bulletins/producerpriceinflation/2013-12-21/data.json";

		TaxonomyDateComparator dateComparator1 = new TaxonomyDateComparator();
		int result1 = dateComparator1.compare(date1AsString, date2AsString);
		assertEquals("date1 should be ranked higher", 1, result1);

		String date3AsString = "target/taxonomy/economy/inflationandpriceindices/bulletins/producerpriceinflation/2014-08-21/data.json";
		String date4AsString = "target/taxonomy/economy/inflationandpriceindices/bulletins/producerpriceinflation/2014-09-21/data.json";

		TaxonomyDateComparator dateComparator2 = new TaxonomyDateComparator();
		int result2 = dateComparator2.compare(date3AsString, date4AsString);
		assertTrue("date4 should be ranked higher", result2 < 0);
		
		String date5AsString = "target/taxonomy/economy/inflationandpriceindices/bulletins/producerpriceinflation/2014-12-21/data.json";
		String date6AsString = "target/taxonomy/economy/inflationandpriceindices/bulletins/producerpriceinflation/2014-12-21/data.json";

		TaxonomyDateComparator dateComparator3 = new TaxonomyDateComparator();
		int result3 = dateComparator3.compare(date5AsString, date6AsString);
		assertEquals("date5 and date6 should be equal", 0, result3);
	}
}
