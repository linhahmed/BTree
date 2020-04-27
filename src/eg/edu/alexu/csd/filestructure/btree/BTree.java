package eg.edu.alexu.csd.filestructure.btree;

import java.util.List;

import javax.management.RuntimeErrorException;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

	private IBTreeNode<K, V> root;
	private int t;

	public BTree(int minimumdegree) {
		if (minimumdegree < 2) {
			throw new RuntimeErrorException(null);
		}
		this.t = minimumdegree;
	}

	@Override
	public int getMinimumDegree() {
		return t;
	}

	@Override
	public IBTreeNode<K, V> getRoot() {
		return root;
	}

	void print() {
		for (int i = 0; i < root.getNumOfKeys(); i++) {
			System.out.println(root.getKeys().get(i));
		}
		System.out.println("  ");
		if (root.isLeaf()) {
			return;
		} else {
			List<IBTreeNode<K, V>> child = root.getChildren();
			for (int l = 0; l < 4; l++) {
				IBTreeNode<K, V> node = new BTreeNode<K, V>();
				for (int i = 0; i < child.size(); i++) {
					node = child.get(i);
					for (int j = 0; j < node.getNumOfKeys(); j++) {
						System.out.println(node.getKeys().get(j));
					}
					System.out.println("  ");
				}
				child = node.getChildren();
			}

		}
	}

	@Override
	public void insert(K key, V value) {
		// first node
		if (root == null) {
			root = new BTreeNode<K, V>();
			root.setNumOfKeys(1);
			root.getKeys().add(key);
			root.getValues().add(value);
			root.setLeaf(true);
		} else {
			// if reached max num of keys then split
			if (root.getNumOfKeys() == 2 * t - 1) {
				IBTreeNode<K, V> node = root;
				root = new BTreeNode<K, V>();
				root.getChildren().add(node);
				split(root, node);
				root.setLeaf(false);
				InsertNonFull(root, key, value);
			} else { // insert non full
				InsertNonFull(root, key, value);
			}
		}
	}

	private void InsertNonFull(IBTreeNode<K, V> node, K key, V value) {
		int i = 0;
		// if node is leaf
		if (node.isLeaf()) {
			// loop to reach the right place to add the key & the value
			while (i < node.getNumOfKeys() && node.getKeys().get(i).compareTo(key) < 0) {
				i++;
			}
			node.getKeys().add(i, key);
			node.getValues().add(i, value);
			node.setNumOfKeys(node.getNumOfKeys() + 1);
		} else {
			// loop to reach the right place to insert to its child
			while (i < node.getNumOfKeys() && node.getKeys().get(i).compareTo(key) < 0) {
				i++;
			}
			// if reached max num of keys then split
			if (node.getChildren().get(i).getNumOfKeys() == 2 * t - 1) {
				split(node, node.getChildren().get(i));
				if (key.compareTo(node.getKeys().get(i)) > 0) {
					i++;
				}
			}
			InsertNonFull(node.getChildren().get(i), key, value);
		}

	}

	private void split(IBTreeNode<K, V> parent, IBTreeNode<K, V> splittedNode) {
		IBTreeNode<K, V> node = new BTreeNode<K, V>();
		K key = splittedNode.getKeys().get(t - 1);
		V value = splittedNode.getValues().get(t - 1);
		node.setLeaf(splittedNode.isLeaf());
		// Copy right half of the keys from splittedNode to the new node
		for (int i = t; i < splittedNode.getNumOfKeys(); i++) {
			node.getKeys().add(splittedNode.getKeys().get(i));
			node.getValues().add(splittedNode.getValues().get(i));
		}

		if (!splittedNode.isLeaf()) {
			for (int i = t; i <= splittedNode.getNumOfKeys(); i++) {
				// Copy right half of the child pointers from splittedNode to the new node
				node.getChildren().add(splittedNode.getChildren().get(i));
			}
		}

		// remove the right half of the the keys from splittedNode
		while (splittedNode.getKeys().size() > t - 1) {
			splittedNode.getKeys().remove(splittedNode.getKeys().size() - 1);
			splittedNode.getValues().remove(splittedNode.getValues().size() - 1);
		}
		// remove the splitted node
		while (splittedNode.getChildren().size() > splittedNode.getKeys().size() + 1) {
			splittedNode.getChildren().remove(splittedNode.getChildren().size() - 1);
		}
		// update the key size
		node.setNumOfKeys(node.getKeys().size());
		splittedNode.setNumOfKeys(t - 1);

		int i = 0;
		// loop to reach the right place to add the key in the parent node
		while (i < parent.getNumOfKeys() && parent.getKeys().get(i).compareTo(key) < 0) {
			i++;
		}
		parent.getKeys().add(i, key);
		parent.getValues().add(i, value);
		parent.setNumOfKeys(parent.getNumOfKeys() + 1);
		// if the parent have one child or more then add it to index i+1
		if (parent.getChildren().size() >= 1) {
			parent.getChildren().add(i + 1, node);
		} else {
			parent.getChildren().add(node);
		}
	}

	@Override
	public V search(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(K key) {
		// TODO Auto-generated method stub
		return false;
	}

}
