package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.onsdigital.json.markdown.Bulletin;
import com.github.onsdigital.json.markdown.Section;

public class BulletinMarkdownTest {

	Bulletin bulletin;

	@Before
	public void setup() {
		bulletin = new Bulletin();
	}

	@Test
	public void shouldReadBulletin() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String lede = "man";
		String more = "nation";
		String summary = "summarizor";
		String headline1 = "Old English revived";
		String headline2 = "leed";
		String headline3 = "leode";
		String contactName = "Jukesie";
		String contactEmail = "jukesie@gmail.com";
		String nextRelease = "soon";
		ClassLoader classLoader = BulletinMarkdownTest.class.getClassLoader();
		String resourceName = "com/github/onsdigital/json/markdown/bulletin.md";
		Path path = Paths.get(classLoader.getResource(resourceName).toURI());

		// When
		bulletin = BulletinMarkdown.read(path);

		// Then

		// Header block
		assertEquals(theme, bulletin.theme);
		assertEquals(level2, bulletin.level2);
		assertEquals(level3, bulletin.level3);
		assertEquals(lede, bulletin.lede);
		assertEquals(more, bulletin.more);
		assertEquals(summary, bulletin.summary);
		assertEquals(headline1, bulletin.headline1);
		assertEquals(headline2, bulletin.headline2);
		assertEquals(headline3, bulletin.headline3);
		assertEquals(contactName, bulletin.contact.name);
		assertEquals(contactEmail, bulletin.contact.email);
		assertEquals(nextRelease, bulletin.nextRelease);

		// Title
		assertEquals("Analysis of consumer price inflation", bulletin.title);

		// Sections
		assertEquals(2, bulletin.sections.size());
		assertEquals("Section one", bulletin.sections.get(0).title);
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, bulletin.sections.get(0).markdown);
		assertEquals("Section two", bulletin.sections.get(1).title);
		String markdown2 = "Lorem ipsum\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2";
		assertEquals(markdown2, bulletin.sections.get(1).markdown);
	}

	@Test
	public void shouldExtractTitle() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = "# \t" + title;
		bulletin.title = "";

		// When
		String result = BulletinMarkdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldExtractTitleWithWeirdContent() {

		// Given
		String title = "Title titel titre # titolo";
		String markdown = "# \t" + title;
		bulletin.title = "";

		// When
		String result = BulletinMarkdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldNotExtractTitleIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		bulletin.title = "already here";

		// When
		String result = BulletinMarkdown.matchTitle(markdown);

		// Then
		assertNull(result);
		assertFalse(StringUtils.isEmpty(bulletin.title));
	}

	@Test
	public void shouldNotExtractTitleWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		bulletin.title = "";

		// When
		String result = BulletinMarkdown.matchTitle(markdown);

		// Then
		assertNull(result);
		assertTrue(StringUtils.isEmpty(bulletin.title));
	}

	@Test
	public void shouldExtractHeading() {

		// Given
		String heading = "Head thing";
		String markdown = "## \t" + heading;

		// When
		Section section = BulletinMarkdown.matchHeading(markdown);

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
		Section section = BulletinMarkdown.matchHeading(markdown);

		// Then
		assertNotNull(section);
		assertEquals(title, section.title);
	}

	@Test
	public void shouldNotExtractHeadingIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		bulletin.title = "already here";

		// When
		Section section = BulletinMarkdown.matchHeading(markdown);

		// Then
		assertNull(section);
	}

	@Test
	public void shouldNotExtractSectionWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " ## " + title;
		bulletin.title = "";

		// When
		Section section = BulletinMarkdown.matchHeading(markdown);

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
		String[] property = BulletinMarkdown.readProperty(line);

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
		String[] property = BulletinMarkdown.readProperty(line);

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
		String[] property = BulletinMarkdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
	}
}
