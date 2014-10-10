package com.github.onsdigital.json;

import com.github.onsdigital.generator.Folder;

/**
 * Represents an item on the website that has a display name and a filename.
 * E.g. Taxonomy node, bulletin, etc.
 */
public class FileItem {

	public String name;
	public String fileName;

	public FileItem(Folder folder) {
		name = folder.name;
		fileName = folder.filename();
	}
}
