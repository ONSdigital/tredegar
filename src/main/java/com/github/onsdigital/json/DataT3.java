package com.github.onsdigital.json;

import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;

public class DataT3 extends Data {

	public DataT3(Folder folder) {
		super(folder, 3);
		level = "t3";
		lede = "Consumer price inflation is the speed at which the prices of goods and services bought by households rise or fall.";
		more = "Some more content goes here. This will expand and contract if you have JS enabled. If JS is disabled, "
				+ "this will show by default. Yay progressive enhancement!";
		timeSeries = new ArrayList<>();

		DetailT3 detail;
		detail = new DetailT3("Total population (UK) ", "64.1", "m",
				"Mid-2013 estimate", "18th Fex 2014", "25th June 2014");
		detail.headline = true;
		this.timeSeries.add(detail);
		detail = new DetailT3("CPI Index", "212,000", "%",
				"Year ending Dec 2014", "18th Fex 2014", "25th June 2014");
		detail.note = "2005 = 100. Not seasonally adjusted";
		this.timeSeries.add(detail);
		detail = new DetailT3("Number of Births (UK) ", "812,970 ", "", "2013",
				"18th Fex 2014", "25th June 2014");
		this.timeSeries.add(detail);
	}

}
