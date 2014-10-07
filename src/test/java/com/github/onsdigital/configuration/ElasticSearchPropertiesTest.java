package com.github.onsdigital.configuration;

import static org.junit.Assert.assertFalse;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class ElasticSearchPropertiesTest {

	@Test
	public void testGetProperty() {
		assertFalse(StringUtils.isEmpty((String) ElasticSearchProperties.INSTANCE.getProperty("title")));
	}
}
