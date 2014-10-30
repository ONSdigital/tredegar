package com.github.onsdigital.generator.data;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.timeseries.Timeseries;

class Node {

	private Node parent;
	private String name;
	private Set<Node> children;
	private TimeseriesList timeseriesList;

	@Override
	public String toString() {
		return name();
	}

	/**
	 * Initialises {@link #name} and {@link #parent}.
	 * 
	 * @param name
	 *            {@link #name}
	 * @param node
	 *            {@link #parent}
	 */
	public Node(String name, Node node) {
		this.name = name;
		this.parent = node;
	}

	/**
	 * @return {@link #parent}
	 */
	public Node parent() {
		return parent;
	}

	/**
	 * @return {@link #name}
	 */
	public String name() {
		return name;
	}

	/**
	 * Gets a child node, creating it if it doesn't exist.
	 * 
	 * @param name
	 *            The name for the child
	 * @return The requested child
	 */
	Node getChild(String name) {

		// Lazily instantiate:
		if (children == null) {
			children = new HashSet<>();
		}

		// Return an existing node if there is one:
		for (Node node : children) {
			if (StringUtils.equals(name, node.name)) {
				return node;
			}
		}

		// Otherwise create a new child:
		Node child = new Node(name, this);
		children.add(child);
		return child;
	}

	/**
	 * @return {@link #children}
	 */
	public Set<Node> children() {
		return children;
	}

	/**
	 * Adds a {@link Timeseries}
	 * 
	 * @param timeseries
	 *            {@link Timeseries}
	 * @param isHeadline
	 *            If this is a headline timeseries at this node, true.
	 */
	void addTimeseries(Timeseries timeseries, boolean isHeadline) {
		timeseriesList().add(timeseries);
		if (isHeadline) {
			timeseriesList().headline = timeseries;
		}
	}

	/**
	 * @return {@link #timeseriesList}
	 */
	public TimeseriesList timeseriesList() {
		// Lazily instantiate:
		if (timeseriesList == null) {
			timeseriesList = new TimeseriesList();
		}
		return timeseriesList;
	}
}
