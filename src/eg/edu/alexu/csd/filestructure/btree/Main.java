package eg.edu.alexu.csd.filestructure.btree;

public class Main {
	public static void main(String[] args) {
		IBTree<Integer, String> btree = new BTree<Integer, String>(2);
		btree.insert(5, "");
		btree.insert(16, "");
		btree.insert(22, "");
		btree.insert(45, "");
		btree.insert(2, "");
		btree.insert(10, "");
		btree.insert(18, "");
		btree.insert(30, "");
		btree.insert(50, "");
		btree.insert(12, "");
		btree.insert(31, "");
	//	btree.insert(16, "");
		
		((BTree<Integer, String>) btree).print();
	}

}