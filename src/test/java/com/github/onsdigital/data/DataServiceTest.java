package com.github.onsdigital.data;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.github.onsdigital.api.taxonomy.Data;
import com.github.onsdigital.data.DataService;

public class DataServiceTest {

	Data data = new Data();

	@Test
	public void shouldLowercase() {

		// Given
		String uri = "cats/MEOW";

		// When
		String path = DataService.cleanPath(URI.create(uri));

		// Then
		assertEquals(uri.toLowerCase(), path);
	}

	@Test
	public void shouldRemoveTrailingSlash() {

		// Given
		String uri = "dogs/woof/";

		// When
		String path = DataService.cleanPath(URI.create(uri));

		// Then
		assertEquals(uri.substring(0, uri.length() - 1), path);
	}

	@Test
	public void shouldRemoveEndpointName() {

		// Given
		String uri = "/data/is/awesome";

		// When
		String path = DataService.cleanPath(URI.create(uri));

		// Then
		assertEquals(uri.replace("/data/", ""), path);
	}

}
