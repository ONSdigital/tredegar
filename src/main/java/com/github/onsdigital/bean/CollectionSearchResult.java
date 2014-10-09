package com.github.onsdigital.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.json.Bulletin;

public class CollectionSearchResult {

	private long numberOfResults; // total number of hits
	private List<Map<String, Object>> results; // results

	public CollectionSearchResult(List<File> files) {
		results = new ArrayList<Map<String, Object>>();
		this.numberOfResults = files.size();
		resolve(files);
	}

	void resolve(List<File> files) {
		for (File file : files) {
			Map<String, Object> item = new HashMap<String, Object>();
			try {
				Bulletin json = Serialiser.deserialise(new FileInputStream(file), Bulletin.class);
				item.put("title", json.title);
				String[] urlPathName = file.getPath().split("target/classes/files");
				String[] urlWithoutJson = urlPathName[1].split("bulletin.json");
				item.put("url", urlWithoutJson[0]);
				item.put("releaseDate", json.releaseDate);
				results.add(item);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public long getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(long numberOfHits) {
		this.numberOfResults = numberOfHits;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, Object>> hits) {
		this.results = hits;
	}

}
