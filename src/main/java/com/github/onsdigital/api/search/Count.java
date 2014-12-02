package com.github.onsdigital.api.search;

public class Count implements Comparable<Count> {

	int count;
	String value;

	Count(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(Count o) {
		return o.count - count;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Ignoring null and incompatible types - aren't going to happen.
	 */
	@Override
	public boolean equals(Object obj) {
		return value.equals(((Count) obj).value);
	}

	@Override
	public String toString() {
		return value + ":" + count;
	}

	// public static void main(String[] args) {
	// Count a = new Count("a");
	// Count b = new Count("b");
	// System.out.println(a.equals(b));
	// }
}
