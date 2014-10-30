package com.github.onsdigital.generator.bulletin;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.json.bulletin.Bulletin;

class BulletinList {

	Bulletin headline;
	List<Bulletin> bulletins;

	public void add(Bulletin bulletin) {
		if (bulletins == null) {
			bulletins = new ArrayList<>();
		}
		bulletins.add(bulletin);
	}

}
