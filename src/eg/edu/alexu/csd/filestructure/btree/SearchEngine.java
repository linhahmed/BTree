package eg.edu.alexu.csd.filestructure.btree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class SearchEngine implements ISearchEngine {
	private IBTree<String, ArrayList<SearchResult>> docTree;
	private Map<String, SearchResult> dataOfDoc;

	public SearchEngine() {
		dataOfDoc = new HashMap<>();
		docTree = new BTree<>(5);
	}

	@Override
	public void indexWebPage(String filePath) {
		Document xmlDoc = getDocument(filePath);
		NodeList docList = xmlDoc.getElementsByTagName("doc");
		System.out.println(docList.item(0).getTextContent());
		for (int i = 0; i < docList.getLength(); i++) {
			dataOfDoc = getDocumentMap(docList, i);
			for (Map.Entry<String, SearchResult> entry : dataOfDoc.entrySet()) {
				ArrayList<SearchResult> SResult = docTree.search(entry.getKey());
				if (SResult == null) {
					ArrayList<SearchResult> results = new ArrayList<>();
					results.add(entry.getValue());
					docTree.insert(entry.getKey(), results);
				} else {
					SResult.add(entry.getValue());
				}
			}
		}

	}
	private Document getDocument(String docString) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			return builder.parse(new InputSource(docString));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}

	private Map<String, SearchResult> getDocumentMap(NodeList docList, int i) {
		Map<String, SearchResult> docWords = new HashMap<>();
		Node document = docList.item(i);
		String doc_id = document.getAttributes().getNamedItem("id").getTextContent();
		String[] docContent = document.getTextContent().split("\\s+");
		for (int j = 0; j < docContent.length; j++) {
			String word = docContent[j].toLowerCase();
			if (docWords.containsKey(word)) {
				docWords.get(word).setRank(docWords.get(word).getRank() + 1);
			} else {
				SearchResult result = new SearchResult(doc_id,1);
				result.setId(doc_id);
				result.setRank(1);
				docWords.put(word, result);
			}
		}
		return docWords;
	}

	@Override
	public void indexDirectory(String directoryPath) {
                if(directoryPath == null || directoryPath.isEmpty()) 
                        throw new RuntimeErrorException(new Error());
		File folder = new File(directoryPath);
                if(!folder.exists()) 
                        throw new RuntimeErrorException(new Error());
                if(folder.isDirectory()){
                        File[] listOfFiles = folder.listFiles();
		        for (int i = 0; i < listOfFiles.length; i++) {
			      if (listOfFiles[i].isDirectory()) 
				  this.indexDirectory(listOfFiles[i].getPath());
                              else 
			          this.indexWebPage(listOfFiles[i].getPath());
		        }
                }else
			throw new RuntimeErrorException(new Error());
		
	}

	@Override
	public void deleteWebPage(String filePath) {
		Document xmlDoc = getDocument(filePath);
		NodeList docList = xmlDoc.getElementsByTagName("doc");
		for (int i = 0; i < docList.getLength(); i++) {
			dataOfDoc = getDocumentMap(docList, i);
			for (Map.Entry<String, SearchResult> entry : dataOfDoc.entrySet()) {
				ArrayList<SearchResult> SResult = docTree.search(entry.getKey());
				if (SResult != null) {
					for (int j = 0; j < SResult.size(); j++) {
						if (SResult.get(j).getId().equals(entry.getValue().getId())) {
							SResult.remove(j);
						}
					}
				}
			}
		}
	}

	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

}
