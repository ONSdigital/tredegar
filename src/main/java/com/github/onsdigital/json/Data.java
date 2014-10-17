package com.github.onsdigital.json;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.taxonomy.Detail;

public class Data extends TaxonomyNode {

	public String level;
	public List<TaxonomyNode> breadcrumb = new ArrayList<>();
	public List<TaxonomyNode> children;
	public List<Detail> timeseries;
	public String lede;
	public String more;

	public Data(Folder folder, int taxonomyLevel) {
		super(folder);
		Folder parent = folder;
		while ((parent = parent.parent) != null) {
			breadcrumb.add(0, new TaxonomyNode(parent));
		}

		if (folder.children.size() > 0) {
			int index = 1;
			children = new ArrayList<>();
			for (Folder child : folder.children) {
				if (taxonomyLevel == 1) {
					children.add(new ChildT1(child, child.index));
				} else {
					children.add(new ChildT2(child, child.index));
				}
			}
		} else {
			timeseries = new ArrayList<>();
		}
		sort(children);
	}

	private void sort(List<TaxonomyNode> children) {
		if (children != null) {
			List<TaxonomyNode> sortedChildren = new ArrayList<>();
			for (TaxonomyNode child : children) {
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
