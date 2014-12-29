package OCOH;

public class LineSegment {

	public Point start;
	public Point end;
	
	public LineSegment(Point p1, Point p2){
		
		start = p1;
		end = p2;
		
	}
	
	public double getSquaredLength(){
		return start.distanceSquaredTo(end);
	}
	
	public double getInftyLength(){
		return start.distanceTo(end, Double.POSITIVE_INFINITY);
	}
	
	
}
