package com.github.onsdigital.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * {@link ONSQueryBuilder} hides details of elastic search query builders with
 * the aim of simplifying query building for the purpose
 * 
 * by default it queries all documents under all indexes
 * 
 * 
 * @author boorhun
 *
 */
public class ONSQueryBuilder {

	private static final String ALL_FIELDS = "_all";

	String query;
	String index;
	String type;
	int from;
	int size = 10;
	String[] fields;

	public String getQuery() {
		return query;
	}

	public ONSQueryBuilder setQuery(String query) {
		this.query = StringUtils.isEmpty(query) ? query : (query + "*");
		return this;
	}

	public String getIndex() {
		return index;
	}

	public ONSQueryBuilder setIndex(String index) {
		this.index = index;
		return this;
	}

	public String getType() {
		return type;
	}

	public ONSQueryBuilder setType(String type) {
		this.type = type;
		return this;
	}

	public int getFrom() {
		return from;
	}

	public ONSQueryBuilder setFrom(int from) {
		this.from = from;
		return this;
	}

	public int getSize() {
		return size;
	}

	public ONSQueryBuilder setSize(int size) {
		this.size = size;
		return this;
	}

	public String[] getFields() {
		if (ArrayUtils.isEmpty(fields)) {
			return new String[] { ALL_FIELDS };
		}
		return fields;
	}

	public ONSQueryBuilder setFields(String... fields) {
		this.fields = fields;
		return this;
	}
}
