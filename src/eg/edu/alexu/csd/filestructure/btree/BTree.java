package eg.edu.alexu.csd.filestructure.btree;

import sun.font.DelegatingShape;

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
			for (int l = 0; l < 5; l++) {
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
		if (key == null || value == null) {
			throw new RuntimeErrorException(null);
		}
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
		// loop to reach the right place to add the key & the value
		while (i < node.getNumOfKeys() && node.getKeys().get(i).compareTo(key) < 0) {
			i++;
		}

		// remove duplicates if any
		if (i < node.getNumOfKeys() && node.getKeys().get(i).compareTo(key) == 0) {
			return;
		}
		// if node is leaf
		if (node.isLeaf()) {
			node.getKeys().add(i, key);
			node.getValues().add(i, value);
			node.setNumOfKeys(node.getNumOfKeys() + 1);
		} else {
			// if reached max num of keys then split
			if (node.getChildren().get(i).getNumOfKeys() == 2 * t - 1) {
				split(node, node.getChildren().get(i));
				/*
				 * if (key.compareTo(node.getKeys().get(i)) > 0) { i++; }
				 */
				InsertNonFull(node, key, value);
			} else {
				InsertNonFull(node.getChildren().get(i), key, value);
			}
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
		// remove the splittedNode
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
		if (key == null)
			throw new RuntimeErrorException(null);
		if (root == null) {
			return null;
		}
		return (V) BTreeSearch(key, root);
	}

	private V BTreeSearch(K key, IBTreeNode<K, V> x) {
		int i = 0;
		while (i < x.getKeys().size() && key.compareTo(x.getKeys().get(i)) > 0)
			i++;
		if (i < x.getKeys().size()  && key.compareTo(x.getKeys().get(i)) == 0)
			return x.getValues().get(i);
		else if (x.isLeaf())
			return null;
		else {
			if (i==x.getChildren().size())
			return BTreeSearch(key, x.getChildren().get(i-1));
			else
				return BTreeSearch(key, x.getChildren().get(i));

		}
	}

	@Override
	public boolean delete(K key) {
		if (key == null)
			throw new RuntimeErrorException(null);

		V value = search(key);
		if (value == null) return false;

		delete(key, root,null,false);
		if (root.getNumOfKeys() == 0) {
			if (root.isLeaf())
				root = null;
			else
				root = root.getChildren().get(0);
		}
		return true;
	}

	private void delete(K key, IBTreeNode<K, V> x,IBTreeNode<K,V> parentPS,boolean PS) {
		int i = 0;

		while (i < x.getKeys().size() && key.compareTo(x.getKeys().get(i)) > 0)  i++;

		if (i < x.getKeys().size()&& x.getKeys().get(i).compareTo(key) == 0) {
			if (x.isLeaf())
				deleteFromLeaf(x, i,parentPS,PS);  //case1
			else
				deleteFromNonLeaf(x, i);          //case2
		} else {
			delete(key, x.getChildren().get(i),parentPS,false);
		}

	}

	private void deleteFromNonLeaf(IBTreeNode<K, V> x, int i) {
		K k = x.getKeys().get(i);
		V v = x.getValues().get(i);
		//case2.1

			/*
			x.getKeys().remove(i);
			x.getValues().remove(i);
			x.setNumOfKeys(x.getNumOfKeys() - 1);

			 */
			if (x==getRoot()&&!x.isLeaf()){
				IBTreeNode<K, V> child =x.getChildren().get(i);


				IBTreeNode<K, V> a = null;
				IBTreeNode<K, V> b = null;
				int  childOldKeys =x.getChildren().get(i).getNumOfKeys();
				if (!child.isLeaf()){
					a =child.getChildren().get(childOldKeys);
					b =x.getChildren().get(i+1).getChildren().get(0);
				}
				merge(x, i);
				int aOldKeys=0;
				IBTreeNode<K, V> parent = getParent(k);
				//IBTreeNode<K, V> pp = getParent(parent.getKeys().get(0));





				x.getChildren().get(i).getKeys().remove(k);
				x.getChildren().get(i).getValues().remove(v);
				x.getChildren().get(i).setNumOfKeys(x.getChildren().get(i).getNumOfKeys() - 1);
				if (!child.isLeaf()) {
					aOldKeys=a.getKeys().size();
					a.getKeys().addAll(b.getKeys());
					a.getValues().addAll(b.getValues());
					a.setNumOfKeys(a.getKeys().size());

					while (!a.isLeaf()){
						a.getChildren().addAll(b.getChildren());
						IBTreeNode<K, V> aLastOldChild = a.getChildren().get(aOldKeys);
						IBTreeNode<K, V> aFirstNewChild = a.getChildren().get(aOldKeys+1);
						int s=a.getNumOfKeys();
						aLastOldChild.getKeys().addAll(aFirstNewChild.getKeys());
						aLastOldChild.getValues().addAll(aFirstNewChild.getValues());
						aLastOldChild.setNumOfKeys(aLastOldChild.getKeys().size());
						a.getChildren().remove(aOldKeys+1);
						a=aLastOldChild;
						b=aFirstNewChild;
						aOldKeys=a.getKeys().size();
					}
					child.getChildren().remove(childOldKeys+1);
				}
				return;


			}
			else if (x==getRoot()){
				x.getKeys().remove(i);
				x.getValues().remove(i);
				x.setNumOfKeys(x.getNumOfKeys() - 1);
				return;

			}




		if (x.getChildren().get(i).getNumOfKeys() >= getMinimumDegree()) {
			IBTreeNode<K, V> pred = getPred(x, i);
			IBTreeNode<K,V> parentPS =getParent(pred.getKeys().get(0));
			x.getKeys().set(i, pred.getKeys().get(pred.getNumOfKeys() - 1));
			x.getValues().set(i, pred.getValues().get(pred.getNumOfKeys() - 1));
			delete(pred.getKeys().get(pred.getNumOfKeys() - 1), pred,parentPS,true);
		} //case2.2
		else if (x.getChildren().get(i + 1).getNumOfKeys() >= getMinimumDegree()) {

			IBTreeNode<K, V> succ = getSucc(x, i);
			IBTreeNode<K,V> parentPS =getParent(succ.getKeys().get(0));
			x.getKeys().set(i, succ.getKeys().get(0));
			x.getValues().set(i, succ.getValues().get(0));
			delete(succ.getKeys().get(0), succ,parentPS,true); }
		//case2.3
		else {
			IBTreeNode<K, V> parent = getParent(k);
			IBTreeNode<K, V> pp = getParent(parent.getKeys().get(0));
			int  childOldKeys =x.getChildren().get(i).getNumOfKeys();
			IBTreeNode<K, V> child =x.getChildren().get(i);
			IBTreeNode<K, V> a = null;
			IBTreeNode<K, V> b = null;
			int aOldKeys=0;
//merge with pred.
			merge(x, i);
			if (!child.isLeaf()){
			a =child.getChildren().get(childOldKeys);
			 b =child.getChildren().get(childOldKeys+1);
			}

			x.getChildren().get(i).getKeys().remove(k);
			x.getChildren().get(i).getValues().remove(v);
			x.getChildren().get(i).setNumOfKeys(x.getChildren().get(i).getNumOfKeys() - 1);
			if (!child.isLeaf()) {
				aOldKeys=a.getKeys().size();
			a.getKeys().addAll(b.getKeys());
			a.getValues().addAll(b.getValues());
			a.setNumOfKeys(a.getKeys().size());
	/* when merging a&b if they are not leaf nodes
	* last node from a and first node from b are merged too
	* and repeat until a & b are leaf nodes
	 */
		while (!a.isLeaf()){
			a.getChildren().addAll(b.getChildren());
			IBTreeNode<K, V> aLastOldChild = a.getChildren().get(aOldKeys);
			IBTreeNode<K, V> aFirstNewChild = a.getChildren().get(aOldKeys+1);
			int s=a.getNumOfKeys();
			aLastOldChild.getKeys().addAll(aFirstNewChild.getKeys());
			aLastOldChild.getValues().addAll(aFirstNewChild.getValues());
			aLastOldChild.setNumOfKeys(aLastOldChild.getKeys().size());
			a.getChildren().remove(aOldKeys+1);
			a=aLastOldChild;
			b=aFirstNewChild;
			aOldKeys=a.getKeys().size();
		}
				child.getChildren().remove(childOldKeys+1);
}
			IBTreeNode<K, V> sibL = null;
			IBTreeNode<K, V> sibR = null;
	//fix number of keys property
			if (parent!= getRoot()&&   parent.getNumOfKeys() < getMinimumDegree() - 1) {
				int childInParent = 0;
				int j = 0;
				while (j < pp.getNumOfKeys() && k.compareTo(pp.getKeys().get(j++)) > 0)
					childInParent++;
				if (childInParent > 0)
					sibL = pp.getChildren().get(childInParent - 1);
				if (childInParent < parent.getNumOfKeys())
					sibR = pp.getChildren().get(childInParent + 1);
				while (parent != getRoot() && parent.getNumOfKeys() < getMinimumDegree() - 1) {
					if (sibL != null && sibL.getNumOfKeys() >= getMinimumDegree())
						borrowFromLeft(pp, childInParent, sibL, parent, i, k, v);
					else if (sibR != null && sibR.getNumOfKeys() >= getMinimumDegree())
						borrowFromRight(pp, childInParent, sibR, parent, i, k, v);
					else {
						if (sibL != null)
							merge(pp, childInParent - 1);
						else
							merge(pp, childInParent);
					}
					parent = pp;
				}
			}
		}
	}

	//return the parent of a given node containing key k
	private IBTreeNode<K, V> getParent(K k) {
		IBTreeNode<K, V> r = root;
		IBTreeNode y = r;
		while (true) {
			int i = 0;
			while (i < r.getKeys().size()  && k.compareTo(r.getKeys().get(i)) > 0)
				i++;
			if (i < r.getKeys().size()  && k.compareTo(r.getKeys().get(i)) == 0)
				break;
			else {
				y = r;
				r = r.getChildren().get(i);
			}
		}
		return y;
	}
//merge i & i+1 child of a given node x
	private void merge(IBTreeNode<K, V> x, int i) {
		// TODO Auto-generated method stub
		IBTreeNode<K, V> child = x.getChildren().get(i);
		IBTreeNode<K, V> sibling = null;
		// merge with left in case of leaf node deletion
		if (i == x.getNumOfKeys())
			sibling = x.getChildren().get(i - 1);
		else
			sibling = x.getChildren().get(i + 1);

		child.getKeys().add(x.getKeys().get(i));
		child.getValues().add(x.getValues().get(i));
		child.getKeys().addAll(sibling.getKeys());
		child.getValues().addAll(sibling.getValues());
		child.setNumOfKeys(child.getKeys().size());

		if (!child.isLeaf()) {
			child.getChildren().addAll(sibling.getChildren());
		}

		x.getKeys().remove(i);
		x.getValues().remove(i);
		x.getChildren().remove(i + 1);
		x.setNumOfKeys(x.getKeys().size());
		child.setNumOfKeys(child.getKeys().size());

	}

	private IBTreeNode<K, V> getSucc(IBTreeNode<K, V> x, int i) {
		// TODO Auto-generated method stub
		IBTreeNode<K, V> node = x.getChildren().get(i + 1);
		while (!node.isLeaf())
			node = node.getChildren().get(0);
		return node;
	}

	private IBTreeNode<K, V> getPred(IBTreeNode<K, V> x, int i) {
		// TODO Auto-generated method stub
		IBTreeNode<K, V> node = x.getChildren().get(i);
		while (!node.isLeaf())
			node = node.getChildren().get(node.getNumOfKeys());
		return node;
	}

	private void deleteFromLeaf(IBTreeNode<K, V> x, int i,IBTreeNode<K,V> parentPS,boolean PS) {

		IBTreeNode toDel = x;
		K k = x.getKeys().get(i);
		V v = x.getValues().get(i);
	//case1.1
		if (toDel.getNumOfKeys() >= getMinimumDegree()) {
			toDel.getKeys().remove(i);
			toDel.getValues().remove(i);
			toDel.setNumOfKeys(toDel.getNumOfKeys() - 1);
		} //case1.2
		else if (toDel.getNumOfKeys() < getMinimumDegree()) {
			if (toDel == root) {
				toDel.getKeys().remove(i);
				toDel.getValues().remove(i);
				toDel.setNumOfKeys(toDel.getNumOfKeys() - 1);
			} //case1.3
			else
				fix(toDel, i,parentPS,PS);
		}
	}

	private void fix(IBTreeNode toDel, int i,IBTreeNode<K,V> parentPS,boolean PS) {
		K k = (K) toDel.getKeys().get(i);
		V v = (V) toDel.getValues().get(i);
		IBTreeNode<K, V> parent;
		if (PS)
			parent = parentPS;
		else
			parent=getParent(k);

		IBTreeNode<K, V> sibL = null;
		IBTreeNode<K, V> sibR = null;
		IBTreeNode<K, V> temp = toDel;
		int childInParent = 0;
		int j = 0;
		while (j < parent.getNumOfKeys() && k.compareTo(parent.getKeys().get(j++)) > 0)
			childInParent++;

		if (childInParent > 0)
			sibL = parent.getChildren().get(childInParent - 1);
		if (childInParent < parent.getKeys().size())
			sibR = parent.getChildren().get(childInParent + 1);
	//borrow from left sibling if possible
		if (sibL != null && sibL.getNumOfKeys() >= getMinimumDegree()) {
			temp.getKeys().remove(i);
			temp.getValues().remove(i);
			temp.setNumOfKeys(temp.getKeys().size());
			borrowFromLeft(parent, childInParent, sibL, toDel, i, k, v);

		} //borrow from right sibling if possible
		else if (sibR != null && sibR.getNumOfKeys() >= getMinimumDegree()) {
			temp.getKeys().remove(i);
			temp.getValues().remove(i);
			temp.setNumOfKeys(temp.getKeys().size());
			borrowFromRight(parent, childInParent, sibR, toDel, i, k, v);

		} //else merging
		else {
			IBTreeNode p = null;
			IBTreeNode<K, V> pp = getParent((K) parent.getKeys().get(0));
			IBTreeNode<K, V> ppp=getParent(pp.getKeys().get(0));
			if (sibL != null) {
				merge(parent, childInParent - 1);
				sibL.getKeys().remove(k);
				sibL.getKeys().remove(v);
				sibL.setNumOfKeys(sibL.getNumOfKeys() - 1);

			} else {
				merge(parent, childInParent);
				toDel.getKeys().remove(k);
				toDel.getKeys().remove(v);
				toDel.setNumOfKeys(toDel.getNumOfKeys() - 1);

			}
			//fix number of keys property
			if (parent.getNumOfKeys() < getMinimumDegree() - 1) {
				p = parent;
				K kk = k;
				V vv = v;

				IBTreeNode<K, V> sibLL = null;
				IBTreeNode<K, V> sibRR = null;

				int childInParentt = 0;
				int h = 0;
				while (h < pp.getNumOfKeys() && kk.compareTo(pp.getKeys().get(h++)) > 0)
					childInParentt++;

				if (childInParentt > 0)
					sibLL = pp.getChildren().get(childInParentt - 1);
				if (childInParentt < pp.getNumOfKeys())
					sibRR = pp.getChildren().get(childInParentt + 1);
				while (p != getRoot() && p.getNumOfKeys() < getMinimumDegree() - 1) {
					if (sibLL != null && sibLL.getNumOfKeys() >= getMinimumDegree())
						borrowFromLeft(pp, childInParentt, sibLL, p, i, k, v);
					else if (sibRR != null && sibRR.getNumOfKeys() >= getMinimumDegree())
						borrowFromRight(pp, childInParentt, sibRR, p, i, k, v);
					else {
						if (sibLL != null) {
							merge(pp, childInParentt - 1);
						} else {
							merge(pp, childInParentt);
						}
					}
					p = pp;
					if (p==getRoot()) return;
					 ppp=getParent(pp.getKeys().get(0));
					pp = ppp;

					 h = 0;
					childInParentt=0;
					 K K=(K)p.getKeys().get(0);
					while (h < pp.getNumOfKeys() && K.compareTo(pp.getKeys().get(h++)) > 0)
						childInParentt++;

					if (childInParentt > 0)
						sibLL = pp.getChildren().get(childInParentt - 1);
					else
						sibLL=null;

					if (childInParentt < pp.getNumOfKeys())
						sibRR = pp.getChildren().get(childInParentt + 1);
					else
						sibRR=null;
				}
			}
		}
	}

	private void borrowFromLeft(IBTreeNode<K, V> parent, int childInParent, IBTreeNode<K, V> sibL, IBTreeNode<K,V> toDel,
								int i, K k, V v) {
		K tempKey = k;
		V tempValue = v;
		toDel.getKeys().add(0, parent.getKeys().get(childInParent - 1));
		toDel.getValues().add(0, parent.getValues().get(childInParent - 1));
		toDel.setNumOfKeys(toDel.getKeys().size());

		parent.getKeys().set(childInParent - 1, sibL.getKeys().get(sibL.getNumOfKeys() - 1));
		parent.getValues().set(childInParent - 1, sibL.getValues().get(sibL.getNumOfKeys() - 1));


		sibL.getKeys().remove(sibL.getNumOfKeys() - 1);
		sibL.getValues().remove(sibL.getNumOfKeys() - 1);
		sibL.setNumOfKeys(sibL.getNumOfKeys() - 1);
		if (!sibL.isLeaf()) {
			int t=sibL.getChildren().size()-1;
			toDel.getChildren().add(0, sibL.getChildren().get(t));
			sibL.getChildren().remove(sibL.getChildren().size()-1);
		}
	}

	private void borrowFromRight(IBTreeNode<K, V> parent, int childInParent, IBTreeNode<K, V> sibR, IBTreeNode toDel,
								 int i, K k, V v) {
		K tempKey = k;
		V tempValue = v;
		toDel.getKeys().add(parent.getKeys().get(childInParent));
		toDel.getValues().add(parent.getValues().get(childInParent));
		toDel.setNumOfKeys(toDel.getKeys().size());

		parent.getKeys().set(childInParent, sibR.getKeys().get(0));
		parent.getValues().set(childInParent, sibR.getValues().get(0));

		sibR.getKeys().remove(0);
		sibR.getValues().remove(0);
		sibR.setNumOfKeys(sibR.getNumOfKeys() - 1);

		if (!sibR.isLeaf()) {
			int t=sibR.getChildren().size()-1;
			toDel.getChildren().add( sibR.getChildren().get(0));
			sibR.getChildren().remove(0);
		}
	}
}