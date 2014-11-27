package com.github.onsdigital.generator.markdown;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.data.DataCSV;
import com.github.onsdigital.json.markdown.Section;

class Markdown {

	private String filename;
	String title;
	Map<String, String> properties = new HashMap<>();
	List<Section> sections = new ArrayList<>();
	List<Section> accordion = new ArrayList<>();

	public Markdown(Path file) throws IOException {

		filename = file.getFileName().toString();

		// Read the markdown - it may be in UTF8 or in Windows encoding (cp1252)
		// because these files will be edited on Linux, Mac and Windows
		// machines.
		try (Reader reader = Files.newBufferedReader(file, Charset.forName("cp1252"))) {
			try (Scanner scanner = new Scanner(reader)) {
				readHeader(scanner);
				readContent(scanner);
			}
		}
		// It looks like if cp1252 fails we don't get any content, so retry with
		// Windows encoding:
		if (properties.size() == 0) {
			try (Reader reader = Files.newBufferedReader(file, Charset.forName("UTF8"))) {
				try (Scanner scanner = new Scanner(reader)) {
					readHeader(scanner);
					readContent(scanner);
				}
			}
		}

	}

	/**
	 * Reads the "header" information about the article. Information is expected
	 * in the form "key : value" and the header block should be terminated with
	 * an empty line. The recognised keys are as follows.
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
	private void readHeader(Scanner scanner) {

		String line;
		while (scanner.hasNextLine() && StringUtils.isNotBlank(line = scanner.nextLine())) {
			// Extract property values:
			String[] property = readProperty(line);
			properties.put(property[0], property[1]);
		}
	}

	/**
	 * Parses the markdown content of the article into title and sections;
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private void readContent(Scanner scanner) {

		Section currentSection = null;

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();

			// Title
			Boolean matched = false;
			if (StringUtils.isBlank(title)) {
				String value = matchTitle(line);
				if (StringUtils.isNotBlank(value)) {
					this.title = value;
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
						accordion.add(newSection);
					} else {
						sections.add(newSection);
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

		// Set a default title if none was found in the markdown:
		title = StringUtils.defaultIfBlank(title, FilenameUtils.getBaseName(filename));
	}

	/**
	 * Sanitises an article name to <code>[a-zA-Z0-9]</code>.
	 * 
	 * @param name
	 *            The string to be sanitised.
	 * @return A sanitised string.
	 */
	String toFilename() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < title.length(); i++) {
			String character = title.substring(i, i + 1);
			if (character.matches("[a-zA-Z0-9]")) {
				result.append(character);
			}
		}
		return result.toString().toLowerCase();
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
	 * the article title to the title text, unless the title has already been
	 * set.
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
	 * the article title to the title text, unless the title has already been
	 * set.
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

	static Collection<Path> getFiles(String resourceName) throws IOException {
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
}
