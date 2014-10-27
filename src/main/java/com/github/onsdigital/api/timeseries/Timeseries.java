package com.github.onsdigital.api.timeseries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.helpers.Path;
import com.github.onsdigital.generator.TimeseriesData;

@Endpoint
public class Timeseries {

	@GET
	public List<Map<String, String>> getData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Map<String, String>> result = null;

		String cdid = Path.newInstance(request).lastSegment();
		Map<String, String> data = TimeseriesData.getDataMaps().get(StringUtils.lowerCase(cdid));
		System.out.println(TimeseriesData.getDataMaps().keySet());
		if (data == null) {
			response.setStatus(HttpStatus.SC_NOT_FOUND);
		} else {
			result = new ArrayList<>();
			for (Entry<String, String> entry : data.entrySet()) {
				Map<String, String> value = new HashMap<>();
				value.put(entry.getKey(), entry.getValue());
				result.add(value);
			}
		}
		return result;
	}
}
