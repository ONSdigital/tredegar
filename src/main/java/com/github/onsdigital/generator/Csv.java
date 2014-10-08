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
import com.github.onsdigital.json.Bulletin;
import com.github.onsdigital.json.Collection;
import com.github.onsdigital.json.Data;
import com.github.onsdigital.json.DataT1;
import com.github.onsdigital.json.DataT2;
import com.github.onsdigital.json.DataT3;
import com.google.common.io.Files;

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

	private static void createHomePage(Folder folder, File file)
			throws IOException {

		// The folder needs to be at the root path:
		Data data = new DataT1(folder);
		data.fileName = "/";
		String json = Serialiser.serialise(data);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
	}

	private static void createT2(Folder folder, File file) throws IOException {

		String json = Serialiser.serialise(new DataT2(folder));
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
	}

	private static void createT3(Folder folder, File file) throws IOException {

		String json = Serialiser.serialise(new DataT3(folder));
		FileUtils.writeStringToFile(new File(file, "data.json"), json);

		createBulletin(folder, file);
		createCollection(folder, file);
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

	private static void createCollection(Folder folder, File file) throws IOException {
		String json = Serialiser.serialise(new Collection());
		FileUtils.writeStringToFile(new File(file, "collection.json"), json);
	}
}
