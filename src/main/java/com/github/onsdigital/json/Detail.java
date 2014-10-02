package com.github.onsdigital.json;

public class Detail {

	public String name;
	public String link;
	public String info = "Lorem ipsum dolor sit amet";
	public String number;
	public String unit;
	public String date;
	public String lastUpated;
	public String nextUpdate;
	public String note;
	public boolean headline;

	public Detail(String name, String number, String unit, String date) {
		this.name = name;
		this.link = "#";
		this.number = number;
		this.unit = unit;
		this.date = date;
	}
}
