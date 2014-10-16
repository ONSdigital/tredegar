package com.github.onsdigital.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Easy drill down to child objects. Solely coded for experimenting with a
 * different coding style. May not make things easier as intended :).
 * 
 * @author Bren
 *
 */

public class JsonHelper {

	JsonObject jsonObject;

	private JsonHelper(JsonElement jsonElement) {
		if (jsonElement != null) {
			this.jsonObject = jsonElement.getAsJsonObject();
		}
	}

	/**
	 * Grab json element for easy object operations.
	 * 
	 * object to grab must be {@link JsonObject} type otherwise it will fail,
	 * parameter is {@link JsonElement} for ease of use. Converts
	 * {@link JsonElement} to {@link JsonObject}
	 * 
	 * @param jsonObject
	 * 
	 * @return
	 */
	public static JsonHelper grab(JsonElement jsonObject) {
		return new JsonHelper(jsonObject);
	}

	public JsonObject getFirstMemberAsObject() {
		return jsonObject.entrySet().iterator().next().getValue()
				.getAsJsonObject();
	}

	public JsonElement getFirstMember() {
		return jsonObject.entrySet().iterator().next().getValue();
	}

	public String getFirstMemberName() {
		return jsonObject.entrySet().iterator().next().getKey();
	}

	public int getNumOfMembers() {
		return this.jsonObject.entrySet().size();
	}

}