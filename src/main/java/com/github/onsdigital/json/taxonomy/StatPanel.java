package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.FileItem;

public class StatPanel extends FileItem {

	public List<URI> timeseries = new ArrayList<>();

	public StatPanel(Folder folder) {
		super(folder);
	}

}
