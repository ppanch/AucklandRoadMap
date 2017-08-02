package code;
import java.util.*;

/**
 * Class representing a node in the trie structure
 * @author Petra P
 *
 */
public class TrieNode {
	public Map<Character, TrieNode> children;
	public char letter;
	public String value; //stores road name, if there is a road at this node
	
	public TrieNode(){ //constructor for root
		children = new HashMap<Character, TrieNode>();
	}
	
	public TrieNode(char c){ //constructor for new trie node children
		letter = c;
		children = new HashMap<Character, TrieNode>();
	}
	
	public void setValue(String val){
		this.value = val;
	}
	
	public void addChild(TrieNode n){ //add trie node child to map
		children.put(n.letter, n);
	}
	
}
