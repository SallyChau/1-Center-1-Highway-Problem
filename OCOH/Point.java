package OCOH;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Comparator;

public class Point{

	double posX;
	
	double posY;
	
	Color color;
	
	public static final int RADIUS = 4;

	public Point(double posX, double posY, Color color) {
		this.posX = posX;
		this.posY = posY;
		this.color = color;
	}

	public Point(){
		this(0,0);
	}
	//DEFAULT_COLOR will be red
	public Point(double posX, double posY) {
		this(posX, posY, Color.RED);
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}
	
	public void setPosY(double posY) {
		this.posY = posY;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getX() {
		return posX;
	}

	public double getY() {
		return posY;
	}

	public Color getColor() {
		return color;
	}
	
	public void draw(Graphics g) {

		g.setColor(color);
		
		g.fillOval((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
			
		
	}
	
	public void draw(Graphics g, Color color){

		g.setColor(color);
		g.fillOval((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
	}

	public void drawHighlight(Graphics g){
		g.setColor(Color.BLACK);
		if(color == Color.RED){
			g.fillOval((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		}else{
			g.fillRect((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		}
	}
	
	public void drawBoundings(Graphics g){
		g.setColor(Color.BLACK);
		if(color == Color.RED){
			g.drawOval((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		}else{
			g.drawRect((int)posX - RADIUS, (int)posY - RADIUS, 2 * RADIUS, 2 * RADIUS);
		}
	}
	public boolean collide(Point p) {
		int abs = distanceSquaredTo(p);

		return (abs <= Math.pow(2*RADIUS, 2));

	}

	//Returns the distance to the power of 2 between two points
	public int distanceSquaredTo(Point p){
		double xDiff = this.getX() - p.getX();
		double yDiff = this.getY() - p.getY();
		int abs = (int) (Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
		
		return abs;
		
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

	public int hashCode(){
		return (int)posX;
	}
	
	public String toString() {
		return "(X,Y) = ("+posX+","+posY+")";
	}

	public double distanceTo(Point point, double p) {
		
		double dist = -1;
		
		if (p == Double.POSITIVE_INFINITY){
		// infinity norm
			if (Math.abs(posX - point.posX) > Math.abs (posY - point.posY)){
				dist = Math.abs(posX - point.posX);
			} else dist = Math.abs(posY - point.posY);
		}
		
		// other norms to be implemented
		
		return dist;
	}

	public static Comparator<Point> COMPARE_BY_YCoord = new Comparator<Point>() {
        public int compare(Point first, Point second) {
            return (int)(first.posY-second.posY);
        }
    };
    
    public static Comparator<Point> COMPARE_BY_XCoord = new Comparator<Point>() {
        public int compare(Point first, Point second) {
            return (int)(first.posX-second.posX);
        }
    };
}
