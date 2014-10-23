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
import com.github.onsdigital.json.Article;
import com.github.onsdigital.json.Collection;
import com.github.onsdigital.json.Data;
import com.github.onsdigital.json.Release;
import com.github.onsdigital.json.bulletin.Bulletin;
import com.github.onsdigital.json.taxonomy.DataT1;
import com.github.onsdigital.json.taxonomy.DataT2;
import com.github.onsdigital.json.taxonomy.DataT3;

public class Csv {

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
		String subTopic = null;
		Folder themeFolder = null;
		Folder subjectFolder = null;
		Folder topicFolder = null;
		Folder subTopicFolder = null;
		int themeCounter = 0;
		int subjectCounter = 0;
		int topicCounter = 0;
		int subTopicCounter = 0;

		Set<Folder> folders = new HashSet<>();

		try (CSVReader csvReader = new CSVReader(reader)) {

			// Column positions:s
			String[] headers = csvReader.readNext();
			System.out.println(ArrayUtils.toString(headers));
			int themeIndex = ArrayUtils.indexOf(headers, "Theme");
			int subjectIndex = ArrayUtils.indexOf(headers, "Subject");
			int topicIndex = ArrayUtils.indexOf(headers, "Topic");
			int subTopicIndex = ArrayUtils.indexOf(headers, "Subtopic");
			System.out.println("Theme=" + themeIndex + " Subject=" + subjectIndex + " Topic=" + topicIndex + " Subtopic=" + subTopicIndex);

			// Theme Subject Topic
			String[] row;
			while ((row = csvReader.readNext()) != null) {

				if (StringUtils.isNotBlank(row[themeIndex])) {
					theme = row[themeIndex];
					themeFolder = new Folder();
					themeFolder.name = theme;
					themeFolder.index = themeCounter++;
					subjectCounter = 0;
					topicCounter = 0;
					subTopicCounter = 0;
					folders.add(themeFolder);
					subject = null;
					topic = null;
					subTopic = null;
				}

				if (StringUtils.isNotBlank(row[subjectIndex])) {
					subject = row[subjectIndex];
					subjectFolder = new Folder();
					subjectFolder.name = subject;
					subjectFolder.parent = themeFolder;
					subjectFolder.index = subjectCounter++;
					topicCounter = 0;
					subTopicCounter = 0;
					themeFolder.children.add(subjectFolder);
					topic = null;
					subTopic = null;
				}

				if (StringUtils.isNotBlank(row[topicIndex])) {
					topic = row[topicIndex];
					topicFolder = new Folder();
					topicFolder.name = topic;
					topicFolder.parent = subjectFolder;
					topicFolder.index = topicCounter++;
					subjectFolder.children.add(topicFolder);
					subTopic = null;
				}

				if (StringUtils.isNotBlank(row[subTopicIndex])) {
					subTopic = row[subTopicIndex];
					subTopicFolder = new Folder();
					subTopicFolder.name = subTopic;
					subTopicFolder.parent = topicFolder;
					subTopicFolder.index = subTopicCounter++;
					topicFolder.children.add(subTopicFolder);
				}

				String path = StringUtils.join(new String[] { theme, subject, topic }, '/');
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
						} else if (isReleaseFolder(o)) {
							for (Folder u : o.children) {
								createRelease(topicFile, u);
							}
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
	private static void createHistory(String name, File file) throws IOException {

		File tempDir = com.google.common.io.Files.createTempDir();
		List<File> historyFolders = historyFolders(file);

		// Delete existing history folders:
		for (File historyFolder : historyFolders) {
			FileUtils.deleteQuietly(historyFolder);
		}

		System.out.println("Copying from " + file.getAbsolutePath() + " to " + tempDir.getAbsolutePath());
		FileUtils.copyDirectory(file, tempDir);

		for (File historyFolder : historyFolders) {
			System.out.println("Copying from " + tempDir.getAbsolutePath() + " to " + historyFolder.getAbsolutePath());
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

	private static void createHomePage(Folder folder, File file) throws IOException {
		// The folder needs to be at the root path:
		Data data = new DataT1(folder);
		data.fileName = "/";
		String json = Serialiser.serialise(data);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
	}

	private static void createT2(Folder folder, File file) throws IOException {
		if (folder.name.equals("Releases") || (folder.parent != null && folder.parent.name.equals("Releases"))) {
			System.out.println("Do not create json for Releases createT2");
		} else {
			DataT2 t2 = new DataT2(folder);
			t2.index = folder.index;
			String json = Serialiser.serialise(t2);
			FileUtils.writeStringToFile(new File(file, "data.json"), json);
		}
	}

	private static void createT3(Folder folder, File file) throws IOException {

		if (isReleaseFolder(folder)) {
			System.out.println("Do not create json for Releases createT3");
		} else {
			DataT3 t3 = new DataT3(folder);
			t3.index = folder.index;
			String json = Serialiser.serialise(t3);
			FileUtils.writeStringToFile(new File(file, "data.json"), json);

			createArticle(folder, file);
			createBulletin(folder, file);
			// createCollection(folder, file);
			if (file.getName().contains("inflationandpriceindices")) {
				createTimeseries(folder, file);
			}
		}
	}

	/**
	 * Creates timeseries data.
	 *
	 * @param folder
	 * @param file
	 * @throws IOException
	 */
	private static void createTimeseries(Folder folder, File file) throws IOException {

		// Create the timeseries directory:
		File timeseriesFolder = new File(file, "timeseries");
		timeseriesFolder.mkdir();

		// Load up the CPI timeseries metadata:
		TimeseriesMetadata.loadTimeseriesMetadata();

		// Generate dummy timeseries for each CDID in the subset:
		for (String cdid : TimeseriesMetadata.timeseries.keySet()) {
			String json = Serialiser.serialise(TimeseriesMetadata.timeseries.get(cdid));
			File cdidFolder = new File(timeseriesFolder, cdid);
			FileUtils.writeStringToFile(new File(cdidFolder, "data.json"), json);
		}
	}

	private static void createArticle(Folder folder, File file) throws IOException {
		// Create a dummy bulletin:
		File articles = new File(file, "articles");
		articles.mkdir();
		Article article = new Article();
		article.title = folder.name;
		String json = Serialiser.serialise(article);
		FileUtils.writeStringToFile(new File(articles, "data.json"), json);
	}

	private static void createBulletin(Folder folder, File file) throws IOException {
		// Create a dummy bulletin:
		File bulletins = new File(file, "bulletins");
		bulletins.mkdir();
		Bulletin bulletin = new Bulletin();
		bulletin.title = folder.name;
		String json = Serialiser.serialise(bulletin);
		FileUtils.writeStringToFile(new File(bulletins, "data.json"), json);
	}

	private static void createCollection(Folder folder, File file) throws IOException {
		String json = Serialiser.serialise(new Collection());
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
	}

	private static boolean isReleaseFolder(Folder o) {
		return o.parent.parent != null && o.parent.parent.name.equals("Releases");
	}

	private static void createRelease(File topicFile, Folder u) throws IOException {
		File subTopicFile = new File(topicFile, u.filename());
		subTopicFile.mkdir();
		System.out.println("\t\t" + subTopicFile.getPath());
		Release release = new Release(u);
		release.name = u.name;
		String json = Serialiser.serialise(release);
		FileUtils.writeStringToFile(new File(subTopicFile, "data.json"), json);
	}
}
