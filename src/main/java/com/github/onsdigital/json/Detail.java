package com.github.onsdigital.json;

import java.util.ArrayList;
import java.util.List;

public class Detail {

	public String reference = "/home/economy/inflationandpriceindices/timeseries/cbvl.json";

	public String name;
	public String link;
	public String info = "Lorem ipsum dolor sit amet";
	public String number;
	public String unit;
	public String date;
	public String lastUpated;
	public String nextUpdate;
	public String note;

	// These properties are used if it's a T3 headline item:
	public boolean headline;
	public String explanation;
	public String change;
	public String changeDirection;
	public List<Link> statsBulletinHeadlines;

	public Detail(String name, String number, String unit, String date) {
		this.name = name;
		this.link = "#";
		this.number = number;
		this.unit = unit;
		this.date = date;
	}

	public void addStatsBulletinHeadline(String text, String href) {
		if (statsBulletinHeadlines == null)
			statsBulletinHeadlines = new ArrayList<Link>();
		Link link = new Link();
		link.text = text;
		link.href = href;
		statsBulletinHeadlines.add(link);
	}
}
