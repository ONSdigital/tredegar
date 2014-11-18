package com.github.onsdigital.json.release;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.partial.Contact;
import com.github.onsdigital.json.taxonomy.TaxonomyHome;

/**
 * Java entity representation of the json that holds links to the articles,
 * bulletins and datasets that make up a published release
 */
public class Release extends TaxonomyHome {
	// Top section
	public String releaseDate = "21 October 2014";
	public String nextRelease = "21 November 2014";
	public Contact contact = new Contact();

	// Exec summary
	public String lede = "The Producer Price Index (PPI) is a monthly survey that measures the price changes of goods bought and sold by UK manufacturers and provides a key measure of inflation, "
			+ "alongside other indicatrs such as the Consume Price Index (CPI) and Services Producer Price Index (SPPI).";
	public String more = "Annual output and input price changes follow similar trends, but with the latter tending to have higher price growth1. In Figure 1, trends in producer price inflation can "
			+ "be split into three distinct periods: 2000 to 2005, 2005 to 2012 and 2012 to June 2014.  Both indices experience greater variability within the second period of 2005 to 2012 than compared with the first and third period. From 2005 to 2012 the average growth rate was 2.9% for output price inflation and 8.1% for input price inflation.  The largest peaks and troughs experienced for both indices throughout the time series shown below occurred in the second period of 2005 to 2012. Output price inflation rose to 8.9% in July 2008 while input price inflation rose to 34.8% in June 2008 and both fell to their lowest rates in July 2009: output price inflation falling by 1.6% and input price inflation falling by 14.8%.";

	// Sections
	public List<ReleaseSection> articlesAndBulletins = new ArrayList<ReleaseSection>();
	public List<ReleaseSection> datasets = new ArrayList<ReleaseSection>();

	public Release(Folder folder) {
		super(folder);
		type = ContentType.release;

		ReleaseSection bulletin = new ReleaseSection();
		bulletin.title = "Producer Price Inflation, September 2014";
		bulletin.summary = "This statistical bulletin contains producer price indices of materials and fuels purchased and output of manufacturing.";
		bulletin.url = "/economy/inflationandpriceindices/bulletins";
		bulletin.type = ContentType.bulletin.name();
		articlesAndBulletins.add(bulletin);

		ReleaseSection dataset = new ReleaseSection();
		dataset.title = "Producer Price Index Summary dataset";
		dataset.summary = "A comprehensive selection of data on input and output indices. Contains producer price indices of materials and fuels purchased.";
		dataset.url = "/economy/inflationandpriceindices/timeseries";
		datasets.add(dataset);
	}
}
