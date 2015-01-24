package OCOH;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import anja.geom.Intersection;
import anja.geom.Line2;
import anja.geom.Point2;
import anja.geom.Rectangle2;
import anja.geom.Segment2;

public class LineSegment {

	public Point start;
	public Point end;
	
	public LineSegment(Point p1, Point p2){
		
		start = p1;
		end = p2;
		correctLine();
		
	}
	
	private void correctLine(){
		// creates LineSegment where p1 < p2 by comparing coordinates
		if (isVertical()){
			if (start.posY > end.posY) swapEndPoints();
		}
		if (isHorizontal()){
			if (start.posX > end.posX) swapEndPoints();
		}
	}
	
	private void swapEndPoints(){
		Point a = new Point();
		a = start;
		start = end;
		end = a;
	}
	
	public double getSquaredLength(){
		return start.distanceSquaredTo(end);
	}
	
	public double getInftyLength(){
		return start.inftyDistanceTo(end);
	}
	
	public boolean isVertical(){
		if (start.posX == end.posX) return true;
		else return false;
	}
	
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
	
	public String toString(){
		
		return "Startpoint: " + start.toString() + " Endpoint: " + end.toString();
		
	}
	
	public void draw(Graphics g){
		g.setColor(Color.BLACK);
		g.drawLine((int)start.posX, (int)start.posY, (int)end.posX, (int)end.posY);
		
	}
	
	public Point midPoint(){
		Point p;
		double l = Math.sqrt(getSquaredLength());
		double[] v = start.getDirectionVectorTo(end);
		
		p = new Point(start.posX + l * v[0], start.posY + l * v[1]);
		
		return p;
	}
	
	public boolean contains(Point p){
		if (p.equals(start) || p.equals(end)) return true;
		if (isVertical()){
			if (p.posX == start.posX){
				if ((p.posY < start.posY && p.posY > end.posY) || (p.posY > start.posY && p.posY < end.posY)){
					return true;
				} 
			} 
			return false;
		} else if (isHorizontal()){
			if (p.posY == start.posY){
				if ((p.posX < start.posX && p.posX > end.posX) || (p.posX > start.posX && p.posX < end.posX)){
					return true;
				} 
			} 
			return false;
		}
		return false;
	}
	
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

	
	public Point[] minDistPointsTo(LineSegment l){
		// distance between 2 segments in the plane
		
		List<Double> distances = new ArrayList<Double>();
		double minDist = Double.POSITIVE_INFINITY;
		int minIndex;
		
		Point[] segmentPoints = new Point[] {start, end, l.start, l.end};
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
		distances.add(l.distanceTo(start));
		distances.add(l.distanceTo(end));
		distances.add(distanceTo(l.start));
		distances.add(distanceTo(l.end));
		
		//find points which span min distance
		for (double dist : distances){
			if (dist < minDist) minDist = dist;
		}
		minIndex = distances.indexOf(minDist);
		minDistPoints[0] = segmentPoints[minIndex];
		
		List<Double> dist = new ArrayList<Double>();
		
		// min dist point lies either on orthogonal line on 2nd segment or is end points of 2nd line segment
		// find second minDist point
		
		if (minIndex < 2){
			
			// First point lies on Segment 1 -> 2nd Point on Segment 2
			
			dist.add(Math.sqrt(minDistPoints[0].distanceSquaredTo(l.start)));
			dist.add(Math.sqrt(minDistPoints[0].distanceSquaredTo(l.end)));
			dist.add(l.distanceTo(minDistPoints[0]));
			if (dist.indexOf(Collections.min(dist)) == 0) minDistPoints[1] = l.start;
			else if (dist.indexOf(Collections.min(dist)) == 1) minDistPoints[1] = l.end;
			else {
				// find point on 2nd segment
				String pos = l.relativePosTo(minDistPoints[0]);
				if (pos == "right" || pos == "left") minDistPoints[1] = new Point(minDistPoints[0].posY, l.start.posX);
				else if (pos == "up" || pos == "down") minDistPoints[1] = new Point(minDistPoints[0].posX, l.start.posY);	
			}
		} else {
			
			// First point lies on Segment 2 -> 2nd Point on Segment 1
			
			dist.add(Math.sqrt(minDistPoints[0].distanceSquaredTo(start)));
			dist.add(Math.sqrt(minDistPoints[0].distanceSquaredTo(end)));
			dist.add(distanceTo(minDistPoints[0]));
			if (dist.indexOf(Collections.min(dist)) == 0) minDistPoints[1] = start;
			else if (dist.indexOf(Collections.min(dist)) == 1) minDistPoints[1] = end;
			else {
				// find point on 2nd segment
				String pos = relativePosTo(minDistPoints[0]);
				if (pos == "right" || pos == "left") minDistPoints[1] = new Point(minDistPoints[0].posY, start.posX);
				else if (pos == "up" || pos == "down") minDistPoints[1] = new Point(minDistPoints[0].posX, start.posY);	
			}
		}
		
		
		return minDistPoints;
	}
	
