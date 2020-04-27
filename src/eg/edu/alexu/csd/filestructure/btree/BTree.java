package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;
import java.util.List;

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
		if (key == null)
			throw new RuntimeErrorException(null);
		if (root == null) {
			return null;
		}
		return (V) BTreeSearch(key, root);
	}

	private V BTreeSearch(K key, IBTreeNode<K, V> x) {
		int i = 0;
		while (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0)
			i++;
		if (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) == 0)
			return x.getValues().get(i);
		else if (x.isLeaf())
			return null;
		else
			return BTreeSearch(key, x.getChildren().get(i));
	}

	@Override
	public boolean delete(K key) {
		if (key == null)
			throw new RuntimeErrorException(null);
		V value = search(key);
		if (value == null) return false;
		delete(key, root);
		int h=0;
		for (int i=0;i<2;i++)
			h++;



		return true;
	}

	private void delete(K key, IBTreeNode<K, V> x) {
		int i = 0;
		while (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0)
			i++;
		if (i < x.getNumOfKeys() && x.getKeys().get(i).compareTo(key) == 0) {
			if (x.isLeaf())
				deleteFromLeaf(x, i);
			else
				deleteFromNonLeaf(x, i);
		} else {
			delete(key, x.getChildren().get(i));
		}


	}

	private void deleteFromNonLeaf(IBTreeNode<K, V> x, int i) {

		// TODO Auto-generated method stub

		K k = x.getKeys().get(i);
		V v = x.getValues().get(i);

		if (x.getChildren().get(i).getNumOfKeys() >= getMinimumDegree()) {
			IBTreeNode<K, V> pred = getPred(x, i);
			x.getKeys().set(i, pred.getKeys().get(pred.getNumOfKeys() - 1));
			x.getValues().set(i, pred.getValues().get(pred.getNumOfKeys() - 1));
			delete(pred.getKeys().get(pred.getNumOfKeys() - 1), x.getChildren().get(i));
		} else if (x.getChildren().get(i + 1).getNumOfKeys() >= getMinimumDegree()) {
			IBTreeNode<K, V> succ = getSucc(x, i);
			x.getKeys().set(i, succ.getKeys().get(0));
			x.getValues().set(i, succ.getValues().get(0));
			delete(succ.getKeys().get(0), x.getChildren().get(i + 1));
		} else {

				merge(x, i);
				x.getChildren().get(i).getKeys().remove(k);
				x.getChildren().get(i).getValues().remove(v);
				x.getChildren().get(i).setNumOfKeys(x.getChildren().get(i).getNumOfKeys()-1);
				//
				//IBTreeNode Parent=getParent(k);

				//x=Parent;


			//while (Parent.getNumOfKeys() < getMinimumDegree() - 1)
				//borrow
		}


	}


	private IBTreeNode<K,V> getParent(K k) {
		IBTreeNode<K,V> r=root;
		IBTreeNode y=r;
		while (true){
			int i = 0;
			while (i < r.getNumOfKeys() && k.compareTo(r.getKeys().get(i)) > 0)
				i++;
			if (i < r.getNumOfKeys() && k.compareTo(r.getKeys().get(i)) == 0)
				break;
			//else if (r.isLeaf())
				//return null;
			else {
				y=r;
				r = r.getChildren().get(i);
			}

		}
		return y;
	}

	private void merge(IBTreeNode<K, V> x, int i) {
		// TODO Auto-generated method stub
		IBTreeNode<K, V> child = x.getChildren().get(i);
		IBTreeNode<K, V> sibling=null;
		//merge with left in case of leaf node deletion
		if (i==x.getNumOfKeys())
		sibling = x.getChildren().get(i - 1);
		else
		 sibling = x.getChildren().get(i + 1);
		child.getKeys().add(x.getKeys().get(i));
		child.getValues().add(x.getValues().get(i));
		child.getKeys().addAll(sibling.getKeys());
		child.getValues().addAll(sibling.getValues());

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

	private void deleteFromLeaf(IBTreeNode<K, V> x, int i) {

		IBTreeNode toDel = x;
		K k = x.getKeys().get(i);
		V v=x.getValues().get(i);

		if (toDel.getNumOfKeys() >= getMinimumDegree()) {
			toDel.getKeys().remove(i);
			toDel.getValues().remove(i);
			toDel.setNumOfKeys(toDel.getNumOfKeys() - 1);
		} else if (toDel.getNumOfKeys() < getMinimumDegree()) {
			if (toDel==root){
				toDel.getKeys().remove(i);
				toDel.getValues().remove(i);
				toDel.setNumOfKeys(toDel.getNumOfKeys() - 1);


			}
			else
			fix(toDel, i);
			/*

			IBTreeNode<K,V> parent = getParent(k);

			IBTreeNode<K,V> sibL = null;
			IBTreeNode<K,V> sibR = null;

			int childInParent = 0;
			while (i < parent.getNumOfKeys() && k.compareTo(parent.getKeys().get(i++)) > 0)
				childInParent++;

			if (i>0)
				sibL=parent.getChildren().get(i-1);
			if (i<parent.getNumOfKeys())
				sibR=parent.getChildren().get(i+1);
			if (sibL!=null&&sibL.getNumOfKeys() >= getMinimumDegree()){
				K tempKey = (K) parent.getKeys().get(childInParent-1);
				V tempValue = (V) parent.getValues().get(childInParent-1);

				parent.getKeys().set(childInParent-1,sibL.getKeys().get(sibL.getNumOfKeys()-1));
				parent.getValues().set(childInParent-1,sibL.getValues().get(sibL.getNumOfKeys()-1));

				sibL.getKeys().remove(sibL.getNumOfKeys()-1);
				sibL.getValues().remove(sibL.getNumOfKeys()-1);
				sibL.setNumOfKeys(sibL.getNumOfKeys()-1);


				//for(int j = i;j>0;j--) {
				//	toDel.getKeys().set(j, toDel.getKeys().get(j - 1));
				//	toDel.getValues().set(j, toDel.getValues().get(j - 1));
				//}


				toDel.getKeys().remove(i);
				toDel.getValues().remove(i);
				toDel.getKeys().add(0,tempKey);
				toDel.getValues().add(0,tempValue);
			}
			else if (sibR!=null&&sibR.getNumOfKeys()>=getMinimumDegree()){
				K tempKey = (K) parent.getKeys().get(childInParent);
				V tempValue = (V) parent.getValues().get(childInParent);

				parent.getKeys().set(childInParent,sibR.getKeys().get(0));
				parent.getValues().set(childInParent,sibR.getValues().get(0));

				sibR.getKeys().remove(0);
				sibR.getValues().remove(0);
				sibR.setNumOfKeys(sibR.getNumOfKeys()-1);

				toDel.getKeys().remove(i);
				toDel.getValues().remove(i);
				toDel.getKeys().add(tempKey);
				toDel.getValues().add(tempValue);
			}
			else {
				if (sibL!=null) {
					merge(parent, i - 1);
					sibL.getKeys().remove(k);
					sibL.getKeys().remove(v);
					sibL.setNumOfKeys(sibL.getNumOfKeys()-1);
				}
				else {
					merge(parent,i);
					sibR.getKeys().remove(k);
					sibR.getKeys().remove(v);
					sibR.setNumOfKeys(sibR.getNumOfKeys()-1);
				}


			}


*/

		}

	}

	private void fix(IBTreeNode toDel, int i) {
		K k = (K) toDel.getKeys().get(i);
		V v= (V) toDel.getValues().get(i);
		IBTreeNode<K,V> parent = getParent(k);

		IBTreeNode<K,V> sibL = null;
		IBTreeNode<K,V> sibR = null;

		int childInParent = 0;
		int j=0;
		while (j < parent.getNumOfKeys() && k.compareTo(parent.getKeys().get(j++)) > 0)
			childInParent++;

		if (childInParent>0)
			sibL=parent.getChildren().get(childInParent-1);
		if (childInParent<parent.getNumOfKeys())
			sibR=parent.getChildren().get(childInParent+1);
		if (sibL!=null&&sibL.getNumOfKeys() >= getMinimumDegree()){
			K tempKey = (K) parent.getKeys().get(childInParent-1);
			V tempValue = (V) parent.getValues().get(childInParent-1);

			parent.getKeys().set(childInParent-1,sibL.getKeys().get(sibL.getNumOfKeys()-1));
			parent.getValues().set(childInParent-1,sibL.getValues().get(sibL.getNumOfKeys()-1));

			sibL.getKeys().remove(sibL.getNumOfKeys()-1);
			sibL.getValues().remove(sibL.getNumOfKeys()-1);
			sibL.setNumOfKeys(sibL.getNumOfKeys()-1);
/*
				for(int j = i;j>0;j--) {
					toDel.getKeys().set(j, toDel.getKeys().get(j - 1));
					toDel.getValues().set(j, toDel.getValues().get(j - 1));
				}

 */
			toDel.getKeys().remove(i);
			toDel.getValues().remove(i);
			toDel.getKeys().add(0,tempKey);
			toDel.getValues().add(0,tempValue);
		}
		else if (sibR!=null&&sibR.getNumOfKeys()>=getMinimumDegree()){
			K tempKey = (K) parent.getKeys().get(childInParent);
			V tempValue = (V) parent.getValues().get(childInParent);

			parent.getKeys().set(childInParent,sibR.getKeys().get(0));
			parent.getValues().set(childInParent,sibR.getValues().get(0));

			sibR.getKeys().remove(0);
			sibR.getValues().remove(0);
			sibR.setNumOfKeys(sibR.getNumOfKeys()-1);

			toDel.getKeys().remove(i);
			toDel.getValues().remove(i);
			toDel.getKeys().add(tempKey);
			toDel.getValues().add(tempValue);
		}
		else {
			if (sibL!=null) {
				merge(parent, childInParent );
				sibL.getKeys().remove(k);
				sibL.getKeys().remove(v);
				sibL.setNumOfKeys(sibL.getNumOfKeys()-1);
			}
			else {
				merge(parent,childInParent+1);
				toDel.getKeys().remove(k);
				toDel.getKeys().remove(v);
				toDel.setNumOfKeys(sibR.getNumOfKeys()-1);
			}
			/*
			if (sibL!=null){
				sibL.getKeys().remove(k);
				sibL.getKeys().remove(v);
			}
			else {
				sibR.getKeys().remove(k);
				sibR.getKeys().remove(v);
			}
			 */


		}
	}


}
