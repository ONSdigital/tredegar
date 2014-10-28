package com.github.onsdigital.json.timeseries;

import java.util.List;

import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.DataItem;

public class TimeSeries extends DataItem {

	// Spreadsheet headings
	public String cdid = "aaaa";
	public String seasonalAdjustment = "Not seasonally adjusted";
	public String units = "Weights (parts per 1000)";
	public String mainMeasure = "Consumer Prices Index";
	public String description = "Consumer price indices are important indicators of how the UK economy is performing.\n"
			+ "The indices are used in many ways by the government, businesses, and society in general. They can affect interest rates, tax allowances, wages, state benefits, pensions, maintenance, contracts and many other payments. They also show the impact of inflation on family budgets and affect the value of the pound in your pocket.";
	public String note1 = "In accordance with the Statistics and Registration Service Act 2007, the Retail Prices Index and its derivatives have been assessed against the Code of Practice for Official Statistics and found not to meet the required standards for designation as National Statistics.";
	public String note2 = "Changes in the internal purchasing power of a currency may be defined as  the 'inverse' of changes in the levels of prices; when prices go up, the  amount which can be purchased with a given sum of money goes down. The  monthly figures of the all items RPI can be used to obtain estimates of the  changes in prices or in purchasing power between any 2 months. To find the  purchasing power of the pound in one month, given that it was 100p in a  previous month the calculation is:  100p multiplied by the earlier month RPI then divided by the later month RPI.";

	// Additional information
	public String number = "126.7";
	public String unit = "m";
	public String date = "January 2014";
	public String lastUpated = "16 Sep 2014";
	public String nextUpdate = "14 Oct 2014";

	// e.g. "Not a national statistic" or "2005 = 100. Not seasonally adjusted"
	public String note = "2005 = 100. Not seasonally adjusted";

	// The nectar, the goodness, the very juice of the fireflower: data.
	public List<TimeSeriesValue> data;

	public TimeSeries() {
		type = ContentType.timeseries;
		name = "People not in Work";
	}
}
