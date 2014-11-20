package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.onsdigital.json.markdown.Section;

public class MarkdownTest {

	@Test
	public void shouldExtractTitle() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = "# \t" + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldExtractTitleWithWeirdContent() {

		// Given
		String title = "Title titel titre # titolo";
		String markdown = "# \t" + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldNotExtractTitleIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNull(result);
	}

	@Test
	public void shouldNotExtractTitleWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;

		// When
		String result = Markdown.matchTitle(markdown);

		// Then
		assertNull(result);
	}

	@Test
	public void shouldExtractHeading() {

		// Given
		String heading = "Head thing";
		String markdown = "## \t" + heading;

		// When
		Section section = Markdown.matchHeading(markdown);

		// Then
		assertNotNull(section);
		assertEquals(heading, section.title);
	}

	@Test
	public void shouldExtractHeadingWithWeirdContent() {

		// Given
		String title = "This will make your head ## thing # spin";
		String markdown = "## \t" + title;

		// When
		Section section = Markdown.matchHeading(markdown);

		// Then
		assertNotNull(section);
		assertEquals(title, section.title);
	}

	@Test
	public void shouldNotExtractHeadingIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;

		// When
		Section section = Markdown.matchHeading(markdown);

		// Then
		assertNull(section);
	}

	@Test
	public void shouldNotExtractSectionWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " ## " + title;

		// When
		Section section = Markdown.matchHeading(markdown);

		// Then
		assertNull(section);
	}

	@Test
	public void shouldReadProperty() {

		// Given
		String name = "name";
		String value = "value";
		String line = name + ":" + value;

		// When
		String[] property = Markdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
		assertEquals(name, property[0]);
		assertEquals(value, property[1]);
	}

	@Test
	public void shouldReadPropertyWithSpaces() {

		// Given
		String name = "name";
		String value = "value";
		String line = "   " + name + " :  " + value + "\t";

		// When
		String[] property = Markdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
		assertEquals(name, property[0]);
		assertEquals(value, property[1]);
	}

	@Test
	public void shouldTwoElementArray() {

		// Given
		String line = "";

		// When
		String[] property = Markdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
	}
}
