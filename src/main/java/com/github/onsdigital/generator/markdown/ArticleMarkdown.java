package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Article;

public class ArticleMarkdown {

	static final String resourceName = "/articles";

	public static void parse() throws IOException {
		Collection<Path> files = Markdown.getFiles(resourceName);

		for (Path file : files) {

			// Read the article:
			Article article = readArticle(file);

			// Add it to the taxonomy:
			Folder folder = Data.getFolder(article.theme, article.level2, article.level3);
			folder.articles.add(article);
		}
	}

	static Article readArticle(Path file) throws IOException {

		// Read the file
		System.out.println("Processing article from: " + file);
		Markdown markdown = new Markdown(file);

		// Set up the article
		Article article = new Article();
		article.name = markdown.title;
		article.title = markdown.title;
		setProperties(article, markdown);
		article.sections.addAll(markdown.sections);
		article.accordion.addAll(markdown.accordion);
		article.fileName = markdown.toFilename();

		return article;
	}

	/**
	 * Reads the "header" information about the article. Information is expected
	 * in the form "key : value" and the header block should be terminated with
	 * an empty line. The recognised keys are as follows.
	 * <ul>
	 * <li>Theme</li>
	 * <li>Level 2</li>
	 * <li>Level 3</li>
	 * <li>Lede</li>
	 * <li>More</li>
	 * <li>Contact name</li>
	 * <li>Contact email</li>
	 * <li>Next release</li>
	 * </ul>
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private static void setProperties(Article article, Markdown markdown) {

		Map<String, String> properties = markdown.properties;

		// Location
		article.theme = properties.remove("theme");
		article.level2 = properties.remove("level 2");
		article.level3 = properties.remove("level 3");

		// Additional details
		article.lede = properties.remove("lede");
		article.more = properties.remove("more");
		article.contact.name = properties.remove("contact name");
		article.contact.email = properties.remove("contact email");
		article.nextRelease = properties.remove("next release");

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Article key not recognised: " + property + " (for value '" + properties.get(property) + "')");
		}

	}
}
