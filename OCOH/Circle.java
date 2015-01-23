package OCOH;

import java.awt.Color;
import java.awt.Graphics;

public class Circle {

	public Point center;
	public double radius;
	
	public Circle(Point center, double radius){
		this.center = center;
		this.radius = radius;
	}
	
	public Point getIntersectionPoint(LineSegment l){
		
		Point intersectionPoint;
		
		double dx = l.end.posX - l.start.posX;
		double dy = l.end.posY - l.start.posY;
		double dr = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0));
		double d = l.start.posX * l.end.posY - l.end.posX * l.start.posY;
		double disc = Math.pow(radius, 2.0) * Math.pow(dr, 2.0) - Math.pow(d, 2.0);
		
		double x = (d * dy + Math.signum(dy) * dy * Math.sqrt(disc)) / Math.pow(dr, 2.0);
		double y = (-d * dx + Math.abs(dy) * Math.sqrt(disc)) / Math.pow(dr, 2.0);
		
		System.out.println(disc + " " );
		
		intersectionPoint = new Point(x, y);
		
		return intersectionPoint;
	}
	
	public void draw(Graphics g){
		
		int x = (int)(center.posX - (radius));
		int y = (int)(center.posY - (radius));
		g.setColor(Color.BLACK);
		g.drawOval(x, y, (int)(2*radius), (int)(2*radius));
		
		
	}
	
}
