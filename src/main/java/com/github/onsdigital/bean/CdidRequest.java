package com.github.onsdigital.bean;

import java.util.List;

public class CdidRequest {

	/** A list of CDIDs, rather than URIs. */
	public List<String> cdids;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("cdidList: ").append(cdids);
		return builder.toString();
	}

}
