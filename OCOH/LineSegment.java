package OCOH;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import anja.geom.Intersection;
import anja.geom.Line2;
import anja.geom.Point2;
import anja.geom.Rectangle2;
import anja.geom.Segment2;

/**
 * 
 * @author Sally Chau
 *
 */
public class LineSegment extends Segment2{
	
	private static final long serialVersionUID = 1L;
	
	//**************************************************************************
	// Variables
	//**************************************************************************
		
	public Point start;
	public Point end;
	
	//**************************************************************************
	// Constructor
	//**************************************************************************

	/**
	 * 
	 * @param p1
	 * 		One endpoint of the line segment.
	 * @param p2
	 * 		Second endpoint of the line segment.
	 */
	public LineSegment(Point p1, Point p2){
		
		start = p1;
		end = p2;
		correctLine();
		
	}
	
	/**
	 * Changes the LineSegment object endpoints such that the startpoint 
	 * is left to or above the endpoint: 
	 */
	private void correctLine(){
		// creates LineSegment where p1 < p2 by comparing coordinates
		if (isVertical()){
			if (start.posY > end.posY) swapEndPoints();
		}
		if (isHorizontal()){
			if (start.posX > end.posX) swapEndPoints();
		}
	}
	
	/**
	 * Swaps start and end points.
	 */
	private void swapEndPoints(){
		Point a = new Point();
		a = start;
		start = end;
		end = a;
	}
	
	//**************************************************************************
	// Boolean
	//**************************************************************************

	/**
	 * Returns true if the line segment is vertical.
	 */
	public boolean isVertical(){
		if (start.posX == end.posX) return true;
		else return false;
	}
	
	/**
	 * Returns true if the line segment is horizontal.
	 */
	public boolean isHorizontal(){
		if (start.posY == end.posY) return true;
		else return false;
	}
	
	public boolean equals(Object obj){
		if (obj instanceof LineSegment) {
			LineSegment l = (LineSegment) obj;
			if (l.start.equals(start) && l.end.equals(end)){
				return true;
			}
		}

		return false;
	}	
	
	/**
	 * 
	 * @param p
	 * @return
	 * 		true, if point p lies on the line segment.
	 */
	public boolean contains(Point2 p){
		
		return liesOn(p);
		
	}
	
	/**
	 * Computes whether two segments in the plane intersect.
	 * @param l
	 * @return
	 * 		true, if the line segment intersects with l
	 */
	public boolean intersectsWith(LineSegment l){
		
		double dx1 = end.posX - start.posX;
		double dy1 = end.posY - start.posY;
		double dx2 = l.end.posX - l.start.posX;
		double dy2 = l.end.posY - l.start.posY;
		
		double delta = dx2 * dy1 - dy2 * dx1;
		if (delta == 0) return false; // parallel segments
		double s = (dx1 * (l.start.posY - start.posY) + dy1 * (start.posX - l.start.posX)) / delta;
		double t = (dx2 * (start.posY - l.start.posY) + dy2 * (l.start.posX - start.posX)) / (-delta);
		return (0 <= s) && (s <= 1) && (0 <= t) && (t <= 1);
		
	}
	
	
	//**************************************************************************
	// Getter
	//**************************************************************************
	
	/**
	 * 
	 * @return the squared Euclidean Length of the LineSegment object
	 */
	public double getSquaredLength(){
		
		return start.distanceSquaredTo(end);
	
	}
	
	/**
	 * 
	 * @return the infinity length of the LineSegment object
	 */
	public double getInftyLength(){
		
		return start.inftyDistanceTo(end);
	
	}
	
	/**
	 * 
	 * @return
	 * 		middle point p on line segment where the distance between p and the
	 * 		startpoint is equal to the distance between p and the endpoint.
	 */
	public Point getMidPoint(){
		Point p;
		double l = Math.sqrt(getSquaredLength());
		double[] v = start.getDirectionVectorTo(end);
		
		p = new Point(start.posX + l * v[0], start.posY + l * v[1]);
		
		return p;
	}
	
	//**************************************************************************
	// 
	//**************************************************************************
		
	public String toString(){
		
		return "Startpoint: " + start.toString() + " Endpoint: " + end.toString();
		
	}
	
	/**
	 * Draws a black line starting from start point and finishing at end point.
	 * @param g
	 */
	public void draw(Graphics g){
		g.setColor(Color.BLACK);
		g.drawLine((int)start.posX, (int)start.posY, (int)end.posX, (int)end.posY);
		
	}
	
	//**************************************************************************
	// Compute Distance Methods
	//**************************************************************************
	
	/**
	 * 
	 * @param p
	 * 		Point 
	 * @return
	 * 		Euclidean distance between a line and a point;
	 * 		if the point lies on the line segment, the distance is 0.
	 */
	public double distTo(Point p){
		// uses Segment2
		
		Point2 p2 = new Point2(p.posX, p.posY);
		Point2 sStart = new Point2(this.start.posX, this.start.posY);
		Point2 sEnd = new Point2(this.end.posX, this.end.posY);
		Segment2 seg = new Segment2(sStart, sEnd);
		
		return seg.distance(p2);
		
	}
	
