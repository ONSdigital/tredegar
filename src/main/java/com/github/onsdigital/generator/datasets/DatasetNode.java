package com.github.onsdigital.generator.datasets;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.Dataset;

class DatasetNode {

	private DatasetNode parent;
	private String name;
	private Set<DatasetNode> children;
	private DatasetList datasetList;

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
	public DatasetNode(String name, DatasetNode node) {
		this.name = name;
		this.parent = node;
	}

	/**
	 * @return {@link #parent}
	 */
	public DatasetNode parent() {
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
	DatasetNode getChild(String name) {

		// Lazily instantiate:
		if (children == null) {
			children = new HashSet<>();
		}

		// Return an existing node if there is one:
		for (DatasetNode node : children) {
			if (StringUtils.equals(name, node.name)) {
				return node;
			}
		}

		// Otherwise create a new child:
		DatasetNode child = new DatasetNode(name, this);
		children.add(child);
		return child;
	}

	/**
	 * @return {@link #children}
	 */
	public Set<DatasetNode> children() {
		return children;
	}

	void addDataset(Dataset dataset) {
		datasetList().add(dataset);
	}

	public DatasetList datasetList() {
		// Lazily instantiate:
		if (datasetList == null) {
			datasetList = new DatasetList();
		}
		return datasetList;
	}
}
