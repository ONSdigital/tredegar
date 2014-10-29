package com.github.onsdigital.json;

import java.util.List;

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

	/**
	 * The name of the folder that represents this item. This is effectively a
	 * relative URL to reach this item from the parent.
	 */
	public String fileName;
	
	public List<TaxonomyHome> breadcrumb;

}
