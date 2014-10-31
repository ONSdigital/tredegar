package com.github.onsdigital.generator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.generator.bulletin.BulletinContent;
import com.github.onsdigital.generator.data.Data;
import com.github.onsdigital.generator.datasets.DatasetContent;
import com.github.onsdigital.json.Dataset;
import com.github.onsdigital.json.HomeSection;
import com.github.onsdigital.json.bulletin.Bulletin;
import com.github.onsdigital.json.taxonomy.T1;
import com.github.onsdigital.json.taxonomy.T2;
import com.github.onsdigital.json.taxonomy.T3;
import com.github.onsdigital.json.timeseries.Timeseries;
import com.github.onsdigital.json.timeseries.TimeseriesValue;

public class TaxonomyGenerator {

	static File root;

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
		// String subTopic = null;
		Folder themeFolder = null;
		Folder subjectFolder = null;
		Folder topicFolder = null;
		// Folder subTopicFolder = null;
		int themeCounter = 0;
		int subjectCounter = 0;
		int topicCounter = 0;
		// int subTopicCounter = 0;

		Set<Folder> folders = new HashSet<>();

		try (CSVReader csvReader = new CSVReader(reader)) {

			// Column positions:s
			String[] headers = csvReader.readNext();
			System.out.println(ArrayUtils.toString(headers));
			int themeIndex = ArrayUtils.indexOf(headers, "Theme");
			int subjectIndex = ArrayUtils.indexOf(headers, "Subject");
			int topicIndex = ArrayUtils.indexOf(headers, "Topic");
			// int subTopicIndex = ArrayUtils.indexOf(headers, "Subtopic");
			int ledeIndex = ArrayUtils.indexOf(headers, "Lede");
			int moreIndex = ArrayUtils.indexOf(headers, "More");
			// System.out.println("Theme=" + themeIndex + " Subject=" +
			// subjectIndex + " Topic=" + topicIndex + " Subtopic=" +
			// subTopicIndex);
			System.out.println("Theme=" + themeIndex + " Subject=" + subjectIndex + " Topic=" + topicIndex + " Lede=" + ledeIndex + " More=" + moreIndex);

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
					// subTopicCounter = 0;
					if (StringUtils.isNotBlank(row[ledeIndex])) {
						themeFolder.lede = row[ledeIndex];
					}
					if (StringUtils.isNotBlank(row[moreIndex])) {
						themeFolder.more = row[moreIndex];
					}
					folders.add(themeFolder);
					subject = null;
					topic = null;
					// subTopic = null;
				}

				if (StringUtils.isNotBlank(row[subjectIndex])) {
					subject = row[subjectIndex];
					subjectFolder = new Folder();
					subjectFolder.name = subject;
					subjectFolder.parent = themeFolder;
					subjectFolder.index = subjectCounter++;
					topicCounter = 0;
					// subTopicCounter = 0;
					if (StringUtils.isNotBlank(row[ledeIndex])) {
						subjectFolder.lede = row[ledeIndex];
					}
					if (StringUtils.isNotBlank(row[moreIndex])) {
						subjectFolder.more = row[moreIndex];
					}
					themeFolder.addChild(subjectFolder);
					topic = null;
					// subTopic = null;
				}

				if (StringUtils.isNotBlank(row[topicIndex])) {
					topic = row[topicIndex];
					topicFolder = new Folder();
					topicFolder.name = topic;
					topicFolder.parent = subjectFolder;
					topicFolder.index = topicCounter++;
					subjectFolder.addChild(topicFolder);
					// subTopic = null;
					if (StringUtils.isNotBlank(row[ledeIndex])) {
						topicFolder.lede = row[ledeIndex];
					}
					if (StringUtils.isNotBlank(row[moreIndex])) {
						topicFolder.more = row[moreIndex];
					}
				}

				// if (StringUtils.isNotBlank(row[subTopicIndex])) {
				// subTopic = row[subTopicIndex];
				// subTopicFolder = new Folder();
				// subTopicFolder.name = subTopic;
				// subTopicFolder.parent = topicFolder;
				// subTopicFolder.index = subTopicCounter++;
				// topicFolder.children.add(subTopicFolder);
				// }

