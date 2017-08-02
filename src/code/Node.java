package code;
import java.util.*;
import java.awt.*;

/**
 * Class representing a Node (intersection) and its data
 * @author Petra P
 *
 */
public class Node {
	public ArrayList<Segment> segments = new ArrayList<Segment>(); //stores which segments are connected to this node
    public ArrayList<Road> roads = new ArrayList<Road>(); //stores road objects connected to this node
    public ArrayList<String> rdNames = new ArrayList<String>(); //stores which road names are connected to this node
	public int nodeID;
    public double latitude;
    public double longitude;

    public Location location;
    public Point pt;
    public Color color = Color.BLACK;
    
    //AssignmentTwo declarations
    public Map<Node, Segment> neighbours = new HashMap<Node, Segment>(); //maps from a neighbouring node to the segment that connect them
    public Map<Segment, Node> nodeNeighbours = new HashMap<Segment, Node>(); //maps from a segment from this node to the node on the other side
    public double costToGoal;
    public double costFromStart;
    public Node parentNode; //parent node for A* search
    
    public int count;
    public int reachBack;
    public Queue<Node> children = new ArrayDeque<Node>();

    /**
     * Constructor for objects of class Node
     */
    public Node(int id, double latNum, double longNum)
    {
        setID(id);
        setLatitude(latNum);
        setLongitude(longNum);
        location = Location.newFromLatLon(this.latitude, this.longitude);
    }

    public void setID(int id){
       this.nodeID = id;
    }

    public void setLatitude(double lat){
        this.latitude = lat;
    }

    public void setLongitude(double lon){
        this.longitude = lon;
    }

    public void addSegment(Segment s){ //adds a segment to the arraylist
        segments.add(s);
    }
    
    public void addRoad(Road r){
    	roads.add(r);
    	String rName = r.getName();
    	if(rdNames.contains(rName) == false){
    		rdNames.add(rName);
    	}
    }
    
    public void addNeighbour(Node n, Segment s){
    	neighbours.put(n, s);
    	nodeNeighbours.put(s, n);
    }
    
    public Map<Node, Segment> getNeighbours(){
    	return neighbours;
    }
    
    public ArrayList<String> getRoads(){
    	return rdNames;
    }
    
    public void setDrawColor(Color c){
    	color = c;
    }

    public void draw(Graphics g, Location origin, double scale){
    	g.setColor(color);
        pt = location.asPoint(origin, scale);
        g.fillRect(pt.x, pt.y, 2, 2);
    }
}
