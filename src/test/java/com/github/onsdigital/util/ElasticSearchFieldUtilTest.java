package com.github.onsdigital.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.onsdigital.search.util.ElasticSearchFieldUtil;

/**
 * Checks that formatted search field includes/excludes boost as appropriate
 */
public class ElasticSearchFieldUtilTest {

	@Test
	public void testGetBoost() {
		String boostedField = ElasticSearchFieldUtil.getBoost("title", "2");
		assertEquals("Format: boostedField^boostFactor", "title^2.0",
				boostedField);
	}

	@Test
	public void testGetEmptyBoost() {
		String boostedField = ElasticSearchFieldUtil.getBoost("title", "");
		assertEquals("Format: boostedField", "title", boostedField);
	}

	@Test
	public void testGetNullBoost() {
		String boostedField = ElasticSearchFieldUtil.getBoost("title", null);
		assertEquals("Format: boostedField", "title", boostedField);
	}
}
