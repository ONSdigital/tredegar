package com.github.onsdigital.json;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;

public class Data extends TaxonomyNode {

	public Data(Folder folder) {
		super(folder);
		Folder parent = folder;
		while ((parent = parent.parent) != null) {
			breadcrumb.add(0, new TaxonomyNode(parent));
		}
		for (Folder child : folder.children) {
			children.add(new TaxonomyNode(child));
		}
	}

	public List<TaxonomyNode> breadcrumb = new ArrayList<>();
	public List<TaxonomyNode> children = new ArrayList<>();

}
