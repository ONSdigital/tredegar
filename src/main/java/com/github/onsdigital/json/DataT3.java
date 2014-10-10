//package com.github.onsdigital.json;
//
//import java.util.ArrayList;
//
//import com.github.onsdigital.generator.Folder;
//
//public class DataT3 extends Data {
//
//	public DataT3(Folder folder) {
//		super(folder, 3);
//		level = "t3";
//		lede = "Consumer price inflation is the speed at which the prices of goods and services bought by households rise or fall.";
//		more = "Some more content goes here. This will expand and contract if you have JS enabled. If JS is disabled, "
//				+ "this will show by default. Yay progressive enhancement!";
//		timeseries = new ArrayList<>();
//
//		DetailT3 detail;
//		detail = new DetailT3("CPI Index", "126.7", "", "January 2014",
//				"18th Feb 2014", "25th June 2014");
//		detail.note = "2005 = 100. Not seasonally adjusted";
//
//		this.timeseries.add(detail);
//		detail = new DetailT3("CPIH Index", "124.7", "%", "January 2014",
//				"18th Feb 2014", "25th June 2014");
//		this.timeseries.add(detail);
//
//		detail = new DetailT3(
//				"CPI Annual Rate Percentage Change over 12 Months", "1.5", "%",
//				"January 2014", "18th Feb 2014", "25th June 2014");
//		detail.headline = true;
//		detail.explanation = "Change in the 12 month rate since last month.";
//		detail.change = "0.7%";
//		detail.changeDirection = "up";
//		detail.addStatsBulletinHeadline(
//				"The Consumer Price Index (CPI) grew by 1.5% in the year to May 2014, down from 1.8% in April.",
//				"#");
//		detail.addStatsBulletinHeadline(
//				"Fall in transport services costs, notably air fares, provided the largest contribution to the decrease in the rate.",
//				"#");
//		detail.addStatsBulletinHeadline(
//				"The timing of Easter in April is likely to have an impact on movements in the index, notably for air and sea fares.",
//				"#");
//		this.timeseries.add(detail);
//
//		detail = new DetailT3("RPI Index", "252.6 ", "", "January 2014",
//				"18th Feb 2014", "25th June 2014");
//		this.timeseries.add(detail);
//
//		detail = new DetailT3("RPIX Index excluding mortgage interest",
//				"251.9", "", "January 2014", "18th Feb 2014", "25th June 2014");
//		detail.note = "Not a national statistic";
//		this.timeseries.add(detail);
//
//	}
//
//	//
//	//
//	// More information about CPI index
//	//
//	//
//	//
// }
