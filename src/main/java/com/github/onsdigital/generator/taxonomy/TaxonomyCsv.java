package com.github.onsdigital.generator.taxonomy;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.generator.data.Csv;

public class TaxonomyCsv {

	static final String resourceName = "/Taxonomy.csv";

	static String THEME = "Theme";
	static String SUBJECT = "Subject";
	static String TOPIC = "Topic";
	static String LEDE = "Lede";
	static String MORE = "More";

	static Csv csv;

	public static Set<Folder> parse() throws IOException {

		// Read the first worksheet - "Data":
		csv = new Csv(resourceName, "cp1252");
		csv.read();
		csv.getHeadings();

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

		Set<Folder> folders = new HashSet<>();

		for (Map<String, String> row : csv) {

			// Theme Subject Topicrow.keySet()
			if (StringUtils.isNotBlank(row.get(THEME))) {
				theme = row.get(THEME);
				System.out.println("Theme: " + theme);
				themeFolder = new Folder();
				themeFolder.name = theme;
				themeFolder.index = themeCounter++;
				subjectCounter = 0;
				topicCounter = 0;
				if (StringUtils.isNotBlank(row.get(LEDE))) {
					themeFolder.lede = row.get(LEDE);
				}
				if (StringUtils.isNotBlank(row.get(MORE))) {
					themeFolder.more = row.get(MORE);
				}
				folders.add(themeFolder);
				subject = null;
				topic = null;
			}

			if (StringUtils.isNotBlank(row.get(SUBJECT))) {
				subject = row.get(SUBJECT);
				System.out.println("Subject: " + subject);
				subjectFolder = new Folder();
				subjectFolder.name = subject;
				subjectFolder.parent = themeFolder;
				subjectFolder.index = subjectCounter++;
				topicCounter = 0;
				if (StringUtils.isNotBlank(row.get(LEDE))) {
					subjectFolder.lede = row.get(LEDE);
				}
				if (StringUtils.isNotBlank(row.get(MORE))) {
					subjectFolder.more = row.get(MORE);
				}
				themeFolder.addChild(subjectFolder);
				topic = null;
			}

			if (StringUtils.isNotBlank(row.get(TOPIC))) {
				topic = row.get(TOPIC);
				System.out.println("Topic: " + topic);
				topicFolder = new Folder();
				topicFolder.name = topic;
				topicFolder.parent = subjectFolder;
				topicFolder.index = topicCounter++;
				subjectFolder.addChild(topicFolder);
				if (StringUtils.isNotBlank(row.get(LEDE))) {
					topicFolder.lede = row.get(LEDE);
				}
				if (StringUtils.isNotBlank(row.get(MORE))) {
					topicFolder.more = row.get(MORE);
				}
			}

			String path = StringUtils.join(new String[] { theme, subject, topic }, '/');
			while (StringUtils.endsWith(path, "/")) {
				path = path.substring(0, path.length() - 1);
			}
			System.out.println(path);
		}

		return folders;
	}
}
