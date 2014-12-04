package com.github.onsdigital.json.release;

import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.Reference;
import com.github.onsdigital.json.dataset.Dataset;
import com.github.onsdigital.json.markdown.Article;
import com.github.onsdigital.json.markdown.Bulletin;

/**
 * Represents a section in a release
 */
public class ReleaseSection extends Reference {

	/**
	 * Creates an {@link Article} reference.
	 * 
	 * @param article
	 *            The item to be referenced.
	 */
	public ReleaseSection(Article article) {
		super(article);
		this.type = ContentType.article;
	}

	/**
	 * Creates a {@link Bulletin} reference.
	 * 
	 * @param bulletin
	 *            The item to be referenced.
	 */
	public ReleaseSection(Bulletin bulletin) {
		super(bulletin);
		this.type = ContentType.bulletin;
	}

	/**
	 * Creates a {@link Dataset} reference.
	 * 
	 * @param dataset
	 *            The item to be referenced.
	 */
	public ReleaseSection(Dataset dataset) {
		super(dataset);
		this.type = ContentType.dataset;
	}
}
