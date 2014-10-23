package com.github.onsdigital.json;

/**
 * The minimal fields required by the data about every item on the website.
 * 
 * @author david
 *
 */
public class DataItem {

	/** Identifies what content type this is. */
	public ContentType type;

	/** The display name of this item. */
	public String name;

	/** The name of the folder that represents this item. */
	public String fileName;
}
