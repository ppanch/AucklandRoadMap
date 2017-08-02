package code;
import java.util.*;

/**
 * Class representing the Trie structure
 * Used for road search auto complete
 * @author Petra P
 *
 */
public class Trie {
	TrieNode root;
	
	public Trie(){
		root = new TrieNode();
	}
	
	public void addRoad(String road){ //add a new road name to the trie
		road = road.toLowerCase();
		TrieNode node = root;
		for(int i = 0; i < road.length(); i++){
			char c = road.charAt(i);
			if(node.children.get(c) == null){ //if there is no corresponding trie node
				node.addChild(new TrieNode(c)); //create new trie node (child)
			}
			node = node.children.get(c);
		}
		node.setValue(road);
	}
	
	public ArrayList<String> getAll(String prefix){ //given a prefix, get all possible road names
		TrieNode node = root;
		ArrayList<String> words = new ArrayList<String>();
		if(prefix != null && prefix != ""){
			prefix = prefix.toLowerCase();
			for(int i = 0; i < prefix.length(); i++){
				char c = prefix.charAt(i);
				if(node != null){
					node = node.children.get(c);
				}
			}
			if(node != null){
				getAllFromNode(node, words);
			}
		}
		return words;
	}
	
	public void getAllFromNode(TrieNode node, List<String> words){
		if(node.value != null){
			words.add(node.value);
		}
		for(Character c : node.children.keySet()){
			getAllFromNode(node.children.get(c), words);
		}
	}
	
	public String get(String name){//get a specific road name
		TrieNode node = root;
		name = name.toLowerCase();
		for(int i = 0; i < name.length(); i++){
			char c = name.charAt(i);
			if(node.children.get(c) == null){
				return null;
			}
			node = node.children.get(c);
		}
		return node.value;
	}
	
}
