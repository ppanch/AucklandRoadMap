package code;
import java.util.*;
import java.awt.*;

/**
 * Class representing a segment object and its data
 * @author Petra P
 *
 */
public class Segment {
	public int roadID;
    public double length;
    public int nodeID1;
    public int nodeID2;
    public ArrayList<Double[]> coords = new ArrayList<Double[]>(); //coordinates from nodes to draw the segment
    
    public Color color = Color.BLACK;
    
    /**
     * Constructor for objects of class Segment
     */
    public Segment(String line){
        String[] info = line.split("\t");
        roadID = Integer.parseInt(info[0]);
        length = Double.parseDouble(info[1]);
        nodeID1 = Integer.parseInt(info[2]);
        nodeID2 = Integer.parseInt(info[3]);
        for(int i = 4; i < info.length; i+=2){
            coords.add(new Double[] {Double.parseDouble(info[i]), Double.parseDouble(info[i+1])});
        }
    }
    
    public int getRoadID(){
    	return roadID;
    }
    
    public void setDrawColor(Color c){
    	color = c;
    }
    
    public void draw(Graphics g, Location origin, double scale, Node n1, Node n2){
    	g.setColor(color);
        g.drawLine(n1.pt.x, n1.pt.y, n2.pt.x, n2.pt.y);
    }
}
