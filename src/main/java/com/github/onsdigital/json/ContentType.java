package com.github.onsdigital.json;

public enum ContentType {
	ARTICLE("article"), COLLECTION("collection"), BULLETIN("bulletin"), HOME("home"), METHODOLOY("methodology"), RELEASE("release"), TIMESERIES("timeseries"), UNKNOWN("unknown");

	private final String text;

	private ContentType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
