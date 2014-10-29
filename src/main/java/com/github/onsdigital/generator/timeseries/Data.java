package com.github.onsdigital.generator.timeseries;

import java.util.HashSet;
import java.util.Set;

import com.github.onsdigital.json.timeseries.TimeSeries;

class Data {

	static Node rootNode = new Node("root", null);
	static Set<TimeSeries> timeseries = new HashSet<>();
	static Set<Dataset> datasets = new HashSet<>();
}
