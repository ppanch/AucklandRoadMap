package code;

import java.util.Comparator;

/**
 * Comparator used to order priority queue elements by their heuristic
 * @author Petra P
 *
 */
public class NodeComparator implements Comparator<AStarSearchNode>{
	
	@Override
	public int compare(AStarSearchNode n1, AStarSearchNode n2){
		return (int)n1.costToGoal - (int)n2.costToGoal;
	}
}