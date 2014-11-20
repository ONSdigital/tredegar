package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Scanner;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Bulletin;

public class BulletinMarkdown extends Markdown {

	static final String resourceName = "/bulletins";

	public void parse() throws IOException {
		Collection<Path> files = getFiles(resourceName);

		for (Path file : files) {

			// Read the bulletin:
			Bulletin bulletin = readBulletin(file);

			// Add it to the taxonomy:
			Folder folder = Data.getFolder(bulletin.theme, bulletin.level2, bulletin.level3);
			folder.bulletins.add(bulletin);
		}
	}

	Bulletin readBulletin(Path file) throws IOException {

		// Read the file
		System.out.println("Processing bulletin from: " + file);
		read(file);

		// Set up the bulletin
		Bulletin bulletin = new Bulletin();
		bulletin.name = title;
		bulletin.title = title;
		setProperties(bulletin);
		bulletin.sections.addAll(sections);
		bulletin.accordion.addAll(accordion);
		bulletin.fileName = toFilename(bulletin.name);

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
	private void setProperties(Bulletin bulletin) {

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
