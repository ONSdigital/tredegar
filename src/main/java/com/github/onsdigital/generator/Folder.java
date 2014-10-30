package com.github.onsdigital.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.timeseries.Timeseries;

public class Folder {

	public int index;
	public String name;
	public Folder parent = null;
	private Map<String, Folder> children = new HashMap<>();
	public String lede;
	public String more;
	public Timeseries headline;
	public List<Timeseries> timeserieses = new ArrayList<>();

	/**
	 * Sanitises folder names to <code>[a-zA-Z0-9]</code>.
	 * 
	 * @param name
	 *            The string to be sanitised.
	 * @return A sanitised string.
	 */
	public String filename() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			String character = name.substring(i, i + 1);
			if (character.matches("[a-zA-Z0-9]")) {
				result.append(character);
			}
		}
		return result.toString().toLowerCase();
	}

	public void addChild(Folder child) {
		this.children.put(child.filename(), child);
	}

	public void addChildren(Collection<Folder> children) {
		for (Folder folder : children) {
			this.children.put(folder.filename(), folder);
		}
	}

	public Folder getChild(String fileName) {
		return this.children.get(fileName);
	}

	public Collection<Folder> getChildren() {
		return children.values();
	}

	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return StringUtils.equals(name, ((Folder) obj).name);
	}

}
