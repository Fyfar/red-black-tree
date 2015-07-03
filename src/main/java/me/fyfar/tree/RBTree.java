package me.fyfar.tree;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * @author Yurii Kucherenko
 */
public class RBTree<K extends Comparable<K>, V> {
	private Node root;

	private boolean isRed(Node node) {
		return node != null && node.color == NodeColor.RED;
	}

	private int size(Node node) {
		return node != null ? node.subtreeCount : 0;
	}

	public int size() {
		return size(root);
	}

	public boolean isEmpty() {
		return root == null;
	}

	public V get(K key) {
		return get(root, key);
	}

	private V get(Node node, K key) {
		while (node != null) {
			int resultOfCompare = key.compareTo(node.key);
			if (resultOfCompare < 0) {
				node = node.left;
			} else if (resultOfCompare > 0) {
				node = node.right;
			} else {
				return node.value;
			}
		}

		return null;
	}

	public boolean contains(K key) {
		return get(key) != null;
	}

	public void put(K key, V value) {
		root = put(root, key, value);
		root.color = NodeColor.BLACK;
	}

	private Node put(Node node, K key, V value) {
		if (node == null) {
			return new Node(key, value, NodeColor.RED, 1);
		}

		int resultOfCompare = key.compareTo(node.key);
		if (resultOfCompare < 0) {
			node.left = put(node.left, key, value);
		} else if (resultOfCompare > 0) {
			node.right = put(node.right, key, value);
		} else {
			node.value = value;
		}

		if (isRed(node.right) && !isRed(node.left)) {
			node = rotateLeft(node);
		}
		if (isRed(node.left) && isRed(node.left.left)) {
			node = rotateRight(node);
		}
		if (isRed(node.left) && isRed(node.right)) {
			node = flipColor(node);
		}

		node.subtreeCount = size(node.left) + size(node.right) + 1;
		return node;
	}

	public void delete(K key) {
		if (isEmpty()) {
			throw new NoSuchElementException("RBTree is empty");
		}

		if (!isRed(root.left) && !isRed(root.right)) {
			root.color = NodeColor.RED;
		}

		root = delete(root, key);
		if (!isEmpty()) {
			root.color = NodeColor.BLACK;
		}
	}

	private Node delete(Node node, K key) {
		if (key.compareTo(node.key) < 0) {
			if (!isRed(node.left) && !isRed(node.left.left)) {
				node = moveRedLeft(node);
			}
			node.left = delete(node.left, key);
		} else {
			if (isRed(node.left)) {
				node = rotateRight(node);
			}
			if (key.compareTo(node.key) == 0 && node.right == null) {
				return null;
			}
			if (!isRed(node.right) && !isRed(node.right.left)) {
				node = moveRedRight(node);
			}
			if (key.compareTo(node.key) == 0) {
				Node temp = min(node.right);
				temp.key = node.key;
				temp.value = node.value;
				node.right = deleteMin(node.right);
			} else {
				node.right = delete(node.right, key);
			}

		}

		return balance(node);
	}

	public void deleteMin() {
		if (isEmpty()) {
			throw new NoSuchElementException("RBTree is empty");
		}

		if (!isRed(root.left) && !isRed(root.right)) {
			root.color = NodeColor.RED;
		}

		root = deleteMin(root);

		if (!isEmpty()) {
			root.color = NodeColor.BLACK;
		}
	}

	public void deleteMax() {
		if (isEmpty()) {
			throw new NoSuchElementException("RBTree is empty");
		}

		if (!isRed(root.left) && !isRed(root.right)) {
			root.color = NodeColor.RED;
		}

		root = deleteMax(root);

		if (!isEmpty()) {
			root.color = NodeColor.BLACK;
		}
	}

	private Node deleteMax(Node node) {
		if (isRed(node.left)) {
			node = rotateRight(node);
		}

		if (node.right == null) {
			return null;
		}

		if (!isRed(node.right) && !isRed(node.right.left)) {
			node = moveRedRight(node);
		}

		node.right = deleteMax(node.right);

		return balance(node);
	}

