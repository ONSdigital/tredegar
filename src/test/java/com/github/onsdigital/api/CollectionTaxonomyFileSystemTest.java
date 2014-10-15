package com.github.onsdigital.api;

import static org.junit.Assert.assertFalse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.onsdigital.bean.CollectionSearchResult;

/**
 * Exercises the scanning of a specific part of the taxonomy file system to
 * return a collection of content types (e.g. stats bulletins), most recent
 * first
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionTaxonomyFileSystemTest {
	@Mock
	HttpServletRequest httpServletRequest;
	@Mock
	HttpServletResponse httpServletResponse;
	@InjectMocks
	private CollectionTaxonomyFileSystem collectionTaxonomyFileSystem;

	@Test
	public void testGet() throws Exception {
		Mockito.when(httpServletRequest.getParameter("q")).thenReturn(
				"/home/economy/inflationandpriceindices");
		Mockito.when(httpServletRequest.getParameter("page")).thenReturn("1");
		Mockito.when(httpServletRequest.getParameter("type")).thenReturn(
				"bulletins");

		CollectionSearchResult collectionSearchResult = (CollectionSearchResult) collectionTaxonomyFileSystem
				.get(httpServletRequest, httpServletResponse);
		/*
		 * existing unit tests upon CollectionSearchResult entity exercises all
		 * attributes, so just testing here that scanning the taxonomy file
		 * system works
		 */
		assertFalse(collectionSearchResult.getResults().isEmpty());
	}
}
