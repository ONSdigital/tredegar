package com.github.onsdigital.generator;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.github.davidcarboni.ResourceUtils;
import com.github.davidcarboni.restolino.json.Serialiser;

public class DatasetsCsv {

	static Set<Folder> folders;

	public static void buildFolders() throws IOException {
		Serialiser.getBuilder().setPrettyPrinting();
		Reader reader = ResourceUtils.getReader("/datasets.csv");

		String taxonomyNode = null;
		String title = null;
		Folder taxonomyNodeFolder = null;
		Folder titleFolder = null;
		int taxonomyNodeCounter = 0;
		int titleCounter = 0;

		try (CSVReader csvReader = new CSVReader(reader)) {

			String[] headers = csvReader.readNext();
			int nodeIndex = ArrayUtils.indexOf(headers, "TaxonomyNode");
			int titleIndex = ArrayUtils.indexOf(headers, "Title");

			folders = new HashSet<>();
			String[] row;
			while ((row = csvReader.readNext()) != null) {

				if (StringUtils.isNotBlank(row[nodeIndex])) {
					taxonomyNode = row[nodeIndex];
					taxonomyNodeFolder = new Folder();
					taxonomyNodeFolder.name = taxonomyNode;
					taxonomyNodeFolder.index = taxonomyNodeCounter++;
					titleCounter = 0;
					folders.add(taxonomyNodeFolder);
					title = null;
				}

				if (StringUtils.isNotBlank(row[titleIndex])) {
					title = row[titleIndex];
					titleFolder = new Folder();
					titleFolder.name = title;
					titleFolder.parent = taxonomyNodeFolder;
					titleFolder.index = titleCounter++;
					taxonomyNodeFolder.children.add(titleFolder);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			buildFolders();

			for (Folder folder : folders) {
				System.out.println("folderName: " + folder.name);
				for (Folder childFolder : folder.children) {
					System.out.println("date: " + childFolder.name);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
