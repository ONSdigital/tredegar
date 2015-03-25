package com.github.onsdigital.api;

import com.github.davidcarboni.restolino.framework.Filter;
import com.github.onsdigital.util.HostHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class Files implements Filter {

	static final int maxAge = 300;

	/**
	 * Adds a default {@value #maxAge}s max-age cache header to static content
	 * requests.
	 */
	@Override
    public boolean filter(HttpServletRequest req, HttpServletResponse res) {

        if (isStaticContentRequest(req)) {

            URL url = HostHelper.getUrl(req);

            // Add a five-minute cache time to static files to reduce
            // round-trips to the server and increase performance whilst still
            // allowing the system to be updated quite promptly if necessary:
            if (!HostHelper.isLocalhost(url)) {
                res.addHeader("cache-control", "public, max-age=" + maxAge);
            }

            // Allow cross-origin resource sharing for css. js. and img.
            // subbomains:
            res.addHeader("Access-Control-Allow-Origin", trimSubdomain(url));
        }

        return true;
    }

	/**
	 * A request is considered to be a static content request if there is a file
	 * extension present.
	 * 
	 * @param req
	 *            The request.
	 * @return If the result of {@link FilenameUtils#getExtension(String)} is
	 *         not blank, true.
	 */
	private boolean isStaticContentRequest(HttpServletRequest req) {
		String requestURI = req.getRequestURI();
		String extension = FilenameUtils.getExtension(requestURI);
		return StringUtils.isNotBlank(extension);
	}

	/**
	 * Removes the first segment of the hostname from the given url if the host
	 * part has a subdomain of css, js or img.
	 * 
	 * @param url
	 *            The URL to check.
	 * @return A string containing <code>protocol://host[:port]</code>, suitable
	 *         for use in an Access-Control-Allow-Origin http header.
	 */
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
