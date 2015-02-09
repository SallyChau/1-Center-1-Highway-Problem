package OCOH;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import anja.geom.Rectangle2;
import anja.geom.Segment2;

/**
 * 
 * @author Ramtin Azimi
 *
 */

public class Point {

	//**************************************************************************
	// Variables
	//**************************************************************************
		
	double posX;
	double posY;
	Color color;
	public static final int RADIUS = 4;

	//**************************************************************************
	// Constructors
	//**************************************************************************
	
	/**
	 * 
	 * @param posX
	 * 		x-coordinate of Point object
	 * @param posY
	 * 		y-coordiante of Point object
	 * @param color
	 * 		color of Point object if it is drawn
	 */
	public Point(double posX, double posY, Color color) {
		this.posX = posX;
		this.posY = posY;
		this.color = color;
	}

	/**
	 * Creates a Point object with at the origin and default color black.
	 */
	public Point(){
		this(0,0);
	}

	/**
	 * 
	 * @param posX
	 * 		x-coordinate of Point object
	 * @param posY
	 * 		y-coordinate of Point object
	 */
	public Point(double posX, double posY) {
		this(posX, posY, Color.BLACK);
	}

	//**************************************************************************
	// Setter
	//**************************************************************************
	
	/**
	 * Moves the Point object to new x-position.
	 * @param posX
	 * 		new x-coordinate of Point object
	 */
	public void setPosX(double posX) {
		this.posX = posX;
	}
	
	/**
	 * Moves the Point object to new y-position.
	 * @param posY
	 * 		new y-coordinate of Point object
	 */
	public void setPosY(double posY) {
		this.posY = posY;
	}

	/**
	 * Changes the color of the Point object.
	 * @param color
	 * 		new color of Point object
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	//**************************************************************************
	// Getter
	//**************************************************************************
	
	/**
	 * 
	 * @return
	 * 		x-coordinate of Point object
	 */
	public double getX() {
		return posX;
	}

	/**
	 * 
	 * @return
	 * 		y-coordinate of Point object
	 */
	public double getY() {
		return posY;
	}

	/**
	 * 
	 * @return
	 * 		color of Point object
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * 
	 * @return
	 * 		String representation of the Point object; 
	 * 		basically outputs the (X,Y) coordinates of the Point object.
	 */
	public String toString() {
		return "(X,Y) = ("+posX+","+posY+")";
	}

	//**************************************************************************
	// draw() - Methods
	//**************************************************************************
	