				String path = StringUtils.join(new String[] { theme, subject, topic }, '/');
				while (StringUtils.endsWith(path, "/")) {
					path = path.substring(0, path.length() - 1);
				}
				System.out.println(path);
			}
		}

		// Set up the taxonomy and trigger CSV parsing:
		Data.setTaxonomy(folders);

		// Walk folder tree:
		root = new File("src/main/taxonomy");
		Folder rootFolder = new Folder();
		rootFolder.name = "Home";
		rootFolder.addChildren(folders);
		createHomePage(rootFolder, root);
		File themeFile;
		File subjectFile;
		File topicFile;
		for (Folder t : folders) {
			themeFile = new File(root, t.filename());
			themeFile.mkdirs();
			System.out.println(themeFile.getAbsolutePath());
			createT2(t, themeFile);
			for (Folder s : t.getChildren()) {
				subjectFile = new File(themeFile, s.filename());
				subjectFile.mkdirs();
				if (s.getChildren().size() == 0) {
					createT3(s, subjectFile);
					// createContentFolders(s.name, subjectFile);
				} else {
					createT2(s, subjectFile);
				}
				System.out.println("\t" + subjectFile.getPath());
				for (Folder o : s.getChildren()) {
					topicFile = new File(subjectFile, o.filename());
					topicFile.mkdirs();
					System.out.println("\t\t" + topicFile.getPath());
					createT3(o, topicFile);
					if (o.getChildren().size() == 0) {
						// createContentFolders(o.name, topicFile);
					}
				}
			}
		}

		System.out.println("You have a grand total of " + Data.size() + " timeseries. Wow.");
	}

	private static void createHomePage(Folder folder, File file) throws IOException {
		// The folder needs to be at the root path:
		T1 t1 = new T1(folder);
		t1.fileName = "/";
		if (StringUtils.isNotBlank(folder.lede)) {
			t1.lede = folder.lede;
			t1.more = folder.more;
		}
		String json = Serialiser.serialise(t1);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);
	}

	private static void createT2(Folder folder, File file) throws IOException {
		if (folder.name.equals("Releases") || (folder.parent != null && folder.parent.name.equals("Releases"))) {
			System.out.println("Do not create json for Releases createT2");
		} else {
			T2 t2 = new T2(folder, folder.index);
			if (StringUtils.isNotBlank(folder.lede)) {
				t2.lede = folder.lede;
				t2.more = folder.more;
			}
			t2.index = folder.index;
			buildT2Sections(t2, folder);

			String json = Serialiser.serialise(t2);
			FileUtils.writeStringToFile(new File(file, "data.json"), json);
		}
	}

	private static void buildT2Sections(T2 t2, Folder folder) throws IOException {
		t2.sections = new ArrayList<HomeSection>();
		for (Folder child : folder.getChildren()) {
			HomeSection section = new HomeSection(child.name, child.filename());
			section.index = child.index;
			t2.sections.add(section);
			List<Folder> t3Folders = getT3Folders(child);
			Collection<URI> timeserieses = getTimeseries(t3Folders);
			for (URI timeseries : timeserieses) {
				section.items.add(timeseries);
			}
		}
		Collections.sort(t2.sections);
	}

	private static List<Folder> getT3Folders(Folder folder) {
		List<Folder> result = new ArrayList<Folder>();

		// If the folder is t3, add it directly:
		if (folder.getChildren().size() == 0) {
			result.add(folder);
		}

		// If it's a t2, recurse:
		for (Folder child : folder.getChildren()) {
			result.addAll(getT3Folders(child));
		}
		return result;
	}

	private static Set<URI> getTimeseries(List<Folder> t3Folders) throws IOException {
		// Keep keys in the order they are added, but allow for de-duplication:
		Set<URI> result = new LinkedHashSet<>();

		// Add the headlines first so that they will appear first
		for (Folder t3Folder : t3Folders) {
			if (t3Folder.headline != null) {
				result.add(t3Folder.headline.uri);
			} else {
				System.out.println("No headline timeseries on " + t3Folder.name);
			}
		}

		// Add the other items in case there aren't enough headline items:
		for (Folder t3Folder : t3Folders) {
			// Limit the number in case we have thousands
			// (this is quite likely at some point)
			int max = 4;
			for (Timeseries timeseries : t3Folder.timeserieses) {
				if (max-- < 0) {
					if (timeseries.uri != null) {
						result.add(timeseries.uri);
					} else {
						System.out.println("No URI defined for " + timeseries + " when scanning to " + t3Folder);
					}
				}
			}
		}

		return result;
	}

	private static void createT3(Folder folder, File file) throws IOException {

		T3 t3 = new T3(folder);
		if (StringUtils.isNotBlank(folder.lede)) {
			t3.lede = folder.lede;
			t3.more = folder.more;
		}
		t3.index = folder.index;

		// Timeseries references:
		if (folder.headline != null && folder.headline.uri != null) {
			t3.headline = folder.headline.uri;
		} else {
			System.out.println("No headline URI set for " + folder.name);
		}
		List<Timeseries> timeserieses = folder.timeserieses;
		t3.items.clear();
		String baseUri = "/" + folder.filename();
		Folder parent = folder.parent;
		while (parent != null) {
			baseUri = "/" + parent.filename() + baseUri;
			parent = parent.parent;
		}
		baseUri += "/timeseries";
		for (Timeseries timeseries : timeserieses) {
			if (timeseries.uri != null) {
				t3.items.add(timeseries.uri);
			} else {
				System.out.println("No URI set for " + timeseries);
			}
		}

		// Stats bulletin references:
		URI statsBulletinHeadline = toStatsBulletinUri(folder, BulletinContent.getHeadlineBulletin(folder));
		if (statsBulletinHeadline != null) {
			t3.statsBulletinHeadline = statsBulletinHeadline;
		}
		
		t3.statsBulletins.clear();
		List<Bulletin> bulletins = BulletinContent.getBulletins(folder);

		if (bulletins != null) {
			System.out.println("bulletins: " + bulletins.size());
			for (Bulletin bulletin : bulletins) {
				if (bulletin.summary != null) {
					URI bulletinUri = toStatsBulletinUri(folder, bulletin);
					t3.statsBulletins.add(bulletinUri);
				}
			}
		}

		t3.datasets.clear();
		List<Dataset> datasets = DatasetContent.getDatasets(folder);

		if (datasets != null) {
			System.out.println("datasets: " + datasets.size());
			for (Dataset dataset : datasets) {
				if (dataset.summary != null) {
					URI datasetUri = toDatasetUri(folder, dataset);
					t3.datasets.add(datasetUri);
				}
			}
		}

		// Serialise
		String json = Serialiser.serialise(t3);
		FileUtils.writeStringToFile(new File(file, "data.json"), json);

		createArticle(folder, file);
		createBulletin(folder, file);
		createDataset(folder, file);
		createTimeseries(folder, file);
	}

	private static URI toStatsBulletinUri(Folder folder, Bulletin bulletin) {
		URI result = null;

		if (bulletin != null) {
			if (bulletin.uri == null) {
				String baseUri = "/" + folder.filename();
				Folder parent = folder.parent;
				while (parent != null) {
					baseUri = "/" + parent.filename() + baseUri;
					parent = parent.parent;
				}
				baseUri += "/bulletins";
				String bulletinFileName = bulletin.fileName;
				String sanitizedBulletinFileName = bulletinFileName.replaceAll("\\W", "");
				bulletin.uri = URI.create(baseUri + "/" + StringUtils.deleteWhitespace(sanitizedBulletinFileName));
			}
			result = bulletin.uri;
		}

		return result;
	}

	private static URI toDatasetUri(Folder folder, Dataset dataset) {
		URI result = null;

		if (dataset != null) {
			if (dataset.uri == null) {
				String baseUri = "/" + folder.filename();
				Folder parent = folder.parent;
				while (parent != null) {
					baseUri = "/" + parent.filename() + baseUri;
					parent = parent.parent;
				}
				baseUri += "/datasets";
				String datasetFileName = dataset.fileName;
				String sanitizedDatasetFileName = datasetFileName.replaceAll("\\W", "");
				dataset.uri = URI.create(baseUri + "/" + StringUtils.deleteWhitespace(sanitizedDatasetFileName));
			}
			result = dataset.uri;
		}

		return result;
	}

	/**
	 * Creates timeseries data.
	 *
	 * @param folder
	 * @param file
	 * @throws IOException
	 */
	private static void createTimeseries(Folder folder, File file) throws IOException {

		List<Timeseries> timeserieses = folder.timeserieses;

		for (Timeseries timeseries : timeserieses) {

			URI uri = timeseries.uri;
			File timeseriesFolder = new File(root, uri.toString());
			File timeseriesFile = new File(timeseriesFolder, "data.json");

			if (!timeseriesFile.exists()) {
				timeseriesFolder.mkdirs();

				Set<TimeseriesValue> data = TimeseriesData.getData(timeseries.cdid());
				if (data != null) {
					timeseries.data = new ArrayList<>(data);
				} else {
					System.out.println("No data for " + timeseries.cdid());
				}
				String json = Serialiser.serialise(timeseries);
				System.out.println(timeseriesFile.getAbsolutePath());
				FileUtils.writeStringToFile(timeseriesFile, json, Charset.forName("UTF8"));
			}
		}
	}

	private static void createArticle(Folder folder, File file) throws IOException {
		File articles = new File(file, "articles");
		articles.mkdir();
		// removed generation pending refactor to reuse AlphaContent approach
	}

	private static void createBulletin(Folder folder, File file) throws IOException {

		List<Bulletin> bulletins = BulletinContent.getBulletins(folder);

		if (bulletins != null && bulletins.size() > 0) {
			File bulletinsFolder = new File(file, "bulletins");
			bulletinsFolder.mkdir();
			for (Bulletin bulletin : bulletins) {
				String bulletinFileName = bulletin.fileName;
				String sanitizedBulletinFileName = bulletinFileName.replaceAll("\\W", "");
				File bulletinFolder = new File(bulletinsFolder, StringUtils.deleteWhitespace(sanitizedBulletinFileName.toLowerCase()));
				String json = Serialiser.serialise(bulletin);
				FileUtils.writeStringToFile(new File(bulletinFolder, "data.json"), json, Charset.forName("UTF8"));
			}
		}
	}

	private static void createDataset(Folder folder, File file) throws IOException {
		List<Dataset> datasets = DatasetContent.getDatasets(folder);

		if (datasets != null && datasets.size() > 0) {
			File datasetsFolder = new File(file, "datasets");
			datasetsFolder.mkdir();
			for (Dataset dataset : datasets) {
				String datasetFileName = dataset.fileName.replaceAll("\\W", "");
				File datasetFolder = new File(datasetsFolder, StringUtils.deleteWhitespace(datasetFileName.toLowerCase()));
				String json = Serialiser.serialise(dataset);
				FileUtils.writeStringToFile(new File(datasetFolder, "data.json"), json, Charset.forName("UTF8"));
			}
		}
	}

}
