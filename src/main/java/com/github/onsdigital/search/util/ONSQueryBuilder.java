package com.github.onsdigital.search.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;

import com.github.onsdigital.configuration.ElasticSearchProperties;

/**
 * 
 * <p>
 * {@link ONSQueryBuilder} hides details of elastic search query builders with
 * the aim of simplifying query building for ONS Alpha search requirements
 * </p>
 * <p>
 * By default it queries all documents under given index.
 * </p>
 * 
 * 
 * @author boorhun
 *
 */
public class ONSQueryBuilder {

	static final String ALL_FIELDS = "_all";
	static final String PRE_TAG = "<strong>";
	static final String POST_TAG = "</strong>";

	String searchTerm;
	String index;
	String[] types;
	int page = 1;
	int size = 10;
	String[] fields;

	public ONSQueryBuilder(String index) {
		this.index = index;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * Query to be searched in documents. The documents with fields that has
	 * values starting with given query will be returned
	 * 
	 * @param searchTerm
	 * @return
	 */
	public ONSQueryBuilder setSearchTerm(String searchTerm) {
		this.searchTerm = StringUtils.isEmpty(searchTerm) ? searchTerm : (searchTerm + "*");
		return this;
	}

	public String getIndex() {
		return index;
	}

	public String[] getTypes() {
		return types;
	}

	/**
	 * Set type to query under index, if not set, all documents are queried
	 * under the index set.
	 * 
	 * @param type
	 * @return
	 */
	public ONSQueryBuilder setTypes(String[] types) {
		this.types = types;
		return this;
	}
	
	/**
	 * Set a single type to search
	 * 
	 */
	public ONSQueryBuilder setType(String type) {
		this.types = new String[1];
		this.types[0]= type;
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

	/**
	 * Builds query with set index, type and query information highlighting all
	 * given fields with html strong tag
	 * 
	 * @return query
	 */
	public String buildQuery() {

		BaseQueryBuilder builder;

		// Return all documents
		if (StringUtils.isEmpty(getSearchTerm())) {
			builder = new MatchAllQueryBuilder();
		} else {
			// return documents with fields containing words that start with
			// given search term
			MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(getSearchTerm(), getFields()).analyzer("ons_synonyms");
			// wrap this up with a function_score capability that allows us to
			// boost the home pages
			float homePageBoostFloat = Float.valueOf((String) ElasticSearchProperties.INSTANCE.getProperty("home"));
			float titleBoostFloat = Float.valueOf((String) ElasticSearchProperties.INSTANCE.getProperty("title"));
			
			multiMatchQueryBuilder.field("title", titleBoostFloat);
			multiMatchQueryBuilder.minimumShouldMatch("2");
			FunctionScoreQueryBuilder functionScoreQueryBuilder = new FunctionScoreQueryBuilder(multiMatchQueryBuilder);
			functionScoreQueryBuilder.add(FilterBuilders.termsFilter("_type", "home"), ScoreFunctionBuilders.factorFunction(homePageBoostFloat));
			builder = functionScoreQueryBuilder;

		}
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.preTags(PRE_TAG).postTags(POST_TAG);
		for (String field : getFields()) {
			highlightBuilder.field(field, 0, 0);
		}

		String query = new SearchSourceBuilder().query(builder).highlight(highlightBuilder).from(calculateFrom()).size(getSize()).toString(); 
		System.out.println("Elastic search query String: " + query);
		return query;

	}

	private int calculateFrom() {
		return getSize() * (getPage() - 1);
	}
}
