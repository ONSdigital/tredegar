package com.github.onsdigital.util;

public class ValidatorUtil {

	private static final String ALPHA_NUMERIC = "^[a-zA-Z0-9]*$";

	/**
	 * Checks that string matches acceptable alphanumeric chars
	 * 
	 * @param input
	 *            the string to check
	 * @return true if string contains non-alphaumerics otherwise false
	 */
	public static boolean isIllegalCharacter(String input) {
		if (input.matches(ALPHA_NUMERIC)) {
			return false;
		}
		return true;
	}
}
