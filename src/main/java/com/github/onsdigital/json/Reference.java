package com.github.onsdigital.json;

import java.net.URI;

/**
 * The minimal fields required by the data about every item on the website.
 * 
 * @author david
 *
 */
public class Reference extends DataItem {

	/**
	 * Creates a "link" item. This is useful for referencing an item, e.g. in a
	 * list of "related" items.
	 * 
	 * @param target
	 *            The item to be referenced.
	 */
	public Reference(DataItem target) {
		this.name = target.name;
		this.uri = target.uri;
	}

	/**
	 * Creates a "link" item. This is useful for referencing an item, e.g. in a
	 * list of "related" items.
	 * 
	 * @param name
	 *            The name for the item.
	 * @param uri
	 *            The URI of the item.
	 */
	public Reference(String name, URI uri) {
		this.name = name;
		this.uri = uri;
	}

}
