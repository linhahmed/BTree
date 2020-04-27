package eg.edu.alexu.csd.filestructure.btree;

public class SearchResult implements ISearchResult {

	private String id;
	private int rank;

	public SearchResult(String id, int rank) {
		this.id=id;
		this.rank=rank;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		this.id=id;
	}

	@Override
	public int getRank() {
		// TODO Auto-generated method stub
		return this.rank;
	}

	@Override
	public void setRank(int rank) {
		// TODO Auto-generated method stub
		this.rank=rank;
	}

}
