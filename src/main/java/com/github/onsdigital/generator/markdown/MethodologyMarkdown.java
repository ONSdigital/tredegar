package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Scanner;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Methodology;

public class MethodologyMarkdown extends Markdown {

	static final String resourceName = "/methodology";

	public void parse() throws IOException {
		Collection<Path> files = getFiles(resourceName);

		for (Path file : files) {

			// Read the methodology:
			Methodology methodology = readMethodology(file);

			// Add it to the taxonomy:
			Folder folder = Data.getFolder(methodology.theme, methodology.level2, methodology.level3);
			folder.methodology.add(methodology);
		}
	}

	Methodology readMethodology(Path file) throws IOException {

		// Read the file
		System.out.println("Processing methodology from: " + file);
		read(file);

		// Set up the methodology
		Methodology methodology = new Methodology();
		methodology.name = title;
		methodology.title = title;
		setProperties(methodology);
		methodology.sections.addAll(sections);
		methodology.accordion.addAll(accordion);
		methodology.fileName = toFilename(methodology.name);

		return methodology;
	}

	/**
	 * Reads the "header" information about the methodology. Information is
	 * expected in the form "key : value" and the header block should be
	 * terminated with an empty line. The recognised keys are as follows.
	 * <ul>
	 * <li>Theme</li>
	 * <li>Level 2</li>
	 * <li>Level 3</li>
	 * <li>Lede</li>
	 * <li>More</li>
	 * </ul>
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private void setProperties(Methodology methodology) {

		// Location
		methodology.theme = properties.remove("Theme");
		methodology.level2 = properties.remove("Level 2");
		methodology.level3 = properties.remove("Level 3");

		// Additional details
		methodology.lede = properties.remove("Lede");
		methodology.more = properties.remove("More");

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Methodology key not recognised: " + property + " (for value '" + properties.get(property) + "')");
		}

	}

}
