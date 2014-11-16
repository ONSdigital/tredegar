package com.github.onsdigital.search.util;

import org.apache.commons.lang3.math.NumberUtils;

public class ElasticSearchFieldUtil {

	public static String getBoost(String field, String floatAsString) {
		StringBuffer boostedField = new StringBuffer();
		boostedField.append(field);
		float titleBoostProperty = NumberUtils.toFloat(floatAsString);
		if (titleBoostProperty != 0.0f) {
			boostedField.append("^");
			boostedField.append(titleBoostProperty);
		}
		return boostedField.toString();
	}

}
