package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.json.markdown.Methodology;

public class MethodologyMarkdown {

	static final String resourceName = "/methodology";

	public static void parse() throws IOException {
		Collection<Path> files = Markdown.getFiles(resourceName);

		for (Path file : files) {

			// Read the methodology:
			Methodology methodology = readMethodology(file);

			// Add it to the taxonomy:
			Folder folder = Data.getFolder(methodology.theme, methodology.level2, methodology.level3);
			folder.methodology.add(methodology);
		}
	}

	static Methodology readMethodology(Path file) throws IOException {

		// Read the file
		System.out.println("Processing methodology from: " + file);
		Markdown markdown = new Markdown(file);

		// Set up the methodology
		Methodology methodology = new Methodology();
		methodology.name = markdown.title;
		methodology.title = markdown.title;
		setProperties(methodology, markdown);
		methodology.sections.addAll(markdown.sections);
		methodology.accordion.addAll(markdown.accordion);
		methodology.fileName = markdown.toFilename();

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
	private static void setProperties(Methodology methodology, Markdown markdown) {

		Map<String, String> properties = markdown.properties;

		// Location
		methodology.theme = properties.remove("theme");
		methodology.level2 = properties.remove("level 2");
		methodology.level3 = properties.remove("level 3");

		// Additional details
		methodology.lede = properties.remove("lede");
		methodology.more = properties.remove("more");

		// Note any unexpected information
		for (String property : properties.keySet()) {
			System.out.println("Methodology key not recognised: " + property + " (for value '" + properties.get(property) + "')");
		}

	}

}
