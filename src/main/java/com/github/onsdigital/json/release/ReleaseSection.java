package com.github.onsdigital.json.release;

import java.net.URI;

import com.github.onsdigital.json.ContentType;

/**
 * Represents a section in a release
 */
public class ReleaseSection {
	public String title;
	public URI url;
	public String summary;
	public ContentType type;
}
