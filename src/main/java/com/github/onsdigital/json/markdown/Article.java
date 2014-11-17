package com.github.onsdigital.json.markdown;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.collection.CollectionItem;
import com.github.onsdigital.json.partial.Email;
import com.github.onsdigital.json.taxonomy.TaxonomyHome;

public class Article extends CollectionItem {

	// Top section
	public String nextRelease;
	public Email contact = new Email();

	// Exec summary
	public String lede;
	public String more;

	// Table of contents
	public List<Section> sections = new ArrayList<Section>();
	public List<Section> accordion = new ArrayList<Section>();
	public URI uri;

	// Used to help place bulletins in the taxonomy
	public transient String theme;
	public transient String level2;
	public transient String level3;

	/**
	 * Sets up some basic content.
	 */
	public Article() {
		type = ContentType.article;
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}

}
