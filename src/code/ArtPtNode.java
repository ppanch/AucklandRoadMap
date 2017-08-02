package code;

/**
 * Class represting a node used when looking for articulation points
 * @author Petra P
 *
 */
public class ArtPtNode {
	public Node thisNode;
	public Node parentNode;
	public int count;
	
	public ArtPtNode(Node thisNode, int count, Node parentNode){
		this.thisNode = thisNode;
		this.count = count;
		this.parentNode = parentNode;
	}
}
