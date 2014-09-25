package com.github.onsdigital.generator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class Folder {

	public String name;
	public Folder parent = null;
	public Set<Folder> children = new HashSet<>();

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
			if (character.matches("[a-zA-Z0-9]"))
				result.append(character);
		}
		return result.toString().toLowerCase();
	}

	@Override
	public int hashCode() {
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return StringUtils.equals(name, ((Folder) obj).name);
	}

}
