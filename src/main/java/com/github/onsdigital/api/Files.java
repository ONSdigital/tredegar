package com.github.onsdigital.api;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.framework.Filter;

public class Files implements Filter {

	@Override
	public boolean filter(HttpServletRequest req, HttpServletResponse res) {

		try {
			URL url = new URL(req.getRequestURL().toString());

			// Add a five-minute cache time to static files to reduce
			// round-trips to
			// the server and increase performance whilst still allowing the
			// system
			// to be updated quite promptly if necessary:
			if (!StringUtils.equalsIgnoreCase("localhost", url.getHost())) {
				res.addHeader("cache-control", "public, max-age=300");
			}

			// Allow cross-origin resource sharing for css. js. and img.
			// subbomains:
			res.addHeader("Access-Control-Allow-Origin", trimSubdomain(url));

		} catch (MalformedURLException e) {

			// Not much to be done if the request URL somehow turns out to be
			// invalid:
			System.out.println("Invalid request URL: " + req.getRequestURL());
			e.printStackTrace();
		}

		return true;
	}

	static String trimSubdomain(URL url) {

		String protocol = url.getProtocol();
		String host = url.getHost();
		String port = "";
		String[] domainLevels = host.split("\\.");
		if (url.getPort() > 0) {
			port = ":" + url.getPort();
		}
		if (StringUtils.equals("css", domainLevels[0]) || StringUtils.equals("js", domainLevels[0]) || StringUtils.equals("img", domainLevels[0])) {
			domainLevels = ArrayUtils.subarray(domainLevels, 1, domainLevels.length);
			host = StringUtils.join(domainLevels, ".");
		}
		return protocol + "://" + host + port;
	}
}
