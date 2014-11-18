package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;

public class T1 extends TaxonomyHome {

	public List<HomeSection> sections;

	public T1(Folder folder) {
		super(folder);
		level = "t1";
		buildSections(folder);
	}

	protected void buildSections(Folder folder) {
		sections = new ArrayList<>();
		buildChild(folder.getChild("economy"), URI.create("/economy/inflationandpriceindices/timeseries/d7g7"));
		buildChild(folder.getChild("businessindustryandtrade"), URI.create("/businessindustryandtrade/internationaltrade/timeseries/ikbj"));
		buildChild(folder.getChild("employmentandlabourmarket"), URI.create("/employmentandlabourmarket/peopleinwork/employmentandemployeetypes/timeseries/lf24"));
		buildChild(folder.getChild("peoplepopulationandcommunity"), URI.create("/peoplepopulationandcommunity/populationandmigration/populationestimates/timeseries/raid121"));
	}

	private void buildChild(Folder child, URI timeseriesLink) {
		HomeSection section = new HomeSection(child.name, child.filename());
		section.items.add(timeseriesLink);
		sections.add(section);
	}
}
