package com.github.onsdigital.util;

import org.junit.Test;

public class SearchConnectionManagerTest {

	@Test(expected = IllegalArgumentException.class)
	public void testOutOfRangePort() {
		SearchConnectionManager manager = new SearchConnectionManager(
				"elsaticSearch", "completelymadeupsillyhostname", 232345342);
		try {
			manager.openConnection();
		} finally {
			manager.closeConnection();
		}
	}
}
