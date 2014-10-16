package com.github.onsdigital.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class ValidatorUtilTest {

	@Test
	public void testIsAlphaNumeric() {
		assertFalse("Only alphanumerics allowed",
				ValidatorUtil.isIllegalCharacter("abcdefghijklmnopqrstuvwxyz"));
		assertFalse("Only alphanumerics allowed",
				ValidatorUtil.isIllegalCharacter("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertFalse("Only alphanumerics allowed",
				ValidatorUtil.isIllegalCharacter("0123456789"));
		assertFalse("Only alphanumerics allowed",
				ValidatorUtil.isIllegalCharacter(RandomStringUtils
						.randomAlphanumeric(40)));
	}

	@Test
	public void testIsIllegalCharacter() {
		String[] illegalChars = { "!", ";", "_", "=" };

		for (String illegalChar : illegalChars) {
			assertTrue("non-alphanumerics should result in failure",
					ValidatorUtil.isIllegalCharacter(illegalChar));
		}
	}
}
