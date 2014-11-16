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

import com.github.onsdigital.json.Section;
import com.github.onsdigital.json.markdown.Article;

public class ArticleMarkdownTest {

	Article article;

	@Before
	public void setup() {
		article = new Article();
	}

	@Test
	public void shouldReadBulletin() throws IOException, URISyntaxException {

		// Given
		String theme = "Economy";
		String level2 = "Government, Public Sector and Taxes";
		String level3 = "Public Sector Finance";
		String lede = "man";
		String more = "nation";
		String contactName = "Jukesie";
		String contactEmail = "jukesie@gmail.com";
		String nextRelease = "soon";
		ClassLoader classLoader = ArticleMarkdownTest.class.getClassLoader();
		String resourceName = "com/github/onsdigital/json/markdown/article.md";
		Path path = Paths.get(classLoader.getResource(resourceName).toURI());

		// When
		article = ArticleMarkdown.read(path);

		// Then

		// Header block
		assertEquals(theme, article.theme);
		assertEquals(level2, article.level2);
		assertEquals(level3, article.level3);
		assertEquals(lede, article.lede);
		assertEquals(more, article.more);
		assertEquals(contactName, article.contact.name);
		assertEquals(contactEmail, article.contact.email);
		assertEquals(nextRelease, article.nextRelease);

		// Title
		assertEquals("What happened to all the money?", article.title);

		// Sections
		assertEquals(2, article.sections.size());
		assertEquals("Section one", article.sections.get(0).title);
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, article.sections.get(0).markdown);
		assertEquals("Section two", article.sections.get(1).title);
		String markdown2 = "Lorem ipsum\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2";
		assertEquals(markdown2, article.sections.get(1).markdown);
	}

	@Test
	public void shouldExtractTitle() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = "# \t" + title;
		article.title = "";

		// When
		String result = ArticleMarkdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldExtractTitleWithWeirdContent() {

		// Given
		String title = "Title titel titre # titolo";
		String markdown = "# \t" + title;
		article.title = "";

		// When
		String result = ArticleMarkdown.matchTitle(markdown);

		// Then
		assertNotNull(result);
		assertEquals(title, result);
	}

	@Test
	public void shouldNotExtractTitleIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		article.title = "already here";

		// When
		String result = ArticleMarkdown.matchTitle(markdown);

		// Then
		assertNull(result);
		assertFalse(StringUtils.isEmpty(article.title));
	}

	@Test
	public void shouldNotExtractTitleWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		article.title = "";

		// When
		String result = ArticleMarkdown.matchTitle(markdown);

		// Then
		assertNull(result);
		assertTrue(StringUtils.isEmpty(article.title));
	}

	@Test
	public void shouldExtractHeading() {

		// Given
		String heading = "Head thing";
		String markdown = "## \t" + heading;

		// When
		Section section = ArticleMarkdown.matchHeading(markdown);

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
		Section section = ArticleMarkdown.matchHeading(markdown);

		// Then
		assertNotNull(section);
		assertEquals(title, section.title);
	}

	@Test
	public void shouldNotExtractHeadingIfAlreadySet() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " # " + title;
		article.title = "already here";

		// When
		Section section = ArticleMarkdown.matchHeading(markdown);

		// Then
		assertNull(section);
	}

	@Test
	public void shouldNotExtractSectionWitLeadingSpaces() {

		// Given
		String title = "Title titel titre titolo";
		String markdown = " ## " + title;
		article.title = "";

		// When
		Section section = ArticleMarkdown.matchHeading(markdown);

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
		String[] property = ArticleMarkdown.readProperty(line);

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
		String[] property = ArticleMarkdown.readProperty(line);

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
		String[] property = ArticleMarkdown.readProperty(line);

		// Then
		assertEquals(2, property.length);
	}
}
