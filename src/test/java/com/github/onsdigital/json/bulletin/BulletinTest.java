package com.github.onsdigital.json.bulletin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.davidcarboni.ResourceUtils;
import com.github.onsdigital.json.Section;

public class BulletinTest {

	Bulletin bulletin;

	@Before
	public void setup() {
		bulletin = new Bulletin();
	}

	@Test
	public void shouldReadBulletin() throws IOException {

		// Given
		String resourceName = "/com/github/onsdigital/json/bulletin/data.md";
		Reader markdown = ResourceUtils.getReader(resourceName);
		String lede = "man";
		String more = "nation";
		String summary = "summarizor";
		String headline1 = "Old English revived";
		String headline2 = "leed";
		String headline3 = "leode";
		String contactName = "Jukesie";
		String contactEmail = "jukesie@gmail.com";
		String nextRelease = "soon";

		// When
		bulletin = new Bulletin(markdown);

		// Then

		// Header block
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
		assertEquals("Titular", bulletin.title);

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
		boolean result = bulletin.matchTitle(markdown);

		// Then
		assertTrue(result);
		assertEquals(title, bulletin.title);
	}

	@Test
	public void shouldExtractTitleWithWeirdContent() {

		// Given
		String title = "Title titel titre # titolo";
		String markdown = "# \t" + title;
		bulletin.title = "";

		// When
		boolean result = bulletin.matchTitle(markdown);

		// Then
		assertTrue(result);
		assertEquals(title, bulletin.title);
	}

	@Test
	public void shouldNotExtractTitleIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		bulletin.title = "already here";

		// When
		boolean result = bulletin.matchTitle(markdown);

		// Then
		assertFalse(result);
		assertFalse(StringUtils.isEmpty(bulletin.title));
	}

	@Test
	public void shouldNotExtractTitleWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		bulletin.title = "";

		// When
		boolean result = bulletin.matchTitle(markdown);

		// Then
		assertFalse(result);
		assertTrue(StringUtils.isEmpty(bulletin.title));
	}

	@Test
	public void shouldExtractHeading() {

		// Given
		String heading = "Head thing";
		String markdown = "## \t" + heading;

		// When
		Section section = bulletin.matchHeading(markdown);

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
		Section section = bulletin.matchHeading(markdown);

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
		Section section = bulletin.matchHeading(markdown);

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
		Section section = bulletin.matchHeading(markdown);

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
		String[] property = bulletin.readProperty(line);

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
		String[] property = bulletin.readProperty(line);

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
		String[] property = bulletin.readProperty(line);

		// Then
		assertEquals(2, property.length);
	}

}
