package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.generator.data.DataCSV;
import com.github.onsdigital.json.Section;
import com.github.onsdigital.json.markdown.Methodology;

public class MethodologyMarkdown {

	static final String resourceName = "/methodology";

	public static void parse() throws IOException {
		Collection<Path> files = getFiles();

		for (Path file : files) {
			Methodology methodology = read(file);
			System.out.println("Processing: " + methodology.name + " / " + methodology.title);
			Folder folder = Data.getFolder(methodology.theme, methodology.level2, methodology.level3);
			folder.methodology.add(methodology);
		}

	}

	static Methodology read(Path file) throws IOException {

		try (Reader reader = new InputStreamReader(Files.newInputStream(file))) {
			Methodology methodology = new Methodology();

			// Read the markdown
			try (Scanner scanner = new Scanner(reader)) {
				readHeader(scanner, methodology);
				readContent(scanner, methodology);
			}

			methodology.fileName = toFilename(methodology.name);

			return methodology;
		}

	}

	/**
	 * Reads the "header" information about the methodology item. Information is
	 * expected in the form "key : value" and the header block should be
	 * terminated with an empty line. The recognised keys are as follows.
	 * <ul>
	 * <li>Next release</li>
	 * <li>Contact name</li>
	 * <li>Contact email</li>
	 * <li>Lede</li>
	 * <li>More</li>
	 * <li>Headline 1</li>
	 * <li>Headline 2</li>
	 * <li>Headline 3</li>
	 * </ul>
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private static void readHeader(Scanner scanner, Methodology methodology) {

		// Property keys:
		String theme = "Theme";
		String level2 = "Level 2";
		String level3 = "Level 3";
		String title = "Title";
		String lede = "Lede";
		String more = "More";

		String line;
		while (scanner.hasNextLine() && StringUtils.isNotBlank(line = scanner.nextLine())) {

			// Extract property values:
			String[] property = readProperty(line);
			if (StringUtils.equalsIgnoreCase(property[0], theme)) {
				methodology.theme = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], level2)) {
				methodology.level2 = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], level3)) {
				methodology.level3 = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], lede)) {
				methodology.lede = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], title)) {
				methodology.name = property[1];
				methodology.title = property[1];
				methodology.fileName = toFilename(property[1]);
			} else if (StringUtils.equalsIgnoreCase(property[0], more)) {
				methodology.more = property[1];
			} else {
				System.out.println("Key not recognised: " + property[0] + " (for value '" + property[1] + "')");
			}

		}
	}

	/**
	 * Parses the markdown content of the methodology item into title and
	 * sections;
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private static void readContent(Scanner scanner, Methodology methodology) {

		Section currentSection = null;

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();

			// Title
			Boolean matched = false;
			if (StringUtils.isBlank(methodology.title)) {
				String title = matchTitle(line);
				if (StringUtils.isNotBlank(title)) {
					methodology.name = title;
					methodology.title = title;
					matched = true;
				}
			}

			// Section heading
			if (!matched) {
				Section newSection = matchHeading(line);
				if (newSection != null) {
					if (newSection.title.matches("\\[accordion\\].*")) {
						// Remove the marker, case insensitively with "(?i)"
						// and add the section to the accordion list:
						newSection.title = newSection.title.replaceFirst("(?i)\\[accordion\\]\\s*", "");
						methodology.accordion.add(newSection);
					} else {
						methodology.sections.add(newSection);
					}
					currentSection = newSection;
					matched = true;
				}
			}

			// Section content
			if (!matched && currentSection != null) {
				if (StringUtils.isNotBlank(currentSection.markdown)) {
					currentSection.markdown += "\n";
				}
				currentSection.markdown += line;
			}

		}

	}

	/**
	 * Extracts a property key and value from the given line.
	 * 
	 * @param line
	 *            The String to be parsed.
	 * @return A two-element String array. If the line can't be parsed the
	 *         elements of the array will be null.
	 */
	static String[] readProperty(String line) {
		String[] result = new String[2];

		int separatorIndex = line.indexOf(':');
		if (separatorIndex > 0) {
			result[0] = StringUtils.trim(line.substring(0, separatorIndex));
			if (line.length() > separatorIndex + 1) {
				result[1] = line.substring(separatorIndex + 1);
			}
		}

		result[0] = StringUtils.trim(result[0]);
		result[1] = StringUtils.trim(result[1]);
		return result;
	}

	/**
	 * If the given line matches markdown H1 syntax (atx only, not Setext), sets
	 * the methodology title to the title text, unless the title has already
	 * been set.
	 * 
	 * @param line
	 *            The line to be matched.
	 * @return
	 * @see <a
	 *      href="http://daringfireball.net/projects/markdown/syntax">http://daringfireball.net/projects/markdown/syntax</a>
	 */
	static String matchTitle(String line) {
		String result = null;

		// Set the title
		String h1Regex = "#\\s+";
		if (line.matches(h1Regex + ".*")) {
			result = line.replaceFirst(h1Regex, "");
		}

		return result;
	}

	/**
	 * If the given line matches markdown H1 syntax (atx only, not Setext), sets
	 * the methodology title to the title text, unless the title has already
	 * been set.
	 * 
	 * @param line
	 *            The line to be matched.
	 * @see <a
	 *      href="http://daringfireball.net/projects/markdown/syntax">http://daringfireball.net/projects/markdown/syntax</a>
	 */
	static Section matchHeading(String line) {
		Section result = null;

		// Set the section title
		String h2Regex = "##\\s+";
		if (line.matches(h2Regex + ".*")) {
			result = new Section();
			result.title = line.replaceFirst(h2Regex, "").trim();
			result.markdown = StringUtils.EMPTY;
		}

		return result;
	}

	private static Collection<Path> getFiles() throws IOException {
		Set<Path> result = new TreeSet<>();

		try {
			URL resource = DataCSV.class.getResource(resourceName);
			Path folder = Paths.get(resource.toURI());

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.md")) {

				// Iterate the paths in this directory:
				for (Path item : stream) {
					result.add(item);
				}

			}

		} catch (URISyntaxException e) {
			throw new IOException(e);
		}

		return result;
	}

	/**
	 * Sanitises a methodology item name to <code>[a-zA-Z0-9]</code>.
	 * 
	 * @param name
	 *            The string to be sanitised.
	 * @return A sanitised string.
	 */
	public static String toFilename(String name) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			String character = name.substring(i, i + 1);
			if (character.matches("[a-zA-Z0-9]")) {
				result.append(character);
			}
		}
		return result.toString().toLowerCase();
	}
}
