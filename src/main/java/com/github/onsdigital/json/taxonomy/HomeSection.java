package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Represents sections on T1 and T2 pages
 * 
 * @author Brn
 */

public class HomeSection implements Comparable<HomeSection> {

	public String name;
	public String link;
	public int index;
	// References to items listed in section
	public List<URI> items = new ArrayList<>();
	public List<Child> children = new ArrayList<Child>();

	public HomeSection(String name, String link) {
		this.name = name;
		this.link = link;
	}

	@Override
	public int compareTo(HomeSection o) {
		return Integer.compare(this.index, o.index);
	}

}
