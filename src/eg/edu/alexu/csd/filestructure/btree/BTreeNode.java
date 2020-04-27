package eg.edu.alexu.csd.filestructure.btree;

import java.util.*;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

	private int m = 0;
	private List<IBTreeNode<K, V>> Children = new ArrayList<IBTreeNode<K, V>>();
	private List<K> Keys = new ArrayList<K>();
	private List<V> Values = new ArrayList<V>();
	private boolean leaf = true;

	@Override
	public int getNumOfKeys() {
		return m;
	}

	@Override
	public void setNumOfKeys(int numOfKeys) {
		m = numOfKeys;
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		leaf = isLeaf;
	}

	@Override
	public List<K> getKeys() {
		return Keys;
	}

	@Override
	public void setKeys(List<K> keys) {
		this.Keys = keys;
	}

	@Override
	public List<V> getValues() {
		return Values;
	}

	@Override
	public void setValues(List<V> values) {
		this.Values = values;
	}

	@Override
	public List<IBTreeNode<K, V>> getChildren() {
		return Children;
	}

	@Override
	public void setChildren(List<IBTreeNode<K, V>> children) {
		this.Children = children;
	}

}
