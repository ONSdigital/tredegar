package com.github.onsdigital.configuration;

import org.apache.commons.lang3.StringUtils;

public class Configuration {

	private static final String DEFAULT_TAXONOMY_ROOT = "target/taxonomy";

	/**
	 * Mongo is currently only used to provide feedback on the search terms
	 * users are typing in.
	 */
	private static final String DEFAULT_MONGO_URI = "mongodb://localhost:27017/db";

	/**
	 * David Carboni: This token relates to a Prerender.io accout I set up. If
	 * necessary this account can be transferred, or a new one set up for ONS.
	 */
	private static final String DEFAULT_PRERENDER_TOKEN = "cCc113eXWWV2TbRcnoMV";

	public static String getTaxonomyPath() {
		return StringUtils.defaultIfBlank(getValue("TAXONOMY_DIR"), DEFAULT_TAXONOMY_ROOT);
	}

	public static String getPrerenderToken() {
		return StringUtils.defaultIfBlank(getValue("PRERENDER_TOKEN"), DEFAULT_PRERENDER_TOKEN);
	}

	public static String getMongoDbUri() {
		return StringUtils.defaultIfBlank(getValue("MONGOLAB_URI"), DEFAULT_MONGO_URI);
	}

	/**
	 * Gets a configured value for the given key from either the system
	 * properties or an environment variable.
	 * <p>
	 * Copied from {@link com.github.davidcarboni.restolino.Configuration}.
	 * 
	 * @param key
	 *            The name of the configuration value.
	 * @return The system property corresponding to the given key (e.g.
	 *         -Dkey=value). If that is blank, the environment variable
	 *         corresponding to the given key (e.g. EXPORT key=value). If that
	 *         is blank, {@link StringUtils#EMPTY}.
	 */
	static String getValue(String key) {
		String result = StringUtils.defaultIfBlank(System.getProperty(key), StringUtils.EMPTY);
		result = StringUtils.defaultIfBlank(result, System.getenv(key));
		return result;
	}

}
