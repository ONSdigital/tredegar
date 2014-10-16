package com.github.onsdigital.json;

import com.github.onsdigital.generator.Folder;

public class TaxonomyNode {

	public String name;
	public String fileName;
	public int index;

	TaxonomyNode(Folder folder) {
		name = folder.name;
		fileName = folder.filename();
	}
}
