package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.FileItem;
import com.github.onsdigital.json.Introduction;

public class TaxonomyNode extends FileItem {

	public String level;
	public List<FileItem> breadcrumb = new ArrayList<>();

	// Used on T2 and T3:
	public Introduction introduction;

	// Used on T1 and T3:
	public URI headlineStat;

	// Used on T1 and T2:
	public List<StatPanel> children = new ArrayList<>();

	// Used on T3:
	public List<URI> timeseries;

	public TaxonomyNode(Folder folder, String level) {
		super(folder);
		this.level = level;
		name = folder.name;
		fileName = folder.filename();
		buildBreadcrumb(folder);
	}

	void buildBreadcrumb(Folder folder) {
		Folder parent = folder;
		while ((parent = parent.parent) != null) {
			breadcrumb.add(0, new FileItem(parent));
		}
	}
}
