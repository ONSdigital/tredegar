package com.github.onsdigital.json;

import java.net.URI;

import com.github.onsdigital.json.dataset.Dataset;
import com.github.onsdigital.json.markdown.Article;
import com.github.onsdigital.json.markdown.Bulletin;

/**
 * The minimal fields required by the data about every item on the website.
 * 
 * @author david
 *
 */
public class Reference extends DataItem {

	public String summary;

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
	 * @param bulletin
	 *            The item to be referenced.
	 */
	public Reference(Bulletin bulletin) {
		this.name = bulletin.name;
		this.uri = bulletin.uri;
		this.summary = bulletin.summary;
	}

	/**
	 * Creates a "link" item. This is useful for referencing an item, e.g. in a
	 * list of "related" items.
	 * 
	 * @param dataset
	 *            The item to be referenced.
	 */
	public Reference(Dataset dataset) {
		this.name = dataset.name;
		this.uri = dataset.uri;
		this.summary = dataset.summary;
	}

	/**
	 * Creates a "link" item. This is useful for referencing an item, e.g. in a
	 * list of "related" items.
	 * 
	 * @param article
	 *            The item to be referenced.
	 */
	public Reference(Article article) {
		this.name = article.name;
		this.uri = article.uri;
		this.summary = article.title;
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
