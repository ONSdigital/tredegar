package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.Introduction;

public class T2 extends TaxonomyNode {

	public T2(Folder folder) {
		super(folder, "t2");
		children = new ArrayList<>();
		introduction = new Introduction();
		introduction.lede = "Producer Price Inflation (PPI) measures the price changes of goods bought and "
				+ "sold by UK manufacturers this month compared to the same month a year ago. PPI provides a "
				+ "key measure of inflation alongside other indicators such as the Consumer Prices Index (CPI) "
				+ "and Services Producer Price Index (SPPI)."
				+ "PPI is split into two components: output price inflation and input price inflation. The "
				+ "input price indices capture changes in the cost of the material and fuel inputs that "
				+ "producers face whereas the output price indices capture the changing prices of goods sold "
				+ "by producers."
				+ "This article looks at the trends in both producer price indices since 2000 and which "
				+ "components contribute most towards changes in each price index in recent years.";
		introduction.more = "Annual output and input price changes follow similar trends, but with the "
				+ "latter tending to have higher price growth1. In Figure 1, trends in producer price "
				+ "inflation can be split into three distinct periods: 2000 to 2005, 2005 to 2012 and 2012 to "
				+ "June 2014.  Both indices experience greater variability within the second period of 2005 "
				+ "to 2012 than compared with the first and third period. From 2005 to 2012 the average growth "
				+ "rate was 2.9% for output price inflation and 8.1% for input price inflation.  The largest "
				+ "peaks and troughs experienced for both indices throughout the time series shown below "
				+ "occurred in the second period of 2005 to 2012. Output price inflation rose to 8.9% in July "
				+ "2008 while input price inflation rose to 34.8% in June 2008 and both fell to their lowest "
				+ "rates in July 2009: output price inflation falling by 1.6% and input price inflation "
				+ "falling by 14.8%.";
		buildChildren(folder);
	}

	void buildChildren(Folder folder) {
		if (folder.children.size() > 0) {
			for (Folder child : folder.children) {
				StatPanel statPanel = new StatPanel(child);
				statPanel.timeseries
						.add(URI.create("/home/economy/inflationandpriceindices/timeseries/d7bt"));
				children.add(statPanel);
			}
		}
	}
}