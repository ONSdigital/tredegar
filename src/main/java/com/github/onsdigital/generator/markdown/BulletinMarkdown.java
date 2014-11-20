package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Bulletin;

public class BulletinMarkdown {

	static final String resourceName = "/bulletins";

	public static void parse() throws IOException {
		Collection<Path> files = Markdown.getFiles(resourceName);

		for (Path file : files) {

			// Read the bulletin:
			Bulletin bulletin = readBulletin(file);

			// Add it to the taxonomy:
			Folder folder = Data.getFolder(bulletin.theme, bulletin.level2, bulletin.level3);
			folder.bulletins.add(bulletin);
		}
	}

	static Bulletin readBulletin(Path file) throws IOException {

		// Read the file
		System.out.println("Processing bulletin from: " + file);
		Markdown markdown = new Markdown(file);

		// Set up the bulletin
		Bulletin bulletin = new Bulletin();
		bulletin.name = markdown.title;
		bulletin.title = markdown.title;
		setProperties(bulletin, markdown);
		bulletin.sections.clear();
		bulletin.sections.addAll(markdown.sections);
		bulletin.accordion.addAll(markdown.accordion);
		bulletin.fileName = markdown.toFilename();

		return bulletin;
	}

	/**
	 * Reads the "header" information about the bulletin. Information is
	 * expected in the form "key : value" and the header block should be
	 * terminated with an empty line. The recognised keys are as follows.
	 * <ul>
	 * <li>Theme</li>
	 * <li>Level 2</li>
	 * <li>Level 3</li>
	 * <li>Lede</li>
	 * <li>More</li>
	 * <li>Summary</li>
	 * <li>Headline 1</li>
	 * <li>Headline 2</li>
	 * <li>Headline 3</li>
	 * <li>Contact name</li>
	 * <li>Contact email</li>
	 * <li>Next release</li>
	 * </ul>
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private static void setProperties(Bulletin bulletin, Markdown markdown) {

		Map<String, String> properties = markdown.properties;

		// Location
		bulletin.theme = properties.remove("Theme");
		bulletin.level2 = properties.remove("Level 2");
		bulletin.level3 = properties.remove("Level 3");

		// Additional details
		bulletin.lede = properties.remove("Lede");
		bulletin.more = properties.remove("More");
		bulletin.summary = properties.remove("Summary");
		bulletin.headline1 = properties.remove("Headline 1");
		bulletin.headline2 = properties.remove("Headline 2");
		bulletin.headline3 = properties.remove("Headline 3");
		bulletin.contact.name = properties.remove("Contact name");
		bulletin.contact.email = properties.remove("Contact email");
		bulletin.nextRelease = properties.remove("Next release");

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Bulletin key not recognised: " + property + " (for value '" + properties.get(property) + "')");
		}

	}
}
