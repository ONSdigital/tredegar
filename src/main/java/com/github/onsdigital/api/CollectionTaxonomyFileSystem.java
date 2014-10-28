package com.github.onsdigital.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.bean.CollectionSearchResult;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.index.ScanFileSystem;
import com.github.onsdigital.util.TaxonomyDateComparator;

@Endpoint
public class CollectionTaxonomyFileSystem {
	private static int multiplierIndex = 10;

	@GET
	public Object get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		return search(extractQuery(request), extractPage(request), request.getParameter("type"));
	}

	private Object search(String query, int page, String type) throws Exception {
		List<File> files = getFiles(query, type);

		Collections.sort(files, new TaxonomyDateComparator());
		Collections.reverse(files);

		int startIndex = getStartIndex(files, page);
		int endIndex = getEndIndex(files, page);
		List<File> pagedFiles = files.subList(startIndex, endIndex);

		CollectionSearchResult collectionSearchResult = new CollectionSearchResult(pagedFiles, page);
		// need to set the number of files on the result otherwise the client
		// loses sight of total results
		collectionSearchResult.setNumberOfResults(files.size());
		return collectionSearchResult;
	}

	private List<File> getFiles(String query, String type) throws IOException {
		List<File> files = new ArrayList<File>();
		String taxonomyRoot = Configuration.getTaxonomyPath() + query;
		final Path rootDir = Paths.get(taxonomyRoot);
		files = ScanFileSystem.getFiles(files, rootDir, type);
		return files;
	}

	private int extractPage(HttpServletRequest request) {
		String page = request.getParameter("page");
		if (StringUtils.isNotEmpty(page) && StringUtils.isNumeric(page)) {
			int pageNumber = Integer.parseInt(page);
			return pageNumber < 1 ? 1 : pageNumber;
		}
		return 1;
	}

	private String extractQuery(HttpServletRequest request) {
		String query = request.getParameter("loc");

		if (StringUtils.isEmpty(query)) {
			throw new RuntimeException("No search query provided");
		}

		// Remove the leading "/home"
		String home = "/home";
		if (query.startsWith(home)) {
			query = query.substring(home.length());
		}

		return query;
	}

	private int getStartIndex(List<File> files, int page) {
		int startIndex = 0;
		int pageMultiplier = page - 1;

		if (pageMultiplier > 0) {
			startIndex += pageMultiplier * multiplierIndex;
		}
		return startIndex;
	}

	private int getEndIndex(List<File> files, int page) {
		int endIndex = 9;
		int pageMultiplier = page - 1;

		if (pageMultiplier > 0) {
			endIndex += pageMultiplier * multiplierIndex;
		}

		if (endIndex > files.size()) {
			endIndex = files.size();
		}

		return endIndex;
	}
}
