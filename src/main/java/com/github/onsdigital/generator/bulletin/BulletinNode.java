package com.github.onsdigital.generator.bulletin;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.onsdigital.json.bulletin.Bulletin;

class BulletinNode {

	private BulletinNode parent;
	private String name;
	private Set<BulletinNode> children;
	private BulletinList bulletinList;

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
	public BulletinNode(String name, BulletinNode node) {
		this.name = name;
		this.parent = node;
	}

	/**
	 * @return {@link #parent}
	 */
	public BulletinNode parent() {
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
	BulletinNode getChild(String name) {

		// Lazily instantiate:
		if (children == null) {
			children = new HashSet<>();
		}

		// Return an existing node if there is one:
		for (BulletinNode node : children) {
			if (StringUtils.equals(name, node.name)) {
				return node;
			}
		}

		// Otherwise create a new child:
		BulletinNode child = new BulletinNode(name, this);
		children.add(child);
		return child;
	}

	/**
	 * @return {@link #children}
	 */
	public Set<BulletinNode> children() {
		return children;
	}

	void addBulletin(Bulletin bulletin) {
		bulletinList().add(bulletin);
	}

	public BulletinList bulletinList() {
		// Lazily instantiate:
		if (bulletinList == null) {
			bulletinList = new BulletinList();
		}
		return bulletinList;
	}
}
