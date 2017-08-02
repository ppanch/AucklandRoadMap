package code;
import java.util.*;

/**
 * Class representing a node used for an A* search
 * @author Petra P
 *
 */
public class AStarSearchNode {

	public Node thisNode;
	public Node parentNode;
	public double costFromStart;
	public double costToGoal;
	public ArrayList<Node> pathNodes;
	public List<Segment> pathSegments;
	
	public AStarSearchNode(Node thisNode, Node parent, double distFromStart, double estimateToGoal){
		this.thisNode = thisNode;
		this.parentNode = parent;
		this.costFromStart = distFromStart;
		this.costToGoal = estimateToGoal;
	}
	
	public Node getThis(){
		return thisNode;
	}
	
	public Node getParent(){
		return parentNode;
	}
}
