package com.github.onsdigital.json.taxonomy;

import java.net.URI;

/**
 * 
 * Holds name and uri of any data item to be referenced from taxonomy home pages
 * 
 */
public class DataItemLink {
	public String name;
	public URI uri;

	public DataItemLink(String name, URI uri) {
		this.name = name;
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		int result = 0;
		if (uri != null) {
			result = uri.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (uri != null) {
			result = uri.equals(((DataItemLink) obj).uri);
		}
		return result;
	}
}
