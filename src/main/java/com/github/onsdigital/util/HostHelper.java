package com.github.onsdigital.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class HostHelper {

	private static String localhost = "localhost";

	public static boolean isLocalhost(HttpServletRequest request) {
		URL url = getUrl(request);
		return isLocalhost(url);
	}

	public static boolean isLocalhost(URL url) {
		return url != null && StringUtils.equalsIgnoreCase(url.getHost(), localhost);
	}

	public static URL getUrl(HttpServletRequest req) {
		try {
			return new URL(req.getRequestURL().toString());
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
