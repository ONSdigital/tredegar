package com.github.onsdigital.json.bulletin;

import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.CollectionItem;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.Email;
import com.github.onsdigital.json.Section;
import com.github.onsdigital.json.TaxonomyHome;

public class Bulletin extends CollectionItem {

	// Top section
	public String nextRelease = "21 November 2014";
	public Email contact = new Email();

	// Exec summary
	public String lede = "Producer Price Inflation (PPI) measures the price changes of goods bought and sold by UK manufacturers this month compared to the same month a year ago. PPI provides a key measure of inflation alongside other indicators such as the Consumer Prices Index (CPI) and Services Producer Price Index (SPPI)."
			+ "PPI is split into two components: output price inflation and input price inflation. The input price indices capture changes in the cost of the material and fuel inputs that producers face whereas the output price indices capture the changing prices of goods sold by producers."
			+ "This article looks at the trends in both producer price indices since 2000 and which components contribute most towards changes in each price index in recent years.";
	public String more = "Annual output and input price changes follow similar trends, but with the latter tending to have higher price growth1. In Figure 1, trends in producer price inflation can be split into three distinct periods: 2000 to 2005, 2005 to 2012 and 2012 to June 2014.  Both indices experience greater variability within the second period of 2005 to 2012 than compared with the first and third period. From 2005 to 2012 the average growth rate was 2.9% for output price inflation and 8.1% for input price inflation.  The largest peaks and troughs experienced for both indices throughout the time series shown below occurred in the second period of 2005 to 2012. Output price inflation rose to 8.9% in July 2008 while input price inflation rose to 34.8% in June 2008 and both fell to their lowest rates in July 2009: output price inflation falling by 1.6% and input price inflation falling by 14.8%.";

	// Table of contents
	public List<Section> sections = new ArrayList<Section>();
	public URI uri;
	public String headline1 = "Duis ut laoreet felis";
	public String headline2 = "Morbi sed sem at magna";
	public String headline3 = "Auctor gravida sed non enim";
	public String summary = "Summary section";

	/**
	 * Sets up some basic content.
	 */
	public Bulletin() {
		type = ContentType.bulletin;
		title = "Consumer Price Inflation, August 2014";
		releaseDate = "19 August 2014";
		Section summary = new Section();
		summary.title = "Summary";
		summary.markdown = "Consectetur adipiscing elit. Suspendisse non tellus nibh. Duis tristique risus sed urna fringilla, eu facilisis orci fringilla. Nullam iaculis libero tempor vehicula ultricies"
				+ "\n\n"
				+ " * The annual rate of output producer price inflation remained low in June, while input prices continued to fall.\n"
				+ " * The output price index for goods produced by UK manufacturers (factory gate prices) rose 0.2% in the year to June, compared with a rise of 0.5% in the year to May.\n"
				+ " * Factory gate prices fell 0.2% between May and June, compared with a fall of 0.1% between April and May\n";
		sections.add(summary);

		Section whatIs = new Section();
		whatIs.title = "What is Producer Price Inflation (PPI)?";

		whatIs.markdown = "Etiam fringilla tellus arcu, quis dapibus lacus lacinia a. Vivamus in sollicitudin eros, sed ornare metus. Nam sapien augue, varius bibendum sagittis sed, malesuada quis augue."
				+ "\n\n"
				+ "> This is an explanation box Morbi orci nulla, lobortis pretium auctor non, varius eget mi. Mauris viverra diam quam, at auctor velit placerat sit amet. In sed erat quis elit eleifend tempus. Pellentesque vestibulum orci nec nulla accumsan egestas. Proin mauris ipsum, ornare posuere risus non, consectetur lobortis mi. Pellentesque mi ante, sodales sollicitudin pretium et, varius vel enim."
				+ "\n\n"
				+ "Etiam ac ultricies orci. Pellentesque et posuere tortor. Nunc quam risus, pharetra non condimentum at, pretium at dolor. Maecenas placerat, arcu non consequat venenatis, eros leo eleifend lorem."
				+ "\n\n"
				+ "    sample chart"
				+ "\n\n"
				+ "Proin sed facilisis sapien. Nunc hendrerit dignissim sapien, vel consequat mi rhoncus eget. Maecenas et tellus convallis, tristique risus vitae.";
		sections.add(whatIs);
	}

