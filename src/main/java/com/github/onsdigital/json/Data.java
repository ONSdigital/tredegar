package com.github.onsdigital.json;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.taxonomy.Detail;

public class Data extends TaxonomyFolder {

	public List<TaxonomyFolder> children;
	public List<Detail> timeseries;

	public Data(Folder folder, int taxonomyLevel) {
		super(folder);
	}

	protected void sort(List<TaxonomyFolder> children) {
		if (children != null) {
			List<TaxonomyFolder> sortedChildren = new ArrayList<>();
			for (TaxonomyFolder child : children) {
				// String current = "";
				// for (TaxonomyNode c : sortedChildren) {
				// current += c.index + " ";
				// }
				// System.out.println("Current: " + current + " Adding: " +
				// child.index);
				int index = 0;
				for (int i = 0; i < sortedChildren.size(); i++) {
					if (child.index > sortedChildren.get(i).index) {
						index = i + 1;
					}
				}
				sortedChildren.add(index, child);
				// String now = "";
				// for (TaxonomyNode c : sortedChildren) {
				// now += c.index + " ";
				// }
				// System.out.println("Now: " + now);
			}
			this.children = sortedChildren;
		}
	}
}
