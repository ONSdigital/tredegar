package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.onsdigital.json.markdown.Methodology;

public class MethodologyMarkdownTest {

	Methodology methodology;

	@Before
	public void setup() {
		methodology = new Methodology();
	}

	@Test
	public void shouldReadBulletin() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String lede = "man";
		String more = "nation";
		ClassLoader classLoader = MethodologyMarkdownTest.class.getClassLoader();
		String resourceName = "com/github/onsdigital/json/markdown/methodology.md";
		Path path = Paths.get(classLoader.getResource(resourceName).toURI());

		// When
		methodology = new MethodologyMarkdown().readMethodology(path);

		// Then

		// Header block
		assertEquals(theme, methodology.theme);
		assertEquals(level2, methodology.level2);
		assertEquals(level3, methodology.level3);
		assertEquals(lede, methodology.lede);
		assertEquals(more, methodology.more);

		// Title
		assertEquals("How do we work out the numbers?", methodology.title);

		// Sections
		assertEquals(3, methodology.sections.size());
		assertEquals("Methodology summary", methodology.sections.get(0).title);
		assertEquals("Summarise methodology.\n", methodology.sections.get(0).markdown);
		assertEquals("Section one", methodology.sections.get(1).title);
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, methodology.sections.get(1).markdown);
		assertEquals("Section two", methodology.sections.get(2).title);
		String markdown2 = "Lorem ipsum methodology\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, methodology.sections.get(2).markdown);

		// Accordion
		assertEquals(1, methodology.accordion.size());
		assertEquals("Footnotes", methodology.accordion.get(0).title);
		assertEquals("Methodology footer", methodology.accordion.get(0).markdown);
	}

}
