package com.github.onsdigital.bean;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DownloadRequest {

	public String type; // xlsx, csv
	public DateVal from;
	public DateVal to;
	public List<String> uriList;
	public List<String> cdidList;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\nType : ").append(type);
		if (uriList != null) {
			builder.append(" uriList: ").append(uriList);
		}
		if (cdidList != null) {
			builder.append(" cdidList: ").append(cdidList);
		}
		builder.append(" From: ").append(ReflectionToStringBuilder.toString(from));
		builder.append(" To: ").append(ReflectionToStringBuilder.toString(to));
		return builder.toString();
	}

}