	public Double distanceTo(LineSegment l){
		// distance between 2 segments in the plane
		
		List<Double> distances = new ArrayList<Double>();
		double minDist = Double.POSITIVE_INFINITY;
		
		if (intersectsWith(l)) return 0.0;
		distances.add(l.distanceTo(start));
		distances.add(l.distanceTo(end));
		distances.add(distanceTo(l.start));
		distances.add(distanceTo(l.end));
		
		for (double dist : distances){
			if (dist < minDist) minDist = dist;
		}
		
		return minDist;
	}
	
	public String relativePosTo(Point p){
		// find relative position between a point p and a line segment from q1 to q2
		// if minDist from p to segment is NOT build by segment endpoints
		
		if (start.posX < p.posX && end.posX < p.posX && 
				!((start.posY < p.posY && end.posY < p.posY)||(start.posY > p.posY && end.posY > p.posY))){
			// point is right from segment
			return "right";
		}
		if (start.posX > p.posX && end.posX > p.posX &&
				!((start.posY < p.posY && end.posY < p.posY)||(start.posY > p.posY && end.posY > p.posY))){
			// point is left from segment
			return "left";
		}
		if (start.posY < p.posY && end.posY < p.posY &&
				!((start.posX < p.posX && end.posX < p.posX)||(start.posX > p.posX && end.posX > p.posX))){
			// point is above segment
			return "up";
		}
		if (start.posY > p.posY && end.posY > p.posY &&
				!((start.posX < p.posX && end.posX < p.posX)||(start.posX > p.posX && end.posX > p.posX))){
			// point is lower than segment
			return "down";
		}
		else return null;
		
	}
	
	public boolean intersectsWith(LineSegment l){
		// whether two segments in the plane intersect
		// one segment is p1 to p2, the other is q1 to q2
		
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
	
	public double distanceTo(Point p){
		// distance between point p and segment (q1,q2)
		
		double dx = end.posX - start.posX;
		double dy = end.posY - start.posY;
		Point point = new Point(p.posX - start.posX, p.posY - start.posY);
		Point origin = new Point(0, 0);
		
		if ((dx == dy) && (dy == 0)){
			// segment is just a point
			return Math.sqrt(origin.distanceSquaredTo(point));
		}
		
		// calculate t that minimizes the distance
		double t = ((p.posX - start.posX) * dx + (p.posY - start.posY) * dy)/(dx * dx + dy * dy);
		
		// See if this represents one of the segment's end points or a point in the middle.
		if (t < 0){
		    dx = p.posX - start.posX;
		    dy = p.posY - start.posY;
		} else if(t > 1){
		    dx = p.posX - end.posX;
		    dy = p.posY - end.posY;
		} else {
		    double near_x = start.posX + t * dx;
		    double near_y = start.posY + t * dy;
		    dx = p.posX - near_x;
		    dy = p.posY - near_y;
		}
		
		return Math.sqrt(new Point(dx, dy).distanceSquaredTo(origin));
		
	}
	
	
	
	
}
