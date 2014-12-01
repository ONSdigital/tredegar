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
		notesSection1.data = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent quam leo, blandit a turpis non, varius feugiat mi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. In posuere lectus eu laoreet ultrices. Donec ac sodales libero, eget fermentum arcu.";
		notes.add(notesSection1);
		NotesSection notesSection2 = new NotesSection();
		notesSection2.data = "Fusce pharetra, libero in imperdiet ultricies, ex erat tempor dolor, vitae aliquet magna velit in urna. Duis finibus augue velit, ut consectetur risus imperdiet in. Sed tempus congue ante, ac cursus diam porttitor vitae. Vivamus arcu leo, volutpat in mauris ac, lacinia fermentum velit. Sed dictum tortor justo, aliquet facilisis lorem dictum a.";
		notes.add(notesSection2);
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}

}