	private Node deleteMin(Node node) {
		if (node.left == null) {
			return null;
		}

		if (!isRed(node.left) && !isRed(node.left.left)) {
			node = moveRedLeft(node);
		}

		node.left = deleteMin(node.left);
		return balance(node);
	}

	private Node balance(Node node) {
		if (isRed(node.right)) {
			node = rotateLeft(node);
		}
		if (isRed(node.left) && isRed(node.left.left)) {
			node = rotateRight(node);
		}
		if (isRed(node.left) && isRed(node.right)) {
			flipColor(node);
		}

		node.subtreeCount = size(node.left) + size(node.right) + 1;
		return node;
	}

	private Node moveRedRight(Node node) {
		flipColor(node);

		if (isRed(node.left.left)) {
			node = rotateRight(node);
			flipColor(node);
		}

		return node;
	}

	private Node moveRedLeft(Node node) {
		flipColor(node);

		if (isRed(node.right.left)) {
			node.right = rotateRight(node.right);
			node = rotateLeft(node);
			flipColor(node);
		}

		return node;
	}

	private Node min(Node node) {
		if (isEmpty()) {
			return null;
		}
		if (node.left == null) {
			return node;
		} else {
			return min(node.left);
		}
	}

	private Node max(Node node) {
		if (isEmpty()) {
			return null;
		}

		if (node.right == null) {
			return node;
		} else {
			return max(node.right);
		}
	}

	public K min() {
		if (isEmpty()) {
			return null;
		}
		return min(root).key;
	}

	public K max() {
		if (isEmpty()) {
			return null;
		}

		return max(root).key;
	}

	private Node rotateRight(Node node) {
		Node newNode = node.left;
		node.left = newNode.right;
		newNode.right = node;
		newNode.color = newNode.right.color;
		newNode.right.color = NodeColor.RED;
		newNode.subtreeCount = node.subtreeCount;
		node.subtreeCount = size(node.left) + size(node.right) + 1;

		return newNode;
	}

	private Node rotateLeft(Node node) {
		Node newNode = node.right;
		node.right = newNode.left;
		newNode.left = node;
		newNode.color = newNode.left.color;
		newNode.left.color = NodeColor.RED;
		newNode.subtreeCount = node.subtreeCount;
		node.subtreeCount = size(node.left) + size(node.right) + 1;

		return newNode;
	}

	private Node flipColor(Node node) {
		node.color = node.color == NodeColor.BLACK ? NodeColor.RED : NodeColor.BLACK;
		node.left.color = node.left.color == NodeColor.BLACK ? NodeColor.RED : NodeColor.BLACK;
		node.right.color = node.right.color == NodeColor.BLACK ? NodeColor.RED : NodeColor.BLACK;
		return node;
	}

	public Iterable<K> keys() {
		return keys(min(), max());
	}

	public Iterable<K> keys(K min, K max) {
		Queue<K> queue = new LinkedList<>();
		if (isEmpty() || min.compareTo(max) > 0) {
			return queue;
		}
		keys(root, queue, min, max);
		return queue;
	}

	private void keys(Node node, Queue<K> queue, K min, K max) {
		if (node == null) {
			return;
		}

		int compareMin = min.compareTo(node.key);
		int compareMax = max.compareTo(node.key);
		if (compareMin < 0) {
			keys(node.left, queue, min, max);
		}
		if (compareMin <= 0 && compareMax >= 0) {
			queue.add(node.key);
		}
		if (compareMax > 0) {
			keys(node.right, queue, min, max);
		}
	}

	public int height() {
		return height(root);
	}

	private int height(Node node) {
		if (node == null) {
			return -1;
		}

		return 1 + Math.max(height(node.left), height(node.right));
	}

	private class Node {
		private NodeColor color;
		private Node left, right;
		private K key;
		private V value;
		private int subtreeCount;

		public Node(K key, V value, NodeColor color, int subtreeCount) {
			this.color = color;
			this.key = key;
			this.value = value;
			this.subtreeCount = subtreeCount;
		}
	}

	private enum NodeColor {
		RED, BLACK
	}
}
