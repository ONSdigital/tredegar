package com.github.onsdigital.json.dataset;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.collection.CollectionItem;
import com.github.onsdigital.json.partial.Email;
import com.github.onsdigital.json.taxonomy.TaxonomyHome;

public class Dataset extends CollectionItem {
	public String nextRelease = "21 November 2014";
	public Email contact = new Email();

	// Exec summary
	public String lede = "This content is not available in the prototype but in future will describe this dataset.";
	public String more = "Real descriptive content will be available in the live version of this website";

	public List<DownloadSection> download = new ArrayList<DownloadSection>();
	public List<NotesSection> notes = new ArrayList<NotesSection>();
	public String summary;

	/** Whether this is a National Statistic: */
	public boolean nationalStatistic;

	public String description;

	/**
	 * Sets up some basic content.
	 */
	public Dataset() {
		type = ContentType.dataset;
		title = "Inflation Summary";
		releaseDate = "19 February 2014";

		// DownloadSection downloadSection = new DownloadSection();
		// downloadSection.title = "Mid-2013";
		// downloadSection.xls = "#";
		// downloadSection.csv = "#";
		// download.add(downloadSection);

		NotesSection notesSection1 = new NotesSection();
		notesSection1.data = "This content is not available in the prototype but in future will provide a note about this dataset.";
		notes.add(notesSection1);
		NotesSection notesSection2 = new NotesSection();
		notesSection2.data = "This content is not available in the prototype but in future will provide a note about this dataset.";
		notes.add(notesSection2);
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		if (result == 0 && name != null) {
			result = name.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj != null && Dataset.class.isAssignableFrom(obj.getClass())) {
			result = StringUtils.equalsIgnoreCase(name, ((Dataset) obj).name);
		}
		return super.equals(obj) || result;
	}

}
