package com.github.onsdigital.generator.markdown;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.onsdigital.json.markdown.Article;

public class ArticleMarkdownTest {

	Article article;

	@Before
	public void setup() {
		article = new Article();
	}

	@Test
	public void shouldReadArticle() throws IOException, URISyntaxException {

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
		article = new ArticleMarkdown().readArticle(path);

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
		assertEquals(3, article.sections.size());
		assertEquals("Article summary", article.sections.get(0).title);
		assertEquals("Summarise article.\n", article.sections.get(0).markdown);
		assertEquals("Section one", article.sections.get(1).title);
		String markdown1 = "Jarogonium est jargonius et dameleie statisticum seculum mondi.\n";
		assertEquals(markdown1, article.sections.get(1).markdown);
		assertEquals("Section two", article.sections.get(2).title);
		String markdown2 = "Lorem ipsum article\n";
		markdown2 += " * bullet1\n";
		markdown2 += " * bullet2\n";
		assertEquals(markdown2, article.sections.get(2).markdown);

		// Accordion
		assertEquals(1, article.accordion.size());
		assertEquals("Footnotes", article.accordion.get(0).title);
		assertEquals("Article footer", article.accordion.get(0).markdown);
	}

}
