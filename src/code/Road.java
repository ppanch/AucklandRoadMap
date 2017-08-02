package code;
import java.util.*;

/**
 * Class representing a Road object and its data
 * @author Petra P
 *
 */
public class Road {
	public ArrayList<Segment> segments = new ArrayList<Segment>(); //segments connected to this road
    public ArrayList<Node> nodes = new ArrayList<Node>(); //nodes this road is connected to
    public static int id;
    public String label;
    public String city;
    public int oneway;
    public int speed;
    public int roadClass;
    public int notForCar;
    public int notForPed;
    public int notForBike;
    
     /**
     * Constructor for objects of class Road
     */
    public Road(int idNum, String label, String city, int oneway, int speed, int roadClass, int notCar, int notPed, int notBike)
    {
        id = idNum;
        this.label = label;
        this.city = city;
        this.oneway = oneway;
        this.speed = speed;
        this.roadClass = roadClass;
        this.notForCar = notCar;
        this.notForPed = notPed;
        this.notForBike = notBike;
    }
    
    public void addSegment(Segment s){
        segments.add(s);
    }
    
    public ArrayList<Segment> getSegments(){
    	return segments;
    }
    
    public void addNode(Node n){
        nodes.add(n);
    }
    
    public String getName(){
    	return label + ", " + city;
    }
}
