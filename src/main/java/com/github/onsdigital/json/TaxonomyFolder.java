package com.github.onsdigital.json;

import com.github.onsdigital.generator.Folder;

public class TaxonomyFolder extends DataItem {
	public int index;

	TaxonomyFolder(Folder folder) {
		type = ContentType.home;
		name = folder.name;
		fileName = folder.filename();
	}
}
