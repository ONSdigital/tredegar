package com.github.onsdigital.json.taxonomy;

import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.Introduction;

public class T3 extends TaxonomyNode {

	public T3(Folder folder) {
		super(folder, "t3");
		timeseries = new ArrayList<>();
		introduction = new Introduction();
		introduction.lede = "Consumer price inflation is the speed at which the prices of goods and services bought by households rise or fall.";
		introduction.more = "Some more content goes here. This will expand and contract if you have JS enabled. If JS is disabled, "
				+ "this will show by default. Yay progressive enhancement!";
	}
}
