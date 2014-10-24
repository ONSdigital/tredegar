package com.github.onsdigital.prerender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import com.github.davidcarboni.restolino.Configuration;
import com.github.greengerong.PreRenderSEOFilter;

/**
 * Most of this code is taken from {@link PreRenderSEOFilter}. It's pretty
 * unpleasant at the moment because it's cut-and-paste with tweaks to get it
 * working. This will need to be revised.
 * 
 * @see <a
 *      href="https://github.com/greengerong/prerender-java">prerender-java</a>
 *      on Github.
 * @author david
 *
 */
public class PrerenderIo {

	public static boolean handle(HttpServletRequest request, HttpServletResponse response) {
		boolean result;

		try {

			URL url = new URL(request.getRequestURL().toString());

			// Build an _escaped_fragment_ URL:
			String escapedFragment;
			if (url.getPath().startsWith("/static")) {
				escapedFragment = extractStaticUrl(url);
			} else {
				escapedFragment = extractHashBangUrl(url);
			}
			URL escapedFragmetUrl = buildUrl(url, escapedFragment);
			escapedFragmetUrl = updateForLocalhost(escapedFragmetUrl);

			System.out.println("URL for Prerender.io is: " + escapedFragmetUrl);

			Map<String, String> config = new HashMap<String, String>();
			config.put("prerenderToken", "cCc113eXWWV2TbRcnoMV");
			PrerenderIoService prerenderSeoService = new PrerenderIoService(config);
			try {
				// return prerenderSeoService.prerenderIfEligible(request,
				// response);
				return prerenderSeoService.proxyPrerenderedPageResponse(request, response, escapedFragmetUrl);
			} finally {
				prerenderSeoService.destroy();
			}
			// fetch(escapedFragmetnUrl, response);
			// return true;

		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	/**
	 * Updates the URL to tredegar.herokuapp.com if we're running on localhost.
	 * 
	 * @param escapedFragmetnUrl
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @return The URL, updated if necessary.
	 */
	private static URL updateForLocalhost(URL escapedFragmetnUrl) throws MalformedURLException, URISyntaxException {
		URL result = escapedFragmetnUrl;

		if (StringUtils.equals(escapedFragmetnUrl.getHost(), "localhost")) {
			System.out.println("Updating localhost to tredegar");
			result = new URIBuilder(escapedFragmetnUrl.toURI()).setUserInfo("stats:titchfield").setHost("tredegar.herokuapp.com").setPort(-1).build().toURL();
			System.out.println("URL for Prerender.io is now: " + escapedFragmetnUrl);
		}

		return result;
	}

	private static URL buildUrl(URL url, String escapedFragment) throws MalformedURLException, URISyntaxException {

		URIBuilder uriBuilder = new URIBuilder();

		// Protocol
		uriBuilder.setScheme(url.getProtocol());

		// User information
		Configuration configuration = new Configuration();
		if (StringUtils.isNotBlank(configuration.username)) {
			uriBuilder.setUserInfo(configuration.username, configuration.password);
		}

		// Host
		uriBuilder.setHost(url.getHost());

		// Port
		if (url.getPort() != 80 && url.getPort() != 443) {
			uriBuilder.setPort(url.getPort());
		}

		// Path
		uriBuilder.setPath("/");

		// Query
		uriBuilder.setParameter("_escaped_fragment_", escapedFragment);

		return uriBuilder.build().toURL();
	}

	private static String extractStaticUrl(URL url) throws URISyntaxException {

		String escapedFragment = url.getPath();
		if (StringUtils.startsWith(escapedFragment, "/static")) {
			escapedFragment = escapedFragment.substring("/static".length());
		}
		return escapedFragment;
	}

	private static String extractHashBangUrl(URL url) throws URISyntaxException {

		String escapedFragment = url.getRef();
		if (escapedFragment.startsWith("!")) {
			escapedFragment = escapedFragment.substring(1);
		}
		return escapedFragment;
	}
}
