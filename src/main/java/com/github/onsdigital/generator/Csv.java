package com.github.onsdigital.generator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.json.Data;
import com.google.common.io.Files;

public class Csv {

	/**
	 * Parses the taxonomy CSV file and generates a file structure..
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Reader reader = ResourceUtils.getReader("/Taxonomy.csv");

		String theme = null;
		String subject = null;
		String topic = null;
		Folder themeFolder = null;
		Folder subjectFolder = null;
		Folder topicFolder = null;

		Set<Folder> folders = new HashSet<>();

		try (CSVReader csvReader = new CSVReader(reader)) {

			// Column positions:
			String[] headers = csvReader.readNext();
			System.out.println(ArrayUtils.toString(headers));
			int themeIndex = ArrayUtils.indexOf(headers, "Theme");
			int subjectIndex = ArrayUtils.indexOf(headers, "Subject");
			int topicIndex = ArrayUtils.indexOf(headers, "Topic");
			System.out.println("Theme=" + themeIndex + " Subject="
					+ subjectIndex + " Topic=" + topicIndex);

			// Theme Subject Topic
			String[] row;
			while ((row = csvReader.readNext()) != null) {

				if (StringUtils.isNotBlank(row[themeIndex])) {
					theme = row[themeIndex];
					themeFolder = new Folder();
					themeFolder.name = theme;
					folders.add(themeFolder);
					subject = null;
					topic = null;
				}

				if (StringUtils.isNotBlank(row[subjectIndex])) {
					subject = row[subjectIndex];
					subjectFolder = new Folder();
					subjectFolder.name = subject;
					subjectFolder.parent = themeFolder;
					themeFolder.children.add(subjectFolder);
					topic = null;
				}

				if (StringUtils.isNotBlank(row[topicIndex])) {
					topic = row[topicIndex];
					topicFolder = new Folder();
					topicFolder.name = topic;
					topicFolder.parent = subjectFolder;
					subjectFolder.children.add(topicFolder);
				}

				String path = StringUtils.join(new String[] { theme, subject,
						topic }, '/');
				while (StringUtils.endsWith(path, "/"))
					path = path.substring(0, path.length() - 1);
				System.out.println(path);

			}

			// Walk folder tree:
			File root = new File("src/main/taxonomy");
			createHomePage(root);
			File themeFile;
			File subjectFile;
			File topicFile;
			System.out.println();
			for (Folder t : folders) {
				themeFile = new File(root, t.filename());
				themeFile.mkdirs();
				System.out.println(themeFile.getAbsolutePath());
				createT2(t, themeFile);
				for (Folder s : t.children) {
					subjectFile = new File(themeFile, s.filename());
					subjectFile.mkdirs();
					if (s.children.size() == 0) {
						createT3(s, subjectFile);
						// createContentFolders(s.name, subjectFile);
					} else
						createT2(s, subjectFile);
					System.out.println("\t" + subjectFile.getPath());
					for (Folder o : s.children) {
						topicFile = new File(subjectFile, o.filename());
						topicFile.mkdirs();
						System.out.println("\t\t" + topicFile.getPath());
						createT3(o, topicFile);
						if (o.children.size() == 0) {
							// createContentFolders(o.name, topicFile);
						}
					}
				}
			}
		}

	}

	// private static void createContentFolders(String name, File file)
	// throws IOException {
	//
	// Folder folder = new Folder();
	// File articles = new File(file, "articles");
	// folder.name = name + ": articles";
	// createIndex(folder, articles);
	// File bulletins = new File(file, "bulletins");
	// folder.name = name + ": bulletins";
	// createIndex(folder, bulletins);
	// File datasets = new File(file, "datasets");
	// folder.name = name + ": datasets";
	// createIndex(folder, datasets);
	// File methodology = new File(file, "methodology");
	// folder.name = name + ": methodology";
	// createIndex(folder, methodology);
	//
	// // This causes "too many open files" because Restolino
	// // attempts to monitor every directory:
	// // createHistory(name, file);
	// }

	/**
	 * Simulate some historical releases.
	 * 
	 * @param name
	 * @param file
	 * @throws IOException
	 */
	private static void createHistory(String name, File file)
			throws IOException {

		File tempDir = Files.createTempDir();
		FileUtils.copyDirectory(file, tempDir);

		for (int i = 1; i <= 10; i++) {
			Calendar release = Calendar.getInstance();
			release.add(Calendar.MONTH, -i);
			int year = release.get(Calendar.YEAR);
			int month = release.get(Calendar.MONTH) + 1;
			// Fixed at 21 to avoid the taxonomy being different too often.
			// 21st of September is "International Peace Day".
			int day = 21;
			String releaseFolderName = year + "-" + month + "-" + day;
			File releaseFolder = new File(file, releaseFolderName);
			FileUtils.copyDirectory(tempDir, releaseFolder);
		}

		FileUtils.deleteDirectory(tempDir);
	}