	/**
	 * Sets up a bulletin according to the given content.
	 */
	public Bulletin(Reader markdown) {
		this();

		// Reinitialise the bulletin:
		title = StringUtils.EMPTY;
		sections.clear();

		// Read the markdown
		try (Scanner scanner = new Scanner(markdown)) {
			readHeader(scanner);
			readContent(scanner);
		}
	}

	/**
	 * Reads the "header" information about the bulletin. Information is
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
	private void readHeader(Scanner scanner) {

		// Property keys:
		String lede = "Lede";
		String more = "More";
		String summary = "Summary";
		String headline1 = "Headline 1";
		String headline2 = "Headline 2";
		String headline3 = "Headline 3";
		String contactName = "Contact name";
		String contactEmail = "Contact email";
		String nextRelease = "Next release";

		String line;
		while (scanner.hasNextLine() && StringUtils.isNotBlank(line = scanner.nextLine())) {

			// Extract property values:
			String[] property = readProperty(line);
			if (StringUtils.equalsIgnoreCase(property[0], lede)) {
				this.lede = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], more)) {
				this.more = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], summary)) {
				this.summary = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], headline1)) {
				this.headline1 = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], headline2)) {
				this.headline2 = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], headline3)) {
				this.headline3 = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], contactName)) {
				this.contact.name = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], contactEmail)) {
				this.contact.email = property[1];
			} else if (StringUtils.equalsIgnoreCase(property[0], nextRelease)) {
				this.nextRelease = property[1];
			} else {
				System.out.println("Key not recognised: " + property[0] + " (for value '" + property[1] + "')");
			}

		}
	}

	/**
	 * Parses the markdown content of the bulletin into title and sections;
	 * 
	 * @param scanner
	 *            The {@link Scanner} to read lines from.
	 */
	private void readContent(Scanner scanner) {

		Section currentSection = null;

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();

			// Extract content structure:
			boolean matched = matchTitle(line);
			if (!matched) {
				Section newSection = matchHeading(line);
				if (newSection != null) {
					sections.add(newSection);
					currentSection = newSection;
					matched = true;
				}
			}
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
	String[] readProperty(String line) {
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
	 * the bulletin title to the title text, unless the title has already been
	 * set.
	 * 
	 * @param line
	 *            The line to be matched.
	 * @return
	 * @see <a
	 *      href="http://daringfireball.net/projects/markdown/syntax">http://daringfireball.net/projects/markdown/syntax</a>
	 */
	boolean matchTitle(String line) {
		boolean result = false;

		// Set the title
		String h1Regex = "#\\s+";
		if (StringUtils.isEmpty(title) && line.matches(h1Regex + ".*")) {
			title = line.replaceFirst(h1Regex, "");
			result = true;
		}

		return result;
	}

	/**
	 * If the given line matches markdown H1 syntax (atx only, not Setext), sets
	 * the bulletin title to the title text, unless the title has already been
	 * set.
	 * 
	 * @param line
	 *            The line to be matched.
	 * @see <a
	 *      href="http://daringfireball.net/projects/markdown/syntax">http://daringfireball.net/projects/markdown/syntax</a>
	 */
	Section matchHeading(String line) {
		Section result = null;

		// Set the section title
		String h2Regex = "##\\s+";
		if (line.matches(h2Regex + ".*")) {
			result = new Section();
			result.title = line.replaceFirst(h2Regex, "");
			result.markdown = StringUtils.EMPTY;
		}

		return result;
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}

}
