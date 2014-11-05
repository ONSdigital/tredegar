package com.github.onsdigital.json.timeseries;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.DataItem;
import com.github.onsdigital.json.TaxonomyHome;

public class Timeseries extends DataItem implements Comparable<Timeseries> {

	// Spreadsheet headings
	private String cdid;
	public String seasonalAdjustment;
	public String units;
	public String mainMeasure;
	public String description;
	public String note1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent quam leo, blandit a turpis non, varius feugiat mi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. In posuere lectus eu laoreet ultrices. Donec ac sodales libero, eget fermentum arcu.";
	public String note2 = "Fusce pharetra, libero in imperdiet ultricies, ex erat tempor dolor, vitae aliquet magna velit in urna. Duis finibus augue velit, ut consectetur risus imperdiet in. Sed tempus congue ante, ac cursus diam porttitor vitae. Vivamus arcu leo, volutpat in mauris ac, lacinia fermentum velit. Sed dictum tortor justo, aliquet facilisis lorem dictum a.";

	// Additional information
	public String number;
	public String unit;
	// A unit that needs to go before the number, e.g. £
	public String preUnit;
	public String date;
	public String lastUpated;
	public String nextUpdate;

	// e.g. "Not a national statistic" or "2005 = 100. Not seasonally adjusted"
	public String note;

	// The nectar, the goodness, the very juice of the fireflower: data.
	public Set<TimeseriesValue> data = new LinkedHashSet<>();
	public Set<TimeseriesValue> years = new LinkedHashSet<>();
	public Set<TimeseriesValue> quarters = new LinkedHashSet<>();
	public Set<TimeseriesValue> months = new LinkedHashSet<>();

	// The URI of this timeseries.
	// This is useful when it is referenced from more than one place in the
	// taxonomy. It helps the generator to ensure it only gets created in one
	// place.
	public URI uri;

	public List<URI> relatedBulletins = new ArrayList<>();

	public Timeseries() {
		type = ContentType.timeseries;
		name = "People not in Work";
	}

	public void setBreadcrumb(TaxonomyHome t3) {
		breadcrumb = new ArrayList<>(t3.breadcrumb);
		Folder folder = new Folder();
		folder.name = t3.name;
		TaxonomyHome extra = new TaxonomyHome(folder);
		breadcrumb.add(extra);
	}

	public String cdid() {
		return cdid;
	}

	public void setCdid(String cdid) {
		if (StringUtils.isBlank(cdid)) {
			throw new IllegalArgumentException("Blank CDID");
		}
		this.cdid = StringUtils.trim(cdid);
		fileName = StringUtils.lowerCase(this.cdid.toLowerCase());

		// We don't have metadata for all of the datasets,so
		// this provides a basic fallback by setting the CDID as the name:
		if (StringUtils.isBlank(name)) {
			name = cdid;
		}
	}

	@Override
	public String toString() {
		return cdid;
	}

	@Override
	public int hashCode() {
		if (cdid != null) {
			return cdid.toLowerCase().hashCode();
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Timeseries.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		return StringUtils.equalsIgnoreCase(((Timeseries) obj).cdid, cdid);
	}

	@Override
	public int compareTo(Timeseries o) {
		return this.cdid.compareTo(o.cdid);
	}
}
