package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;

public class T1 extends TaxonomyNode {

	public T1(Folder folder) {
		super(folder, "t1");
		children = new ArrayList<>();
		headlineStat = URI
				.create("/home/economy/inflationandpriceindices/timeseries/d7bt");
		buildChildren(folder);
	}

	void buildChildren(Folder folder) {
		if (folder.children.size() > 0) {
			for (Folder child : folder.children) {
				StatPanel statPanel = new StatPanel(child);
				statPanel.timeseries.add(headlineStat);
				children.add(statPanel);
			}
		}
	}
}
