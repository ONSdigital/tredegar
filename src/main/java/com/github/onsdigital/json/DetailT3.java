package com.github.onsdigital.json;

public class DetailT3 extends Detail {

	public DetailT3(String name, String number, String unit, String date,
			String lastUpated, String nextUpdate) {
		super(name, number, unit, date);
		this.lastUpated = lastUpated;
		this.nextUpdate = nextUpdate;
	}
}
