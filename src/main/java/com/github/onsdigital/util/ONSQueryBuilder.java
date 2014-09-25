package com.github.onsdigital.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;

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
	int page = 1;
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

	public int getPage() {
		return page;
	}

	/**
	 * Queried result will return documents starting from given index. Useful
	 * for paging. Default is zero
	 * 
	 * @param from
	 * @return
	 */
	public ONSQueryBuilder setPage(int page) {
		this.page = page;
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

	public String buildQuery() {

		BaseQueryBuilder builder;

		// Return all documents
		if (StringUtils.isEmpty(getQuery())) {
			builder = new MatchAllQueryBuilder();
		}
		// return documents with fields containing words that start with given
		// query
		builder = new MultiMatchQueryBuilder(getQuery(), getFields())
				.type(MatchQueryBuilder.Type.PHRASE_PREFIX);

		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.fragmentSize(0);

		for (String field : getFields()) {
			highlightBuilder.field(field);
		}

		return new SearchSourceBuilder().query(builder)
				.highlight(highlightBuilder).from(calculateFrom())
				.size(getSize()).toString();

	}

	private int calculateFrom() {
		return getSize() * (getPage() - 1);
	}
}
