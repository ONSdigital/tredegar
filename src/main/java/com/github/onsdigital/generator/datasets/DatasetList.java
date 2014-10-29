package com.github.onsdigital.generator.datasets;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.json.Dataset;

class DatasetList {

	List<Dataset> datasets;

	public void add(Dataset dataset) {
		if (datasets == null) {
			datasets = new ArrayList<>();
		}
		datasets.add(dataset);
	}

}
