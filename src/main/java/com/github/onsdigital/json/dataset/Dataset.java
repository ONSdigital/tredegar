package com.github.onsdigital.json.dataset;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.collection.CollectionItem;
import com.github.onsdigital.json.partial.Email;
import com.github.onsdigital.json.taxonomy.TaxonomyHome;

public class Dataset extends CollectionItem {
	public String nextRelease = "21 November 2014";
	public Email contact = new Email();

	// Exec summary
	public String lede = "Definiebas nam te. Eos viderer albucius conceptam ei, cu vix amet duis indoctum, vim at assum ludus intellegebat. "
			+ "Ex vix fugit discere nominavi, per te tantas exerci elaboraret. Nam ne oblique convenire.Sensibus voluptatibus mei ut, eum sonet nonumy epicurei ne. "
			+ "Sed ea causae ancillae, usu id alia soluta reformidans. Eloquentiam deterruisset mel ut, officiis persecuti cu eos. Pri at nostro veritus facilisi.";
	public String more = "Lorem ipsum dolor sit amet";

	public List<DownloadSection> download = new ArrayList<DownloadSection>();
	public List<NotesSection> notes = new ArrayList<NotesSection>();
	public String summary;
	public URI uri;

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

		NotesSection notesSection = new NotesSection();
		notesSection.data = "These estimates are rounded to the nearest hundred and are different to the unrounded estimates in other published tables.";
		notes.add(notesSection);
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}


}
