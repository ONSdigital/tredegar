package com.github.onsdigital.json;

import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;

public class DataT3 extends Data {

	public DataT3(Folder folder) {
		super(folder, 3);
		level = "t3";
		lede = "Consumer price inflation is the speed at which the prices of goods and services bought by households rise or fall.";
		more = "Some more content goes here. This will expand and contract if you have JS enabled. If JS is disabled, "
				+ "this will show by default. Yay progressive enhancement!";
		timeSeries = new ArrayList<>();
	}

}