	private static void createHomePage(File file) throws IOException {

		// if (folder.children.size() == 0) {
		String markup = ResourceUtils.getString("/files/t1.html");
//		markup = markup.replaceAll("\\$title", "Home");
//		FileUtils.writeStringToFile(new File(file, "index.html"), markup);
		// File index = ResourceUtils.getFile("/index.html");
		// index.renameTo(new File(file, "index.html"));
		// }
	}

	private static void createT2(Folder folder, File file) throws IOException {

		// if (folder.children.size() == 0) {
		String markup = ResourceUtils.getString("/files/t2.html");
		String json = Serialiser.serialise(new Data(folder));

		// Iterator<Folder> iterator = folder.children.iterator();
		//
		// if (iterator.hasNext()) {
		// // First section:
		// Folder item1 = iterator.next();
		// markup = markup.replace("Gross Domestic Product (GDP)", item1.name);
		// markup = markup.replace("$link1", item1.filename());
		// }
		//
		// if (iterator.hasNext()) {
		// // Second section:
		// Folder item2 = iterator.next();
		// markup = markup.replace("Inflation and Price Indices", item2.name);
		// markup = markup.replace("$link2", item2.filename());
		// }
		//
		// if (iterator.hasNext()) {
		// // Third section:
		// Folder item3 = iterator.next();
		// markup = markup.replace("National Accounts", item3.name);
		// markup = markup.replace("$link3", item3.filename());
		// }
		//
		// String additionalLinks;
		// if (iterator.hasNext()) {
		//
		// // Stringses:
		// additionalLinks = "";
		// String linkTemplate = "				<footer class=\"nav-panel__footer\">\n"
		// + "		          <div class=\"nav-panel__action\">\n"
		// +
		// "		            <a class=\"nav-panel__roomy\" href=\"$link\">$title</a>\n"
		// + "		          </div>\n" + "		        </footer>\n";
		//
		// // Generate the rest of the links:
		// while (iterator.hasNext()) {
		//
		// Folder item = iterator.next();
		// additionalLinks += linkTemplate.replace("$title", item.name)
		// .replace("$link", item.filename());
		//
		// }
		// } else {
		// // additionalLinks = "		  <section class=\"nav-panel__roomy\">\n"
		// // + "            <ul class=\"list--neutral\">\n"
		// // + "              <li class=\"nav-panel__item\">\n"
		// // + "                <dl><dt>\n"
		// // + "                <h3 class=\"nav-panel__heading\">\n"
		// // +
		// //
		// "                  Lies, damned lies and missing statistics! There's nothing here to see.\n"
		// // + "                </h3>\n" + "                <dt><dl>\n"
		// // + "              </li>\n" + "            </ul>\n"
		// // + "          </section>\n";
		//
		// additionalLinks = "		        <footer class=\"nav-panel__footer\">\n"
		// + "		          <div class=\"nav-panel__action\">\n"
		// +
		// "		            <a class=\"nav-panel__roomy\">Lies, damned lies and missing statistics! There's nothing here to see.</a>\n"
		// + "		          </div>\n" + "		        </footer>";
		// }
		// markup = markup.replace("<!-- Additional links -->",
		// additionalLinks);

		// Breadcrumb
		String breadcrumb = " &gt; " + folder.name;
		Folder parent = folder.parent;
		String parentLink = "../";
		while (parent != null) {
			breadcrumb = " &gt; " + "<a href=\"" + parentLink
					+ "\" class=\"action-link hide-med-down\">" + parent.name
					+ "</a>" + breadcrumb;
			parent = parent.parent;
			parentLink += "../";
		}
		markup = markup.replace("$breadcrumb", breadcrumb);

		// Do the title last, otherwise it partially matches the keys above:
//		markup = markup.replaceAll("\\$title", folder.name);

//		FileUtils.writeStringToFile(new File(file, "index.html"), markup);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
		// File index = ResourceUtils.getFile("/index.html");
		// index.renameTo(new File(file, "index.html"));
		// }
	}

	private static void createT3(Folder folder, File file) throws IOException {

		// if (folder.children.size() == 0) {
		String markup = ResourceUtils.getString("/files/t3.html");
		String json = Serialiser.serialise(new Data(folder));

//		markup = markup.replaceAll("\\$title", folder.name);

		// Breadcrumb
		String breadcrumb = " &gt; " + folder.name;
		Folder parent = folder.parent;
		String parentLink = "../";
		while (parent != null) {
			breadcrumb = " &gt; " + "<a href=\"" + parentLink
					+ "\" class=\"action-link hide-med-down\">" + parent.name
					+ "</a>" + breadcrumb;
			parent = parent.parent;
			parentLink += "../";
		}
		markup = markup.replace("$breadcrumb", breadcrumb);

//		FileUtils.writeStringToFile(new File(file, "index.html"), markup);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
		// File index = ResourceUtils.getFile("/index.html");
		// index.renameTo(new File(file, "index.html"));
		// }
	}
}