	/**
	 * 
	 * @param p
	 * 		Point
	 * @return
	 * 		2 points in an array which span the minimum distance between the point p
	 * 		and the line segment
	 */
	public Point[] minDistPointsTo(Point p){
		Point2 p2 = new Point2(p.posX, p.posY);
		Point2 sStart = new Point2(this.start.posX, this.start.posY);
		Point2 sEnd = new Point2(this.end.posX, this.end.posY);
		Segment2 seg = new Segment2(sStart, sEnd);
		
		Point2 q = seg.closestPoint(p2);
		Point[] minDistPoints = new Point[2];
		minDistPoints[0] = new Point(q.getX(), q.getY());
		minDistPoints[1] = p;
		
		return minDistPoints;
	}
	
	/**
	 * 
	 * @param l
	 * 		Line Segment
	 * @return
	 * 		smallest distance between two line segments;
	 * 		if they intersect the distance is 0.
	 */
	public Double distanceTo(LineSegment l){
		// distance between 2 segments in the plane
		
		List<Double> distances = new ArrayList<Double>();
		double minDist = Double.POSITIVE_INFINITY;
	
		distances.add(l.distTo(start));
		distances.add(l.distTo(end));
		distances.add(distTo(l.start));
		distances.add(distTo(l.end));
		
		for (double dist : distances){
			if (dist < minDist) minDist = dist;
		}
		
		return minDist;
	}
	
	/**
	 * 
	 * @param l
	 * 		Line Segment
	 * @return
	 * 		2 points in an array which span the minimum distance between two 
	 * 		line segments;
	 * 		if they intersect the 2 points are equal
	 */
	public Point[] minDistPointsTo(LineSegment l){
		// distance between 2 segments in the plane
		
		double minDist = Double.POSITIVE_INFINITY;
		
		Point[] minDistPoints = new Point[2]; // points which span min distance
		
		if (intersectsWith(l)) {
			Line2 line1 = new Line2(new Point2(this.start.posX, this.start.posY), new Point2(this.end.posX, this.end.posY));
			Line2 line2 = new Line2(new Point2(l.start.posX, l.start.posY), new Point2(l.end.posX, l.end.posY));
			
			Intersection inter = new Intersection();
			line1.intersection(line2, inter);
			
			Point2 interPoint = inter.getPoint();
			minDistPoints[0] = new Point(interPoint.getX(), interPoint.getY());
			minDistPoints[1] = new Point(interPoint.getX(), interPoint.getY());
			
			return minDistPoints;
		}
		
		List<Point[]> points = new ArrayList<Point[]>();
		points.add(this.minDistPointsTo(l.start));
		points.add(this.minDistPointsTo(l.end));
		points.add(l.minDistPointsTo(start));
		points.add(l.minDistPointsTo(end));
		
		for (int i = 0; i < points.size(); i++){
			if (Math.sqrt(points.get(i)[0].distanceSquaredTo(points.get(i)[1])) < minDist){
				minDist = Math.sqrt(points.get(i)[0].distanceSquaredTo(points.get(i)[1]));
				minDistPoints = points.get(i);
			}
		}
		return minDistPoints;
	}
	
	/**
	 * 
	 * @param rect
	 * 		Rectangle
	 * @return
	 * 		smallest distance between a line segment and a rectangle;
	 * 		if they intersect the distance is 0.
	 * 		
	 */
	public double distanceTo(Rectangle2 rect){
		double dist = Double.POSITIVE_INFINITY;
		
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
		distances.add(this.distanceTo(l1));
		distances.add(this.distanceTo(l2));
		distances.add(this.distanceTo(l3));
		distances.add(this.distanceTo(l4));
		
		for (double d : distances){
			if (d < dist) dist = d;
		}
		
		return dist;
		
	}

	/**
	 * 
	 * @param rect
	 * 		Rectangle
	 * @return
	 * 		2 points in an array which span the minimum distance between the rectangle
	 * 		and the line segment;
	 * 		if they intersect the 2 points are equal
	 */
	public Point[] minDistPointsTo(Rectangle2 rect){
		
		Point[] minDistPoints = new Point[2];
		double dist = Double.POSITIVE_INFINITY;
		
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
		
		List<Point[]> points = new ArrayList<Point[]>();
		points.add(this.minDistPointsTo(l1));
		points.add(this.minDistPointsTo(l2));
		points.add(this.minDistPointsTo(l3));
		points.add(this.minDistPointsTo(l4));
		
		for (int i = 0; i < points.size(); i++){
			if (Math.sqrt(points.get(i)[0].distanceSquaredTo(points.get(i)[1])) < dist) {
				dist = Math.sqrt(points.get(i)[0].distanceSquaredTo(points.get(i)[1]));
				minDistPoints = points.get(i);
			}
		}
		
		return minDistPoints;
	}
		
}
