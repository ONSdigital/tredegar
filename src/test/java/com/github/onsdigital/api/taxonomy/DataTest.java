package com.github.onsdigital.api.taxonomy;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

public class DataTest {

	String prefix = "home/";
	Data data = new Data();

	@Test
	public void shouldLowercase() {

		// Given
		String uri = "/cats.com/UPPERCASE";

		// When
		String path = data.cleanPath(URI.create(uri));

		// Then
		assertEquals(prefix + uri.toLowerCase(), path);
	}

	@Test
	public void shouldRemoveTrailingSlash() {

		// Given
		String uri = "/dogs.com/woof/";

		// When
		String path = data.cleanPath(URI.create(uri));

		// Then
		assertEquals(prefix + uri.substring(0, uri.length() - 1), path);
	}

	@Test
	public void shouldRemoveEndpointName() {

		// Given
		String uri = "/data/deers";

		// When
		String path = data.cleanPath(URI.create(uri));

		// Then
		assertEquals(uri.replace("/data/", "home/"), path);
	}

}
