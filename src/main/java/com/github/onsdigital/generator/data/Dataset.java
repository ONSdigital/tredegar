package com.github.onsdigital.generator.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.timeseries.TimeSeries;

public class Dataset implements Iterable<TimeSeries> {

	private String name;
	private Map<String, TimeSeries> timeserieses = new HashMap<>();

	public Dataset(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void addTimeseries(String cdid) {
		TimeSeries result = timeserieses.get(StringUtils.lowerCase(cdid));
		if (result == null) {
			result = new TimeSeries();
			result.setCdid(cdid);
		}
		timeserieses.put(StringUtils.lowerCase(result.cdid()), result);
	}

	public TimeSeries timeseries(String cdid) {
		return timeserieses.get(StringUtils.lowerCase(cdid));
	}

	@Override
	public Iterator<TimeSeries> iterator() {
		return timeserieses.values().iterator();
	}
}
