package com.github.onsdigital.generator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.json.Bulletin;
import com.github.onsdigital.json.Collection;
import com.github.onsdigital.json.Data;
import com.github.onsdigital.json.DataT1;
import com.github.onsdigital.json.DataT2;
import com.github.onsdigital.json.DataT3;
import com.github.onsdigital.json.TaxonomyNode;

public class Csv {

	static StringBuilder sitemap = new StringBuilder(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

	/**
	 * Parses the taxonomy CSV file and generates a file structure..
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Serialiser.getBuilder().setPrettyPrinting();
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
			Folder rootFolder = new Folder();
			rootFolder.name = "Home";
			rootFolder.children.addAll(folders);
			createHomePage(rootFolder, root);
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

		// Output the sitemap:
		sitemap.append("</urlset>\n");
		FileUtils.writeStringToFile(new File(
				"src/main/resources/files/sitemap.xml"), sitemap.toString());
	}

	private static void addUrl(Data t2) {
		String url = "";
		for (TaxonomyNode node : t2.breadcrumb) {
			url += "/" + node.fileName;
		}
		url += "/" + t2.fileName;
		addUrl(url);
	}

	static void addUrl(String path) {
		sitemap.append("<url><loc>http://onsdigital.herokuapp.com" + path
				+ "</loc></url>\n");
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

		File tempDir = com.google.common.io.Files.createTempDir();
		List<File> historyFolders = historyFolders(file);

		// Delete existing history folders:
		for (File historyFolder : historyFolders) {
			FileUtils.deleteQuietly(historyFolder);
		}

		System.out.println("Copying from " + file.getAbsolutePath() + " to "
				+ tempDir.getAbsolutePath());
		FileUtils.copyDirectory(file, tempDir);

		for (File historyFolder : historyFolders) {
			System.out.println("Copying from " + tempDir.getAbsolutePath()
					+ " to " + historyFolder.getAbsolutePath());
			FileUtils.copyDirectory(tempDir, historyFolder);
		}

		FileUtils.deleteDirectory(tempDir);
	}

	private static List<File> historyFolders(File file) {
		List<File> result = new ArrayList<>();

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
			result.add(releaseFolder);
		}

		return result;
	}

	private static void createHomePage(Folder folder, File file)
			throws IOException {

		// The folder needs to be at the root path:
		Data data = new DataT1(folder);
		data.fileName = "/";
		String json = Serialiser.serialise(data);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
		addUrl("/");
	}

	private static void createT2(Folder folder, File file) throws IOException {

		DataT2 t2 = new DataT2(folder);
		String json = Serialiser.serialise(t2);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
		addUrl(t2);
	}

	private static void createT3(Folder folder, File file) throws IOException {

		DataT3 t3 = new DataT3(folder);
		String json = Serialiser.serialise(t3);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);

		createBulletin(folder, file);
		createCollection(folder, file);
		if (file.getName().contains("inflationandpriceindices")) {
			createTimeseries(folder, file);
		}
		addUrl(t3);
	}

	/**
	 * Creates timeseries data.
	 *
	 * @param folder
	 * @param file
	 * @throws IOException
	 */
	private static void createTimeseries(Folder folder, File file)
			throws IOException {

		// Create the timeseries directory:
		File timeseriesFolder = new File(file, "timeseries");
		timeseriesFolder.mkdir();

		// Load up the CPI timeseries metadata:
		TimeseriesMetadata.loadTimeseriesMetadata();

		// Generate dummy timeseries for each CDID in the subset:
		for (String cdid : TimeseriesMetadata.timeseries.keySet()) {
			String json = Serialiser.serialise(TimeseriesMetadata.timeseries
					.get(cdid));
			FileUtils.writeStringToFile(new File(timeseriesFolder, cdid
					+ ".json"), json);
		}
	}

	private static void createBulletin(Folder folder, File file)
			throws IOException {
		// Create a dummy bulletin:
		File bulletins = new File(file, "bulletins");
		bulletins.mkdir();
		Bulletin bulletin = new Bulletin();
		bulletin.title = folder.name;
		String json = Serialiser.serialise(bulletin);
		FileUtils.writeStringToFile(new File(bulletins, "bulletin.json"), json);

		String name = folder.filename();
		if (name.contains("inflationandpriceindices")) {
			createHistory(folder.filename(), file);
		}
	}

	private static void createCollection(Folder folder, File file)
			throws IOException {
		String json = Serialiser.serialise(new Collection());
		FileUtils.writeStringToFile(new File(file, "collection.json"), json);
	}

}
