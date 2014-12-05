package com.github.onsdigital.search.util;

import static com.github.onsdigital.util.JsonHelper.grab;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ONSQueryBuilderTest {

	static final String SEARCH_TERM = "searchme";
	static final String SEARCH_INDEX = "i am an index";
	static final String SEARCH_TYPE = "i am not really a type name";
	static final String MATCH_ALL_QUERY = "match_all";
	static final String MULTI_MATCH_QUERY = "multi_match";
	static final String FUNCTION_SCORE_QUERY = "function_score";
	static final String[] ALL_FIELDS = new String[] { ONSQueryBuilder.ALL_FIELDS };
	static final String[] TEST_FIELDS = new String[] { "field1", "field2" };

	@Test
	public void testMatchAllQuery() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX)
				.setFields(TEST_FIELDS);
		JsonObject query = buildQuery(builder);
		String matchType = grab(query.get("query")).getFirstMemberName();
		query.get("query").getAsJsonObject().get("took");
		JsonElement fields = query.getAsJsonObject("query").get("fields");
		assertEquals(MATCH_ALL_QUERY, matchType);
		assertNull(fields);
	}

	@Test
	public void testMultiMatchQuery() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX)
				.setSearchTerm(SEARCH_TERM);
		JsonObject query = buildQuery(builder);
		String matchType = grab(query.get("query")).getFirstMemberName();
		assertEquals(FUNCTION_SCORE_QUERY, matchType);
		testSearchTerm(builder);
	}

	@Test
	public void testSearchTerm() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX)
				.setSearchTerm(SEARCH_TERM);
		testSearchTerm(builder);
	}

	private void testSearchTerm(ONSQueryBuilder builder) {
		JsonObject query = buildQuery(builder);
		JsonObject multiMatchQuery = grab(query.get("query"))
				.getFirstMemberAsObject();
		String searchTerm = grab(multiMatchQuery.get("query"))
				.getFirstMemberAsObject().get("query").getAsString();
		assertEquals(SEARCH_TERM + "*", searchTerm);
	}

	@Test
	public void testFrom() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX).setPage(3)
				.setSize(24);
		JsonObject query = buildQuery(builder);
		int from = query.get("from").getAsInt();
		assertEquals(calculateFrom(3, 24), from);
	}

	@Test
	public void testSize() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX).setSize(12);
		JsonObject query = buildQuery(builder);
		int size = query.get("size").getAsInt();
		assertEquals(12, size);
	}

	@Test
	public void testAllFields() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX)
				.setSearchTerm(SEARCH_TERM);
		JsonObject query = buildQuery(builder);
		JsonObject multiMatchQuery = grab(query.get("query"))
				.getFirstMemberAsObject();
		String fields = grab(multiMatchQuery.get("query"))
				.getFirstMemberAsObject().get("fields").getAsJsonArray().get(0)
				.getAsString();
		assertEquals("_all", fields);
	}

	@Test
	public void testSomeFields() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX)
				.setSearchTerm(SEARCH_TERM).setFields(TEST_FIELDS);
		JsonObject query = buildQuery(builder);
		JsonObject multiMatchQuery = grab(query.get("query"))
				.getFirstMemberAsObject();
		JsonArray array = grab(multiMatchQuery.get("query"))
				.getFirstMemberAsObject().get("fields").getAsJsonArray();
		for (int i = 0; i < ALL_FIELDS.length; i++) {
			assertEquals(TEST_FIELDS[i], array.get(i).getAsString());
		}

	}

	//	@Test
	public void testHihglighFields() {
		ONSQueryBuilder builder = new ONSQueryBuilder(SEARCH_INDEX)
				.setSearchTerm(SEARCH_TERM).setFields("myfield");
		JsonObject query = buildQuery(builder);
		String fieldName = grab(
				query.getAsJsonObject("highlight").get("fields"))
				.getFirstMemberName();
		assertEquals("myfield", fieldName);
	}

	JsonObject buildQuery(ONSQueryBuilder builder) {
		JsonObject query = new JsonParser().parse(builder.buildQuery())
				.getAsJsonObject();
		// System.out.println("Query : " + query);
		return query;
	}

	private int calculateFrom(int page, int size) {
		return size * (page - 1);
	}
}
