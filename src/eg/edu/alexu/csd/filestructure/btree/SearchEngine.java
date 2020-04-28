package eg.edu.alexu.csd.filestructure.btree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SearchEngine implements ISearchEngine {
	private IBTree<String, HashMap<String, Integer>> btree;

	public SearchEngine(int minDegree) {
		btree = new BTree<String, HashMap<String,Integer>>(minDegree);
	}

	@Override
	public void indexWebPage(String filePath) {
		if(filePath == null || filePath.isEmpty())
			throw new RuntimeErrorException(new Error());
		HashMap<String, HashMap<String, Integer>> map = parse(filePath);
		for(String id: map.keySet()) {
			HashMap<String, Integer> words = map.get(id);
			for(String word: words.keySet()) {
				HashMap<String, Integer> wordIndex = btree.search(word);
				if(wordIndex == null) {
					wordIndex = new HashMap<String, Integer>();
					wordIndex.put(id, words.get(word));
					btree.insert(word, wordIndex);
				} else {
					wordIndex.put(id, words.get(word));
				}
			}
		}
	}

	private HashMap<String, HashMap<String, Integer>> parse(String path) {
		HashMap<String, HashMap<String, Integer>> files = new HashMap<>();
		File xmldoc = new File(path);
		if (!xmldoc.exists()) {
			throw new RuntimeErrorException(new Error());
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmldoc);
			Element root = doc.getDocumentElement();
			NodeList rootDocuments = root.getElementsByTagName("doc");
			for (int i = 0; i < rootDocuments.getLength(); i++) {
				Node n = rootDocuments.item(i);
				String body = n.getTextContent();
				HashMap<String, Integer> ranks = getRank(body);
				Node attributeID = n.getAttributes().item(0);
				files.put(attributeID.getNodeValue(), ranks);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}

	private HashMap<String, Integer> getRank(String body) {
		HashMap<String, Integer> rank = new HashMap<>();
		body = body.trim();
		String[] words = body.split("\\s+");
		for (int i = 0; i < words.length; i++) {
			if (rank.get(words[i].toLowerCase()) == null) {
				rank.put(words[i].toLowerCase(), 1);
			} else {
				rank.put(words[i].toLowerCase(), rank.get(words[i].toLowerCase()) + 1);
			}
		}
		return rank;
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
		if(filePath == null || filePath.isEmpty()) throw new RuntimeErrorException(new Error());
		HashMap<String, HashMap<String, Integer>> map = parse(filePath);
		for(String id: map.keySet()) {
			HashMap<String, Integer> words = map.get(id);
			for(String word: words.keySet()) {
				HashMap<String, Integer> wordIndices = btree.search(word);
				if(wordIndices != null) {
					wordIndices.remove(id);
				}
			}
		}
	}

	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {
		return null;
	}

	@Override
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		return null;
	}
}
