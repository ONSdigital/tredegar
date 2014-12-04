package com.github.onsdigital.json.release;

import com.github.onsdigital.json.Reference;

/**
 * Represents a section in a release
 */
public class ReleaseReference extends Reference {

	public String releaseDate;
	public String nextRelease;

	/**
	 * Creates a reference to a {@link Release}.
	 * <p>
	 * This is a bit fudged for the Alpha in that it's the same release that
	 * displays on both the current and upcoming. The only difference is whether
	 * we show {@link #releaseDate} or {@link #nextRelease}.
	 * 
	 * @param release
	 *            The release to be referenced.
	 */
	public ReleaseReference(Release release) {
		super(release);
		releaseDate = release.releaseDate;
		nextRelease = release.nextRelease;
	}
}
