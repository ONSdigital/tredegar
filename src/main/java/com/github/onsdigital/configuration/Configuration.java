package com.github.onsdigital.configuration;

import org.apache.commons.lang3.StringUtils;

public class Configuration {

	private static final String DEFAULT_TAXONOMY_ROOT = "target/classes/files/taxonomy";

	public static String getTaxonomyPath() {
		String path = System.getenv("TAXONOMY_DIR");
		if (StringUtils.isEmpty(path)) {
			return DEFAULT_TAXONOMY_ROOT;
		}
		return path;
	}

}
