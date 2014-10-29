package com.github.onsdigital.generator.bulletin;

import java.util.HashSet;
import java.util.Set;

import com.github.onsdigital.json.bulletin.Bulletin;

class BulletinData {

	static BulletinNode rootNode = new BulletinNode("root", null);
	static Set<Bulletin> bulletins = new HashSet<>();
}
