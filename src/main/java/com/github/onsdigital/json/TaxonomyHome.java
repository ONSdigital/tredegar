package com.github.onsdigital.json;

import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;

/**
 * Represents a home page in the taxonomy - T1-T3.
 * 
 * @author david
 *
 */
public class TaxonomyHome extends DataItem {

	public String level;
	public int index;
	public String lede;
	public String more;

	/**
	 * 
	 * @param folder
	 *            The folder for this taxonomy homepage node.
	 */
	public TaxonomyHome(Folder folder) {
		this(folder, true);
	}

	/**
	 * 
	 * @param folder
	 *            The folder for this taxonomy homepage node.
	 * @param parseBreadcrumb
	 *            If true, {@link #breadcrumb} will be created. This is passed
	 *            as false when parsing a breadcrumb in order to avoid
	 *            recursively building breadcrumbs within breadcrumbs.
	 */
	private TaxonomyHome(Folder folder, boolean parseBreadcrumb) {
		type = ContentType.home;
		name = folder.name;
		fileName = folder.filename();

		if (parseBreadcrumb) {
			breadcrumb = new ArrayList<>();
			Folder parent = folder;
			while ((parent = parent.parent) != null) {
				breadcrumb.add(0, new TaxonomyHome(parent, false));
			}
		}

	}
	
}
