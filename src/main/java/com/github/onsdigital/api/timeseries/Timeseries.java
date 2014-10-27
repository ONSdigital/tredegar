package com.github.onsdigital.api.timeseries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.helpers.Path;
import com.github.onsdigital.generator.TimeseriesData;
import com.github.onsdigital.json.timeseries.TimeSeriesValue;

@Endpoint
public class Timeseries {

	@GET
	public List<TimeSeriesValue> getData(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String cdid = Path.newInstance(request).lastSegment();
		Set<TimeSeriesValue> data = TimeseriesData.getDataMaps().get(StringUtils.lowerCase(cdid));
		System.out.println(TimeseriesData.getDataMaps().keySet());
		return new ArrayList<TimeSeriesValue>(data);
	}
}
