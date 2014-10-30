package com.github.onsdigital.generator.data;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.json.timeseries.Timeseries;

class TimeseriesList {

	Timeseries headline;
	private List<Timeseries> timeserieses;

	public void add(Timeseries timeseries) {
		timeserieses().add(timeseries);
	}

	public List<Timeseries> timeserieses() {
		if (timeserieses == null) {
			timeserieses = new ArrayList<>();
		}
		return timeserieses;
	}

}
