package com.github.onsdigital.generator.data;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.json.timeseries.TimeSeries;

class TimeseriesList {

	TimeSeries headline;
	private List<TimeSeries> timeserieses;

	public void add(TimeSeries timeseries) {
		timeserieses().add(timeseries);
	}

	public List<TimeSeries> timeserieses() {
		if (timeserieses == null) {
			timeserieses = new ArrayList<>();
		}
		return timeserieses;
	}

}
