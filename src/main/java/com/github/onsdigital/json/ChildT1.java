package com.github.onsdigital.json;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.taxonomy.Detail;

public class ChildT1 extends TaxonomyFolder {

	public List<Detail> detail = new ArrayList<>();

	public ChildT1(Folder folder, int index) {
		super(folder);
		this.index = index;
		if (index == 1) {
			buildList1();
		} else if (index == 2) {
			buildList2();
		} else if (index == 3) {
			buildList3();
		} else if (index == 4) {
			buildList4();
		}
	}

	private void buildList1() {
		Detail detail;
		detail = new Detail("Value added of Non-Financial Business Economy", "£936.3", "bn", "1st Quarter 2014");
		this.detail.add(detail);
		detail = new Detail("UK Gross Expenditure on Research and Development", "£27", "bn", "2012");
		this.detail.add(detail);
		detail = new Detail("Chained Volume Measure of Construction Output", "1.2", "%", "Jan 2014");
		this.detail.add(detail);
	}

	private void buildList2() {
		Detail detail;
		detail = new Detail("Gross Domestic Product", "£936.3", "bn", "1st Quarter 2014");
		this.detail.add(detail);
		detail = new Detail("CPI Annual Rate Percentage Change over 12 months", "£27", "bn", "January 2014");
		this.detail.add(detail);
		detail = new Detail("Public Sctor Net Borrowing excluding financial interventions", "£13.3", "bn", "Jan 2014");
		this.detail.add(detail);
	}

	private void buildList3() {
		Detail detail;
		detail = new Detail("Employment Rate", "72.9", "%", "Feb — Apr 2014");
		this.detail.add(detail);
		detail = new Detail("Unemployment Rate", "6.6", "%", "Feb — Apr 2014");
		this.detail.add(detail);
		detail = new Detail("Vacancies", "637,000", "bn", "Mar-May 2014");
		this.detail.add(detail);
	}

	private void buildList4() {
		Detail detail;
		detail = new Detail("Total population (UK) ", "64.1", "m", "Mid-2013 estimate");
		this.detail.add(detail);
		detail = new Detail("Net Migration (UK) ", "212,000", "%", "Year ending Dec 2014");
		this.detail.add(detail);
		detail = new Detail("Number of Births (UK) ", "812,970 ", "", "2013");
		this.detail.add(detail);
	}

}
