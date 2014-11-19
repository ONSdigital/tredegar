package com.github.onsdigital.api;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Serves data files in xls or csv format
 * 
 */
import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.onsdigital.bean.CdidRequest;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.json.timeseries.Timeseries;

/**
 * Provides the ability to request the json for one or more CDIDs.
 * 
 * @author david
 *
 */
@Endpoint
public class Cdid {

	@POST
	public List<Timeseries> post(@Context HttpServletRequest request, @Context HttpServletResponse response, CdidRequest cdidRequest) throws IOException {
		System.out.println("Download request recieved" + cdidRequest);
		return processRequest(cdidRequest);
	}

	private List<Timeseries> processRequest(CdidRequest cdidRequest) throws IOException {

		List<Timeseries> result = new ArrayList<>();
		result.add(new Timeseries());
		List<Path> timeseriesPaths = findTimeseries(cdidRequest.cdids);
		for (Path path : timeseriesPaths) {
			System.out.println(path);
		}
		return result;
	}

	/**
	 * Scans the taxonomy to find the requested timeseries.
	 * 
	 * @param cdids
	 *            The list of CDIDs to find.
	 * @return A list of paths for the given CDIDs, if found.
	 * @throws IOException
	 */
	private List<Path> findTimeseries(final List<String> cdids) throws IOException {
		final List<Path> result = new ArrayList<>();

		Path taxonomy = FileSystems.getDefault().getPath(Configuration.getTaxonomyPath());

		/**
		 * Finds json files inside folders that match a cdid value.
		 */
		FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String filename = file.getFileName().toString();
				String extension = FilenameUtils.getExtension(filename);
				String parent = file.getParent().getFileName().toString();

				if (StringUtils.equalsIgnoreCase("json", extension)) {
					for (String cdid : cdids) {
						if (StringUtils.equalsIgnoreCase(cdid, parent)) {
							result.add(file);
						}
					}
				}

				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(taxonomy, fv);

		return result;
	}

}
