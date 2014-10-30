package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.TaxonomyHome;

public class T3 extends TaxonomyHome {

	public URI headline;
	public List<URI> items = new ArrayList<>();
	public URI statsBulletinHeadline;

	public T3(Folder folder) {
		super(folder);
		level = "t3";
		lede = "Consumer price inflation is the speed at which the prices of goods and services bought by households rise or fall.";
		more = "Some more content goes here. This will expand and contract if you have JS enabled. If JS is disabled, " + "this will show by default. Yay progressive enhancement!";

		headline = URI.create("/economy/inflationandpriceindices/timeseries/d7bt");
		items.add(headline);
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/d7g7"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/l522"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/l55o"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/chaw"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/czbh"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/kvr8"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/kvr9"));

		statsBulletinHeadline = URI.create("/economy/inflationandpriceindices/bulletins/consumerpriceinflation");
	}

}
