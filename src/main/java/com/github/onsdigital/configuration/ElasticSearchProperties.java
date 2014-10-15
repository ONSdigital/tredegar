package com.github.onsdigital.configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * Single element enum type that implements singleton. (reference - Josh Bloch
 * Effective Java(2) Item 3)
 */
public enum ElasticSearchProperties {
	// single constant must be enforced, i.e. only INSTANCE, nothing else
	INSTANCE;
	private final Properties properties;

	ElasticSearchProperties() {
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(
					"/ons-elasticsearch.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load search properties file",
					e);
		}
	}

	/**
	 * @param key
	 *            the property to search for
	 * @return the value of this property
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}
}
