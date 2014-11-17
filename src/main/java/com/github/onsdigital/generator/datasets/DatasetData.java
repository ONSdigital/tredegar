package com.github.onsdigital.generator.datasets;

import java.util.HashSet;
import java.util.Set;

import com.github.onsdigital.json.dataset.Dataset;

class DatasetData {

	static DatasetNode rootNode = new DatasetNode("root", null);
	static Set<Dataset> datasets = new HashSet<>();
}
