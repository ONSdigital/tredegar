package com.github.onsdigital.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * <p>
 * {@link ONSQueryBuilder} hides details of elastic search query builders with
 * the aim of simplifying query building for the purpose
 * </p>
 * <p>
 * By default it queries all documents under all indexes
 * </p>
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

	public ONSQueryBuilder(String index) {
		this.index = index;
	}

	public String getQuery() {
		return query;
	}

	/**
	 * Query to be searched in documents. The documents with fields that has
	 * values starting with given query will be returned
	 * 
	 * @param query
	 * @return
	 */
	public ONSQueryBuilder setQuery(String query) {
		this.query = StringUtils.isEmpty(query) ? query : (query + "*");
		return this;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	/**
	 * Set type to query under index, if not set, all documents are queried
	 * under the index set.
	 * 
	 * @param type
	 * @return
	 */
	public ONSQueryBuilder setType(String type) {
		this.type = type;
		return this;
	}

	public int getFrom() {
		return from;
	}

	/**
	 * Queried result will return documents starting from given index. Useful
	 * for paging. Default is zero
	 * 
	 * @param from
	 * @return
	 */
	public ONSQueryBuilder setFrom(int from) {
		this.from = from;
		return this;
	}

	public int getSize() {
		return size;
	}

	/**
	 * By default 10 documents are returned from the result set. Set this value
	 * to increase or decrease the number of results fetched
	 * 
	 * @param size
	 * @return
	 */
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

	/**
	 * <p>
	 * All the indexed fields of the documents are queried against the given
	 * query if there is one set. In order to specify certain fields to be
	 * queried set fields array
	 * </p>
	 * 
	 * If no query is set, this value does not have any affect and all documents
	 * will be returned
	 * 
	 * @param fields
	 * @return
	 */
	public ONSQueryBuilder setFields(String... fields) {
		this.fields = fields;
		return this;
	}
}
