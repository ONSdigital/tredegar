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
		Count a = new Count("123");
		Count b = new Count("123");

		// Then
		assertEquals(a, b);
	}

	@Test
	public void shouldNotBeEqual() {

		// Given
		Count a = new Count("123");
		Count b = new Count("321");

		// Then
		assertNotEquals(a, b);
	}

	@Test
	public void shouldSort() {

		// Given
		Count a = new Count("123");
		Count b = new Count("321");
		Count c = new Count("456");

		// When
		a.count = 1;
		b.count = 3;
		c.count = 2;
		Set<Count> set = new TreeSet<>();
		set.add(a);
		set.add(b);
		set.add(c);

		// Then
		Count[] result = new Count[3];
		int i = 0;
		for (Count count : set) {
			result[i] = count;
			i++;
		}
		assertEquals("321", result[0].value);
		assertEquals("456", result[1].value);
		assertEquals("123", result[2].value);
	}
}
