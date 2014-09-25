package com.github.onsdigital.generator.json;

import com.github.onsdigital.generator.Folder;

public class TaxonomyNode {

	public String name;
	public String fileName;

	TaxonomyNode(Folder folder) {
		name = folder.name;
		fileName = folder.filename();
	}
}
