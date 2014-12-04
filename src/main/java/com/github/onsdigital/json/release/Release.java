package com.github.onsdigital.json.release;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.DataItem;
import com.github.onsdigital.json.dataset.Dataset;
import com.github.onsdigital.json.markdown.Article;
import com.github.onsdigital.json.markdown.Bulletin;
import com.github.onsdigital.json.partial.Contact;
import com.github.onsdigital.json.taxonomy.T3;

/**
 * Java entity representation of the json that holds links to the articles,
 * bulletins and datasets that make up a published release
 */
public class Release extends DataItem {
	// Top section
	public String releaseDate = "21 October 2014";
	public String nextRelease = "21 January 2015";
	public Contact contact = new Contact();

	// Exec summary
	public String lede = "The Producer Price Index (PPI) is a monthly survey that measures the price changes of goods bought and sold by UK manufacturers and provides a key measure of inflation, "
			+ "alongside other indicatrs such as the Consume Price Index (CPI) and Services Producer Price Index (SPPI).";
	public String more = "Annual output and input price changes follow similar trends, but with the latter tending to have higher price growth1. In Figure 1, trends in producer price inflation can "
			+ "be split into three distinct periods: 2000 to 2005, 2005 to 2012 and 2012 to June 2014.  Both indices experience greater variability within the second period of 2005 to 2012 than compared with the first and third period. From 2005 to 2012 the average growth rate was 2.9% for output price inflation and 8.1% for input price inflation.  The largest peaks and troughs experienced for both indices throughout the time series shown below occurred in the second period of 2005 to 2012. Output price inflation rose to 8.9% in July 2008 while input price inflation rose to 34.8% in June 2008 and both fell to their lowest rates in July 2009: output price inflation falling by 1.6% and input price inflation falling by 14.8%.";

	// Sections
	public List<ReleaseSection> articles = new ArrayList<>();
	public List<ReleaseSection> bulletins = new ArrayList<>();
	public List<ReleaseSection> datasets = new ArrayList<>();

	public Release(T3 t3, Folder folder) {

		type = ContentType.release;
		lede = t3.lede;
		more = t3.more;
		name = t3.name;
		fileName = t3.fileName;

		for (Article article : folder.articles) {
			articles.add(new ReleaseSection(article));
		}
		for (Bulletin bulletin : folder.bulletins) {
			bulletins.add(new ReleaseSection(bulletin));
		}
		if (folder.datasets != null) {
			for (Dataset dataset : folder.datasets) {
				datasets.add(new ReleaseSection(dataset));
			}
		}
	}

}