	/**
	 * Draws the Point object as a filled circle of radius RADIUS and color color.
	 * @param g
	 */
	public void draw(Graphics g) {

		g.setColor(color);
		g.fillOval((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
			
	}
	
	/**
	 * Draws the Point object as a filled circle of radius RADIUS and color color.
	 * @param g
	 */
	public void draw(Graphics g, Color color){

		g.setColor(color);
		g.fillOval((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
	
	}

	/**
	 * Draws a black highlight around Point object.
	 * @param g
	 */
	public void drawHighlight(Graphics g){
		
		g.setColor(Color.BLACK);
		g.fillRect((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		
		
	}
	
	/**
	 * Draws black bounding around Point object.
	 * @param g
	 */
	public void drawBoundings(Graphics g){
		
		g.setColor(Color.BLACK);
		g.drawRect((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
	
	}
	
	//**************************************************************************
	// Boolean
	//**************************************************************************
	
	/**
	 * 
	 * @param p
	 * @return
	 * 		True, if p lies within the circle of another point in the list points.
	 * 		Else false.
	 */
	public boolean collide(Point p) {
		
		double abs = distanceSquaredTo(p);

		return (abs <= Math.pow(2*RADIUS, 2));

	}
	
	public boolean equals(Object obj) {

		if (obj instanceof Point) {
			Point p = (Point) obj;
			double xDiff = Math.abs(posX - p.getX());
			double yDiff = Math.abs(posY - p.getY());

			if (xDiff < RADIUS && yDiff < RADIUS) {
				return true;
			}
		}

		return false;
	}
	
	//**************************************************************************
	// Distance Methods
	//**************************************************************************
	
	/**
	 * 
	 * @param p
	 * @return
	 * 		distance to the power of 2 between two points
	 */
	public double distanceSquaredTo(Point p){
		
		double xDiff = this.getX() - p.getX();
		double yDiff = this.getY() - p.getY();
		double abs = (Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
		
		return abs;
		
	}

	/**
	 * 
	 * @param p
	 * @return
	 * 		infinity distance to between two points
	 */
	public double inftyDistanceTo(Point p) {
		
		double dist = -1;

		// infinity norm
			if (Math.abs(posX - p.posX) > Math.abs (posY - p.posY)){
				dist = Math.abs(posX - p.posX);
			} else dist = Math.abs(posY - p.posY);
		
		return dist;
	}
	
	/**
	 * 
	 * @param rect
	 * @return
	 * 		Euclidean distance between a point and a rectangle;
	 * 		if the point is contained in the rectangle, the distance is 0.
	 */
	public double distanceTo(Rectangle2 rect){
		
		double dist = Double.POSITIVE_INFINITY;
		
		if (rect.contains(this.posX, this.posY)) {
			return 0;
		}
		
		Segment2 s1 = rect.top();
		Segment2 s2 = rect.bottom();
		Segment2 s3 = rect.left();
		Segment2 s4 = rect.right();
		
		LineSegment l1 = new LineSegment(new Point(s1.source().getX(),s1.source().getY()),
				new Point(s1.target().getX(),s1.target().getY()));
		LineSegment l2 = new LineSegment(new Point(s2.source().getX(),s2.source().getY()),
				new Point(s2.target().getX(),s2.target().getY()));
		LineSegment l3 = new LineSegment(new Point(s3.source().getX(),s3.source().getY()),
				new Point(s3.target().getX(),s3.target().getY()));
		LineSegment l4 = new LineSegment(new Point(s4.source().getX(),s4.source().getY()),
				new Point(s4.target().getX(),s4.target().getY()));
		
		List<Double> distances = new ArrayList<Double>();
		distances.add(l1.distTo(this));
		distances.add(l2.distTo(this));
		distances.add(l3.distTo(this));
		distances.add(l4.distTo(this));
		
		for (double d : distances){
			if (d < dist) dist = d;
		}
		
		return dist;
	}
	
	//**************************************************************************
	// Direction Methods
	//**************************************************************************
	
	/**
	 * Computes the normalized direction vector to a Point p.
	 * @param p
	 * @return
	 * 		array of size 2 contains normalizes direction vector entries
	 */
	public double[] getNormDirectionVectorTo(Point p){
		
		//vector from point to p
		double[] dir = getDirectionVectorTo(p);
		double vectorLength;
		
		vectorLength = Math.sqrt(Math.pow(dir[0], 2.0) + Math.pow(dir[1],2.0));
		
		//normalizing dirVector
		dir[0] = dir[0] / vectorLength;
		dir[1] = dir[1] / vectorLength;
		
		return dir;
	}
	
	/**
	 * Computes the diretion vectore to a Point p.
	 * @param p
	 * @return
	 * 		array of size 2 contains direction vector entries
	 */
	public double[] getDirectionVectorTo(Point p){
		
		//vector from point to p
		double[] dir = new double[2];
		
		dir[0] = (p.posX - posX);
		dir[1] = (p.posY - posY);
		
		
		return dir;
	}

	//**************************************************************************
	// Comparator
	//**************************************************************************
	
	/**
	 * Comparator compares Point objects by increasing y-coordinate.
	 */
	public static Comparator<Point> COMPARE_BY_YCoord = new Comparator<Point>() {
        public int compare(Point first, Point second) {
            return (int)(first.posY-second.posY);
        }
    };
    
    /**
	 * Comparator compares Point objects by increasing x-coordinate.
	 */
    public static Comparator<Point> COMPARE_BY_XCoord = new Comparator<Point>() {
        public int compare(Point first, Point second) {
            return (int)(first.posX-second.posX);
        }
    };
}
