package OCOH;

import java.util.ArrayList;
import java.util.List;

import anja.geom.Rectangle2;
import anja.geom.Segment2;


public class Rect {

	public Point p;
	public double width, height;

	Point leftUpperVertex;
	Point rightUpperVertex;
	Point leftLowerVertex;
	Point rightLowerVertex;
	
	public Rect(Point p, double width, double height){

		this.p = p;
		this.width = width;
		this.height = height;
		leftUpperVertex = p;
		rightUpperVertex = new Point(p.posX + width, p.posY);
		leftLowerVertex = new Point(p.posX, p.posY + height);
		rightLowerVertex = new Point(p.posX + width, p.posY + height);
		
	}
	
	public Rect(Point upLeft, Point upRight, Point downLeft, Point downRight){
		
		this.width = Math.abs(upLeft.posX - upRight.posX);
		this.height = Math.abs(upLeft.posY - downLeft.posY);
		leftUpperVertex = upLeft;
		rightUpperVertex = upRight;
		leftLowerVertex = downLeft;
		rightLowerVertex = downRight;
		p = leftUpperVertex;
		
	}
	
	public Rect(Point midPoint, double radius){
		// square or L-infty ball
		p = new Point(midPoint.posX - radius, midPoint.posY - radius);
		width = 2 * radius;
		height = 2 * radius;
	}
	

	
	public double getArea(){
		
		return width * height;
		
	}
	
	public Point getMidPoint(){
		
		Point midPoint = new Point(p.posX + (0.5 * width), p.posY + (0.5 * height));
		
		return midPoint;
		
	}
	
	public void move(double inXdir, double inYdir){
		
		p.posX += inXdir;
		p.posY += inYdir;
		
	}	
	
	public boolean contains(Point q){
		
		// slight numerical mistake erased by rounding: needed for contains method in OCOHAlgorithm class
		
		if(Math.round(q.posX) >= Math.round(leftUpperVertex.posX) 
				&& Math.round(q.posX) <= Math.round(rightUpperVertex.posX) 
				&& Math.round(q.posY) >= Math.round(leftUpperVertex.posY)
				&& Math.round(q.posY) <= Math.round(leftLowerVertex.posY))
			return true;
		else return false;
	}
	
	public String toString(){
		
		return "P1: (" + p.posX + "," + p.posY + ") - P2: (" + p.posX+width + "," + p.posY + ") - P3: (" + p.posX + "," + p.posY+height + ") - P4: (" + p.posX+width + "," + p.posY+height + ")";
		
	}
	
	public Point[] minDistPointsTo(Rect rect){
		
		Rectangle2 rect1 = new Rectangle2((float)leftUpperVertex.posX, (float)leftUpperVertex.posY, (float) width, (float) height);
		Rectangle2 rect2 = new Rectangle2((float)rect.leftUpperVertex.posX, (float)rect.leftUpperVertex.posY, (float) rect.width, (float) rect.height);
		
		Point[] minDistPoints = new Point[2];
		double minDist = Double.POSITIVE_INFINITY;
		
		Segment2 s1 = rect1.top();
		Segment2 s2 = rect1.bottom();
		Segment2 s3 = rect1.left();
		Segment2 s4 = rect1.right();
		
		LineSegment l1 = new LineSegment(new Point(s1.source().getX(),s1.source().getY()),
				new Point(s1.target().getX(),s1.target().getY()));
		LineSegment l2 = new LineSegment(new Point(s2.source().getX(),s2.source().getY()),
				new Point(s2.target().getX(),s2.target().getY()));
		LineSegment l3 = new LineSegment(new Point(s3.source().getX(),s3.source().getY()),
				new Point(s3.target().getX(),s3.target().getY()));
		LineSegment l4 = new LineSegment(new Point(s4.source().getX(),s4.source().getY()),
				new Point(s4.target().getX(),s4.target().getY()));
		
		List<Point[]> points = new ArrayList<Point[]>();
		points.add(l1.minDistPointsTo(rect2));
		points.add(l2.minDistPointsTo(rect2));
		points.add(l3.minDistPointsTo(rect2));
		points.add(l4.minDistPointsTo(rect2));
		
		for (int i = 0; i < points.size(); i++){
			if (Math.sqrt(points.get(i)[0].distanceSquaredTo(points.get(i)[1])) < minDist) {
				minDist = Math.sqrt(points.get(i)[0].distanceSquaredTo(points.get(i)[1]));
				minDistPoints = points.get(i);
			}
		}
		
		return minDistPoints;
	}
	

	public boolean contains(Rect rect){

		// return true, if rect2 is contained in rect1

		Rectangle2 rect1 = new Rectangle2((float)leftUpperVertex.posX, (float)leftUpperVertex.posY, (float) width, (float) height);
		Rectangle2 rect2 = new Rectangle2((float)rect.leftUpperVertex.posX, (float)rect.leftUpperVertex.posY, (float) rect.width, (float) rect.height);
		
		if (rect1.contains(rect2.getX(), rect2.getY(), rect2.getWidth(), rect2.getHeight())){
			return true;
		} else return false;
	}
	
}
