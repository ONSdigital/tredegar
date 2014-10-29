package com.github.onsdigital.generator.timeseries;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.json.timeseries.TimeSeries;

class TimeseriesList {

	TimeSeries headline;
	List<TimeSeries> timeserieses;

	public void add(TimeSeries timeseries) {
		if (timeserieses == null) {
			timeserieses = new ArrayList<>();
		}
		timeserieses.add(timeseries);
	}

}
