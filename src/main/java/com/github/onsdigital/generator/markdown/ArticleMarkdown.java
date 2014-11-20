package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Scanner;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Article;

public class ArticleMarkdown extends Markdown {

	static final String resourceName = "/articles";

	public void parse() throws IOException {
		Collection<Path> files = getFiles(resourceName);

		for (Path file : files) {

			// Read the article:
			Article article = readArticle(file);

			// Add it to the taxonomy:
			Folder folder = Data.getFolder(article.theme, article.level2, article.level3);
			folder.articles.add(article);
		}
	}

	Article readArticle(Path file) throws IOException {

		// Read the file
		System.out.println("Processing article from: " + file);
		read(file);

		// Set up the article
		Article article = new Article();
		article.name = title;
		article.title = title;
		setProperties(article);
		article.sections.addAll(sections);
		article.accordion.addAll(accordion);
		article.fileName = toFilename(article.name);

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
	private void setProperties(Article article) {

		// Location
		article.theme = properties.remove("Theme");
		article.level2 = properties.remove("Level 2");
		article.level3 = properties.remove("Level 3");

		// Additional details
		article.lede = properties.remove("Lede");
		article.more = properties.remove("More");
		article.contact.name = properties.remove("Contact name");
		article.contact.email = properties.remove("Contact email");
		article.nextRelease = properties.remove("Next release");

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Article key not recognised: " + property + " (for value '" + properties.get(property) + "')");
		}

	}
}
