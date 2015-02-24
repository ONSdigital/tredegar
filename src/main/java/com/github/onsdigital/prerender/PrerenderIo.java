package com.github.onsdigital.prerender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.github.greengerong.PreRenderSEOFilter;
import com.github.onsdigital.configuration.Configuration;

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

	public static String handle(HttpServletRequest request, HttpServletResponse response) {
		String result = null;

		try {

			URL url = new URL(request.getRequestURL().toString());

			// Build an _escaped_fragment_ URL:
			String escapedFragment;
			if (url.getPath().startsWith("/static")) {
				escapedFragment = extractStaticUrl(url);
			} else {
				escapedFragment = extractHashBangUrl(url);
			}
			if (StringUtils.isEmpty(escapedFragment)) {
				escapedFragment = "/";
			}
			URL escapedFragmetUrl = buildUrl(url, escapedFragment, request.getQueryString());
			escapedFragmetUrl = updateForLocalhost(escapedFragmetUrl);

			System.out.println("URL for Prerender.io is: " + escapedFragmetUrl);

			Map<String, String> config = new HashMap<String, String>();
			config.put("prerenderToken", Configuration.getPrerenderToken());
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
			result = new URIBuilder(escapedFragmetnUrl.toURI()).setUserInfo("stats:titchfield").setHost("tredegar.herokuapp.com").setPort(-1).build().toURL();
		}

		return result;
	}

	private static URL buildUrl(URL url, String escapedFragment, String query) throws MalformedURLException, URISyntaxException {

		URIBuilder uriBuilder = new URIBuilder();

		// Protocol
		uriBuilder.setScheme(url.getProtocol());

		// User information
		// This will be easier to access once we move past Restolino 0.0.x
		com.github.davidcarboni.restolino.Configuration configuration = new com.github.davidcarboni.restolino.Configuration();
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

		// Query - start with existing parameters so that we keep things like
		// search queries:
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("prerender", "true"));
		if (StringUtils.isNotBlank(query)) {
			parameters.add(new BasicNameValuePair("_escaped_fragment_", escapedFragment + "?" + query));
		} else {
			parameters.add(new BasicNameValuePair("_escaped_fragment_", escapedFragment));
		}
		uriBuilder.setParameters(parameters);

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
