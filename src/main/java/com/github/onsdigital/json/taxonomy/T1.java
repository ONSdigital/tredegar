package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.Reference;

public class T1 extends TaxonomyHome {

	public List<HomeSection> sections;

	public T1(Folder folder) {
		super(folder);
		level = "t1";
		buildSections(folder);
	}

	protected void buildSections(Folder folder) {
		sections = new ArrayList<>();
		buildChild(folder.getChild("economy"), "CPI: Consumer Prices Index", URI.create("/economy/inflationandpriceindices/timeseries/d7g7"));
		buildChild(folder.getChild("economy"), "GDP Quarter on Quarter growth (CVM)", URI.create("/economy/grossdomesticproductgdp/timeseries/ihyq"));
		buildChild(folder.getChild("businessindustryandtrade"), "Trade in goods and services deficit (or surplus)", URI.create("/businessindustryandtrade/internationaltrade/timeseries/ikbj"));
		buildChild(folder.getChild("employmentandlabourmarket"), "Employment rate (aged 16-64)", URI.create("/employmentandlabourmarket/peopleinwork/employmentandemployeetypes/timeseries/lf24"));
		buildChild(folder.getChild("peoplepopulationandcommunity"), "UK population", URI.create("/peoplepopulationandcommunity/populationandmigration/populationestimates/timeseries/raid121"));
	}

	private void buildChild(Folder child, String timeSeriesName, URI timeseriesLink) {
		HomeSection section = new HomeSection(child.name, child.filename());
		section.items.add(new Reference(timeSeriesName, timeseriesLink));
		sections.add(section);
	}
}
