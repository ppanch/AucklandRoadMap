package code;
import java.util.*;

/**
 * COMP261 Assignment One
 * Main Class containing the data structures for the road graphs and the various functions
 * used for the Auckland Map (e.g. clicking on nodes, searching for roads)
 * 
 * @author Petra P
 */

import javax.swing.JTextField;

import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class AMap extends GUI{
	
	public Map<Integer, Node> nodes = new HashMap<Integer, Node>(); //map of nodeID to node objects
    public Map<Integer, Road> roads = new HashMap<Integer, Road>(); //map of roadID to road objects
    public ArrayList<Segment> segments = new ArrayList<Segment>(); //list of segments
    public Map<String, ArrayList<Integer>> roadNames = new HashMap<String, ArrayList<Integer>>(); //stores road names and their roadIDs
    public Location origin = new Location(0,0);
    public Location clickLoc; //stores location of a mouse click

    public double scale = 80;
    public double zoomFactor = 1.25;
    
    //for size of drawing area
    public double width;
    public double height;

    public Trie trie;
    
    //AssignmentTwo declarations
    public Node startNode;
    public Node goalNode;
    public PriorityQueue<AStarSearchNode> fringe;
    
    
    /**
     * Constructor - sets drawing area size and creates new trie structure
     */
    public AMap()
    {
    	height = getDrawingAreaDimension().getHeight();
        width = getDrawingAreaDimension().getWidth();
        
        trie = new Trie();
    }

    @Override
    public void onLoad(File nodes, File roads, File segments, File polygons) {
    	//reading and storing file data
        readNodes(nodes);
        readRoads(roads);
        readSegments(segments);
    }

    /**
     * reads and stores info from the nodes file into nodes HashMap
     */
    public void readNodes(File nodesFile){
        String line;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(nodesFile));
            while((line = reader.readLine()) != null){
                String[] nodeData = line.split("\t"); //split line to pass variables into Node constructor separately
                int idNum = Integer.parseInt(nodeData[0]);
                double latNum = Double.parseDouble(nodeData[1]);
                double longNum = Double.parseDouble(nodeData[2]);

                Node n = new Node(idNum, latNum, longNum);
                nodes.put(idNum, n);
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * reads and stores info from road files into roads HashMap
     */
    public void readRoads(File roadsFile){
        String line;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(roadsFile));
            line = reader.readLine(); //used to skip column titles            
            while((line = reader.readLine()) != null){
                String[] roadData = line.split("\t"); //split line to pass variables into Road constructor separately
                int idNum = Integer.parseInt(roadData[0]);
                String label = roadData[2];
                String city = roadData[3];
                int oneway = Integer.parseInt(roadData[4]);
                int speed = Integer.parseInt(roadData[5]);
                int roadClass = Integer.parseInt(roadData[6]);
                int notForCar = Integer.parseInt(roadData[7]);
                int notForPed = Integer.parseInt(roadData[8]);
                int notForBikes = Integer.parseInt(roadData[9]);

                Road r = new Road(idNum, label, city, oneway, speed, roadClass, notForCar, notForPed, notForBikes);
                roads.put(idNum, r);
                
                //adding road name to roadNames array
                ArrayList<Integer> roadIDs = roadNames.get(label + ", " + city);
                if(roadIDs != null){ //add new road id (i.e. road name already is in the list)
                	roadNames.get(label + ", " + city).add(idNum);
                }else{ //create new entry
                	roadNames.put(label + ", " + city, new ArrayList<Integer>(Arrays.asList(idNum)));
                }
            }
            buildTrie();
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void buildTrie(){ //build trie by adding all the road names into the trie
    	for(String name : roadNames.keySet()){
    		if(trie.get(name) == null){ //if no entry exists for this road name, create new entry
    			trie.addRoad(name);
    		}
    	}
    }

    /**
     * reads and stores info from segments file into segments list
     * also adds segments to its corresponding nodes and roads
     */
    public void readSegments(File segmentsFile){
        String line;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(segmentsFile));
            line = reader.readLine();
            while((line = reader.readLine()) != null){
                Segment s = new Segment(line);
                segments.add(s);

                //add segment to node and road
                Road r = roads.get(s.roadID);
                r.addSegment(s);
                
                Node n1 = nodes.get(s.nodeID1);
                n1.addSegment(s);
                n1.addRoad(r);
                
                Node n2 = nodes.get(s.nodeID2);
                n2.addSegment(s);
                n2.addRoad(r);
                
                //add node to the other node's neighbours list
                n1.addNeighbour(n2, s);
                n2.addNeighbour(n1, s);
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void redraw(Graphics g){ //draw the map, calls the draw methods in node and segment objects
        for(Node n: nodes.values()){
            n.draw(g, origin, scale);
        }
        for(Segment s : segments){
            Node n1 = nodes.get(s.nodeID1);
            Node n2 = nodes.get(s.nodeID2);
            s.draw(g, origin, scale, n1, n2);
        }
    }
    
    public void resetColor(){ //resets colour to black for any highlighted nodes or roads
    	for(Segment s : segments){
    		s.setDrawColor(Color.BLACK);
    	}
    	for(Node n : nodes.values()){
    		n.setDrawColor(Color.BLACK);
    	}
    }

    /**
     * Highlights and displays node ID and names of connected roads of the nearest intersection
     */
    public void onClick(MouseEvent e){
    	resetColor();
    	double dist = Double.MAX_VALUE;
    	Node nearbyNode = null;
    	clickLoc = Location.newFromPoint(new Point(e.getX(), e.getY()), origin, scale);
    	for(Node n : nodes.values()){
    		nearbyNode = n;
    		break;
    	}
    	if(nearbyNode != null){
    		dist = nearbyNode.location.distance(clickLoc);
    	}
    	for(Node n : nodes.values()){
    		double tempDist = n.location.distance(clickLoc);
    		if(tempDist <= dist){
    			dist = tempDist;
    			nearbyNode = n;
    		}
    	}
    	nearbyNode.setDrawColor(Color.RED);
    	//print out nodeID and the names of all the roads that go through it
    	String rds = "";
    	ArrayList<String> rdNames = nearbyNode.getRoads();
        for(String name : rdNames){
        	rds = name + "\n" + rds;
        }
    	getTextOutputArea().setText("NodeID: " + nearbyNode.nodeID + "\nRoads: " + rds);
    	startNode = goalNode;
    	goalNode = nearbyNode;
    	if(startNode != null){
    		startNode.setDrawColor(Color.BLUE);
        	goalNode.setDrawColor(Color.YELLOW);
        	aStarSearch(startNode, goalNode);
    	}
    	
    }
    
    /**
     * highlights the road the user was searching for
     * also outputs road name suggestions (auto complete) as the user types
     */
    public void onSearch(){
    	resetColor();
    	ArrayList<String> matchingNames = new ArrayList<String>();
    	String[] topTen = new String[10]; //array for up to 10 road name suggestions displayed
    	String entry = getSearchBox().getText();
    	String matches = "";
   
    	if(trie.get(entry) == null){ //if no exact match, search for suggestions
    		matchingNames = trie.getAll(entry);
    		if(matchingNames.size() >= topTen.length){
	    		for(int i = 0; i < topTen.length; i++){
	    			topTen[i] = matchingNames.get(i);
	        	}
    		}else if(matchingNames.size() < topTen.length){
    			for(int i = 0; i < matchingNames.size(); i++){
    				topTen[i] = matchingNames.get(i);
    			}
    		}
    		for(String name : topTen){
    			if(name != null){
    				matches = name + "\n" + matches;
    				for(Integer id : roadNames.get(name)){ //for each road ID number in road names map
    					ArrayList<Segment> roadSeg = roads.get(id).getSegments();
    					for(Segment seg : roadSeg){
    						seg.setDrawColor(Color.RED);
    					}    				
    				}
    			}
    		}
    		getTextOutputArea().setText("Matching road names: " + matches);
    	}
    	
    	if(trie.get(entry) != null){
    		for(Integer id : roadNames.get(entry)){ //for each road ID number in road names map
				ArrayList<Segment> roadSeg = roads.get(id).getSegments();
				for(Segment seg : roadSeg){
					seg.setDrawColor(Color.RED);
				}
			}
    		getTextOutputArea().setText(trie.get(entry));
    	}
    	
    }

    @Override
    public void onMove(Move m){
        switch(m){
            case NORTH: //pan up
            origin = origin.moveBy(0, 2);
            break;
            case SOUTH: //pan down
            origin = origin.moveBy(0, -2);
            break;
            case EAST: //pan right
            origin = origin.moveBy(2, 0);
            break;
            case WEST: //pan left
            origin = origin.moveBy(-2, 0);
            break;
            case ZOOM_IN:
            zoomIn();
            scale = scale * zoomFactor;
            break;
            case ZOOM_OUT:
            zoomOut();
            scale = scale * (1/zoomFactor);
            break;
        }

    }
    
    /**
     * zoom into map
     */
    public void zoomIn(){
    	int[][] vertices = {{0,0}, {0, (int)height}, {(int)width, 0}, {(int)width, (int)height}};
    	Location[] loc = new Location[4];
    	for(int i = 0; i < 4; i++){
    		Point p = new Point(vertices[i][0], vertices[i][1]);
    		loc[i] = Location.newFromPoint(p, origin, scale);
    	}
    	double locWidth = loc[2].x - loc[0].x;
    	double locHeight = loc[1].y - loc[0].y;
    	double dx = (locWidth - locWidth*(1/zoomFactor))/2;
    	double dy = (locHeight - locHeight *(1/zoomFactor))/2;
    	origin = origin.moveBy(dx, dy);
    }
    
    /**
     * zoom out of map
     */
    public void zoomOut(){
    	int[][] vertices = {{0,0}, {0, (int)height}, {(int)width, 0}, {(int)width, (int)height}};
    	Location[] loc = new Location[4];
    	for(int i = 0; i < 4; i++){
    		Point p = new Point(vertices[i][0], vertices[i][1]);
    		loc[i] = Location.newFromPoint(p, origin, scale);
    	}
    	double locWidth = loc[2].x - loc[0].x;
    	double locHeight = loc[1].y - loc[0].y;
    	double dx = (locWidth - locWidth*(zoomFactor))/2;
    	double dy = (locHeight - locHeight *(zoomFactor))/2;
    	origin = origin.moveBy(dx, dy);
    }
    
    
  /**
   * Uses A* search to find the shortest path between two nodes (start node and end node)
   * @param startNode
   * @param goalNode
   */
  public void aStarSearch(Node startNode, Node goalNode){
	if(startNode.equals(goalNode)){
		getTextOutputArea().setText("Start node is goal node");
		return;
	}
	ArrayList<Node> pathNodes = new ArrayList<Node>(); //nodes in the path
	ArrayList<Segment> pathSeg = new ArrayList<Segment>(); //segments in the path
	double costFromStart; //cost to node from the start node
	double estimateTotal = 0; //estimate from node to goal node
	double toNeighbour; //cost from start node to a node's neighbour
	
	Comparator<AStarSearchNode> comparator = new NodeComparator();
	fringe = new PriorityQueue<AStarSearchNode>(10, comparator);
	
	double bestToGoal = Double.POSITIVE_INFINITY;
	for(Node n : nodes.values()){
		n.costFromStart = Double.POSITIVE_INFINITY;
	}
	
	fringe.add(new AStarSearchNode(startNode, null, 0, estimate(startNode, goalNode)));
	while(!(fringe.isEmpty())){
		AStarSearchNode asNode = fringe.poll();
		Node current = asNode.getThis();
		Node parent = asNode.getParent();
		costFromStart = asNode.costFromStart;


		if(costFromStart < current.costFromStart){
			current.parentNode = parent;
			current.costFromStart = costFromStart;
			if(current.equals(goalNode)){ //goal is reached
				String route = "";
				double totalLength = 0;
				//building the path from the start node to goal node
				pathNodes.add(current);
				pathSeg.add(current.neighbours.get(current.parentNode));
				while(current.parentNode != null){
					pathNodes.add(current.parentNode);
					pathSeg.add(current.neighbours.get(current.parentNode));
					current = current.parentNode;
				}
				//highlighting the path on the map
				for(Node n : pathNodes){
					n.setDrawColor(Color.RED);
				}
				Collections.reverse(pathSeg);
				for(Segment s : pathSeg){
					totalLength = totalLength + s.length;
					s.setDrawColor(Color.RED);
					route = route + "\n" + roads.get(s.roadID).getName() + ": " + String.format("%.3f", s.length) + "km";
				}
				route = route + "\n Total distance: " + String.format("%.3f", bestToGoal) + "km";
				getTextOutputArea().setText(route); //display the segments along the route, segment lengths, and total distance
				return;
			}
			for(Segment s : current.segments){ //calculating heuristics and adding neighbouring nodes to the fringe
				Node neighbour = current.nodeNeighbours.get(s); //find neighbouring node using the mutual segment
				toNeighbour = costFromStart + s.length;
				if(toNeighbour < neighbour.costFromStart){
					estimateTotal = toNeighbour + estimate(neighbour, goalNode);
					if(estimateTotal < bestToGoal){
						fringe.add(new AStarSearchNode(neighbour, current, toNeighbour, estimateTotal));
					}
					if(neighbour.equals(goalNode)){
						bestToGoal = toNeighbour;
					}
				}
			}
		}
	}
}
    
  /**
   * Uses an iterative algorithm to find the articulation points in the graph
   */
    public void showArtPts(){
    	ArrayList<Node> artPts = new ArrayList<Node>(); //list of the articulation points in the graph
    	Stack<ArtPtNode> stack = new Stack<ArtPtNode>();
    	Node start = null;
    	Node current;
    	Node parent;
    	int count;
    	for(Node n : nodes.values()){
    		n.count = Integer.MAX_VALUE;
    		start = n;
    	}
    	ArtPtNode pt = new ArtPtNode(start, 0, null);
    	stack.push(pt);
    	while(!(stack.isEmpty())){
    		ArtPtNode a = stack.peek();
    		current = a.thisNode;
    		parent = a.parentNode;
    		count = a.count;
    		if(current.count == Integer.MAX_VALUE){
    			current.count = a.count;
    			current.reachBack = a.count;
    			current.children = new ArrayDeque<Node>();
    			for(Node n : current.neighbours.keySet()){
    				if(!(n.equals(parent))){
    					current.children.add(n);
    				}
    			}
    		}else if(!(current.children.isEmpty())){
    			Node child = current.children.poll();
    			if(child.count < Integer.MAX_VALUE){
    				current.reachBack = Math.min(current.reachBack, child.count);
    			}else{
    				ArtPtNode aPt = new ArtPtNode(child, count+1, current);
    				stack.push(aPt);
    			}
    		}else{
    			if(!(current.equals(start))){
    				if(current.reachBack >= parent.count){
    					artPts.add(parent);
    				}
    				parent.reachBack = Math.min(parent.reachBack, current.reachBack);
    			}
    			stack.pop();
    		}
    	}
    	for(Node n : artPts){
    		if(n != null){
    			n.setDrawColor(Color.RED);
    		}
    	}
    }
    
    public double estimate(Node start, Node goal){
    	return start.location.distance(goal.location);
    }

    @SuppressWarnings("unused")
	public static void main(String[] args){
        AMap a = new AMap();
    }
}

