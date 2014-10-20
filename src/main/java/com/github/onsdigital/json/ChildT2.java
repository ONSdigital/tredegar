package com.github.onsdigital.json;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.taxonomy.Detail;

public class ChildT2 extends TaxonomyNode {

	public List<Detail> detail = new ArrayList<>();

	public ChildT2(Folder folder, int index) {
		super(folder);
		this.index = index;
		if (index == 1) {
			buildList1();
		} else if (index == 2) {
			buildList2();
		} else if (index == 3) {
			buildList3();
		}
	}

	private void buildList1() {
		Detail detail;
		detail = new Detail("Gross Domestic Product", "£390383", "m", "1st Quarter 2014");
		this.detail.add(detail);
		detail = new Detail("GDP Quarter on Quarter Growth", "0.8", "%", "1st Quarter 2014");
		this.detail.add(detail);
		detail = new Detail("GDP Change on same Quarter a year ago", "0.3", "%", "1st Quarter 2014");
		this.detail.add(detail);
		detail = new Detail("Production Index", "97.5", "", "1st Quarter 2014");
		this.detail.add(detail);
	}

	private void buildList2() {
		Detail detail;
		detail = new Detail("CPI Index", "126.7", "", "January 2014");
		this.detail.add(detail);
		detail = new Detail("CPI Annual Rate Percentage Change over 12 months", "1.9", "%", "January 2014");
		this.detail.add(detail);
		detail = new Detail("CPIH Index", "124.7", "", "January 2014");
		this.detail.add(detail);
		detail = new Detail("CPIH Annual Rate Percentage Change over 12 months", "1.8", "%", "January 2014");
		this.detail.add(detail);
	}

	private void buildList3() {
		Detail detail;
		detail = new Detail("Employment Rate", "72.9", "%", "Feb — Apr 2014");
		this.detail.add(detail);
		detail = new Detail("Unemployment Rate", "6.6", "%", "Feb — Apr 2014");
		this.detail.add(detail);
	}

}
