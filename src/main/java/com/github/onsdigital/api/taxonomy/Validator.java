package com.github.onsdigital.api.taxonomy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.configuration.Configuration;

public class Validator implements Runnable {
	static void validate() {
		Validator validator = new Validator();
		Thread thread = new Thread(validator, "Json validator");
		thread.setDaemon(true);
		thread.start();
	}

	ExecutorService executorService;
	boolean error;

	@Override
	public void run() {
		Path path = FileSystems.getDefault().getPath(Configuration.getTaxonomyPath());

		executorService = Executors.newCachedThreadPool();
		System.out.println("Validating taxonomy Json");

		validate(path);

		executorService.shutdown();
		System.out.println("Validation in progress...");
		while (!executorService.isTerminated()) {
			sleep();
		}
		if (!error) {
			System.out.println("Validation completed");
		} else {
			System.out.println("Validation issues found");
		}
	}

	private void sleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void validate(Path path) {

		List<Path> jsonFiles = new ArrayList<Path>();
		List<Path> subdirectories = new ArrayList<Path>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

			// Iterate the paths in this directory:
			for (Path item : stream) {
				if (Files.isDirectory(item)) {
					subdirectories.add(item);
				} else if (item.toString().endsWith(".json")) {
					jsonFiles.add(item);
				}
			}
		} catch (IOException e) {
			error = true;
			System.out.println("Error validating directory " + path);
			e.printStackTrace();
		}

		// Submit json for processing
		for (Path json : jsonFiles) {
			submit(json);
		}

		// Iterate subdirectories
		for (Path subdirectory : subdirectories) {
			validate(subdirectory);
		}
	}

	private void submit(final Path json) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try (InputStream input = Files.newInputStream(json)) {
					Serialiser.deserialise(input, Map.class);
				} catch (Exception e) {
					error = true;
					System.out.println("Malformed Json detected: error deserialising " + json + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
				}
			}
		});
	}
}
