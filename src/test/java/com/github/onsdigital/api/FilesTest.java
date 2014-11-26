package com.github.onsdigital.api;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class FilesTest {

	@Test
	public void sholudHandleLocalhostAndPort() throws MalformedURLException {

		// Given
		String expected = "http://localhost:8080";
		URL url = new URL("http://localhost:8080/#!/peoplepopulationandcommunity/healthandsocialcare");

		// When
		String actual = Files.trimSubdomain(url);

		// Then
		assertEquals(expected, actual);
	}

	@Test
	public void sholudHandleHeroku() throws MalformedURLException {

		// Given
		String expected = "http://onsdigital.herokuapp.com";
		URL url = new URL("http://onsdigital.herokuapp.com/#!/economy");

		// When
		String actual = Files.trimSubdomain(url);

		// Then
		assertEquals(expected, actual);
	}

	@Test
	public void sholudHandleHttps() throws MalformedURLException {

		// Given
		String expected = "https://onsdigital.herokuapp.com";
		URL url = new URL("https://onsdigital.herokuapp.com/#!/economy");

		// When
		String actual = Files.trimSubdomain(url);

		// Then
		assertEquals(expected, actual);
	}

	@Test
	public void sholudTrimSubdomain() throws MalformedURLException {

		// Given
		String expected = "http://onsdigital.herokuapp.com";
		URL url = new URL("http://js.onsdigital.herokuapp.com/#!/economy");

		// When
		String actual = Files.trimSubdomain(url);

		// Then
		assertEquals(expected, actual);
	}

}
