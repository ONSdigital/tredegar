package com.github.onsdigital.json.taxonomy;

import java.util.ArrayList;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.ChildT1;
import com.github.onsdigital.json.Data;

public class DataT1 extends Data {

	public DataT1(Folder folder) {
		super(folder, 1);
		level = "t1";

		children = new ArrayList<>();
		for (Folder child : folder.children) {
			children.add(new ChildT1(child, child.index));
		}
		sort(children);
	}

}
