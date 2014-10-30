package com.github.onsdigital.generator.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.timeseries.Timeseries;

public class Dataset implements Iterable<Timeseries> {

	private String name;
	private Map<String, Timeseries> timeserieses = new HashMap<>();

	public Dataset(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public void addTimeseries(String cdid) {
		Timeseries result = timeserieses.get(StringUtils.lowerCase(cdid));
		if (result == null) {
			result = new Timeseries();
			result.setCdid(cdid);
		}
		timeserieses.put(StringUtils.lowerCase(result.cdid()), result);
	}

	public Timeseries timeseries(String cdid) {
		return timeserieses.get(StringUtils.lowerCase(cdid));
	}

	@Override
	public Iterator<Timeseries> iterator() {
		return timeserieses.values().iterator();
	}
}
