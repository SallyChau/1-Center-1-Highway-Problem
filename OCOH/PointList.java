package OCOH;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PointList {

	private Color pointColor;
	List<Point> points;

	public PointList(Color pointColor, List<Point> points) {
		this.points = points;
		this.pointColor = pointColor;
		setPointsColor(pointColor);
	}
	

	public PointList(List<Point> points){
		this(Color.BLACK, points);
	}

	public PointList(Color pointColor) {
		this(pointColor, new ArrayList());
	}
	
	public PointList(){
		this(Color.RED);
	}

	public void setList(List<Point> points){
		this.points = points;
	}
	
	public void setPointsColor(Color color){
		
		for(Point p: points){
			p.setColor(color);
		}
	}
	
	public boolean collisionExists(Point p){
		
		for (Point point : points) {
			if (point.collide(p)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addPoint(Point p) {

		p.setColor(pointColor);
		points.add(p);

	}

	public void setPointList() {

	}

	public boolean enoughPoints() {
		
		if(points.size()>=2){
			return true;
		}
		
		return false;
	}
	
	public void remove(Point p){
		points.remove(p);
	}
	
	public void clear(){
		points.clear();
	}
	
	public List<Point> getPoints(){
		return points;
	}
	
	public void draw(Graphics g){
			
		for(Point p: points){
			p.draw(g);
			
		}
	}
	
	public int getSize(){
		return points.size();
	}
	
	public boolean contains(Point p){
		return points.contains(p);
	}
	
	public Point search(Point p){
		
		for(Point point: points){
			if(point.equals(p)){
				
				return point; 
			}
		}
		
		return null;
	}
	
	public void removeAll(Point p){
		
		for (Iterator<Point> iterator = points.iterator(); iterator.hasNext();) {
		    Point point = iterator.next();
		    if (point.equals(p)) {
		        // Remove the current element from the iterator and the list.
		        iterator.remove();
		    }
		}
	}
	
	public boolean equals(Object o){
		
		if(o instanceof PointList){
			
			PointList pointsList = (PointList)o;
			
			for(Point p: pointsList.getPoints()){
				
				if(!points.contains(p)){
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	public int hasCode(){
		return points.size();
	}

	public String toString(){
		return points.toString();
	} 
	
	public Point[] getExtremePoints(){
		
		//extremePoints contains the points with highest and lowest x and y coordinate of the PointList
		
		Point[] extremePoints = new Point[4];
		
		//extreme points in X direction
		Collections.sort(points, Point.COMPARE_BY_XCoord);
		extremePoints[0] = points.get(0); // smallest X coordinate
		extremePoints[1] = points.get(getSize()-1); // biggest X coordinate
		
		//extreme points in Y direction
		Collections.sort(points, Point.COMPARE_BY_YCoord);
		extremePoints[2] = points.get(0); // smallest Y coordinate
		extremePoints[3] = points.get(getSize()-1); // biggest Y coordinate
		
		return extremePoints;
	}
	
	public double delta(){
		
		// returns 1/2 of the largest L_infty distance between any 2 points of PointList
		
		double max = Double.NEGATIVE_INFINITY;
		for (Point p : points){
			for (Point q : points){
				if (p.distanceTo(q, Double.POSITIVE_INFINITY) > max){
					max = p.distanceTo(q, Double.POSITIVE_INFINITY);
				}
			}
		}
		
		return (0.5 * max);
	}
	
	public boolean isEmpty(){
		
		int size = getSize();
		if (size > 0) return false;
		else return true;
		
	}
	
	
}
