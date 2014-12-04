package com.github.onsdigital.api.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class CountTest {

	@Test
	public void shouldBeEqual() {

		// Given
		SearchConsole.QueryCount a = new SearchConsole.QueryCount("123");
		SearchConsole.QueryCount b = new SearchConsole.QueryCount("123");

		// Then
		assertEquals(a, b);
	}

	@Test
	public void shouldNotBeEqual() {

		// Given
		SearchConsole.QueryCount a = new SearchConsole.QueryCount("123");
		SearchConsole.QueryCount b = new SearchConsole.QueryCount("321");

		// Then
		assertNotEquals(a, b);
	}

	@Test
	public void shouldSort() {

		// Given
		SearchConsole.QueryCount a = new SearchConsole.QueryCount("123");
		SearchConsole.QueryCount b = new SearchConsole.QueryCount("321");
		SearchConsole.QueryCount c = new SearchConsole.QueryCount("456");

		// When
		a.count = 1;
		b.count = 3;
		c.count = 2;
		Set<SearchConsole.QueryCount> set = new TreeSet<>();
		set.add(a);
		set.add(b);
		set.add(c);

		// Then
		SearchConsole.QueryCount[] result = new SearchConsole.QueryCount[3];
		int i = 0;
		for (SearchConsole.QueryCount count : set) {
			result[i] = count;
			i++;
		}
		assertEquals("321", result[0].query);
		assertEquals("456", result[1].query);
		assertEquals("123", result[2].query);
	}
}
