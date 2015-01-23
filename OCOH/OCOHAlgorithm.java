package OCOH;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import anja.geom.BasicLine2;
import anja.geom.Circle2;
import anja.geom.Intersection;
import anja.geom.Line2;
import anja.geom.Point2;
import anja.geom.Rectangle2;
import anja.geom.Segment2;
import anja.util.SimpleList;


public class OCOHAlgorithm {
		
		Point currentFacility = new Point(); // turnpike endpoint CHECK WHETHER CURRENT POINTS ARE CORRECT
		Point currentTurnpikeStart = new Point();
		double currentRadius = 0;
		
	
	// case a) and b)
		PointList[] L;
		PointList[] R;
		
		Point[] facilityPointsLR;
		Point[] turnpikePointsLR;
		double[] partitionRadiusLR;
		
		// case c)
		PointList[][] UR;
		PointList[][] DL;
		
		// Extreme Points of L and R
		Point[][] XL;
		Point[][] XR;
		
		// Extreme Points of UR_i,j and DL_i,j
		Point[][][] XUR;
		Point[][][] XDL;
		

		PointList[] c1;
		PointList[] c2;
		double prevY = 0;
		double eps1, eps2, x = 0;
		double maxDist;
		
		int highwayLength;
		int velocity;
		
	public void runAlgorithm(PointList customers, int highWayLength, int velocity) {
		
		if (!customers.isEmpty()){
			
			this.highwayLength = highWayLength;
			this.velocity = velocity;
			
			maxDist = maxDistInSet(customers);
			
			L = new PointList[customers.getSize()-1];
			R = new PointList[customers.getSize()-1];
			
			facilityPointsLR = new Point[customers.getSize()-1];
			turnpikePointsLR = new Point[customers.getSize()-1];
			partitionRadiusLR = new double[customers.getSize()-1];
			
			UR = new PointList[customers.getSize()][customers.getSize()];
			DL = new PointList[customers.getSize()][customers.getSize()];
			
			splitByLine(customers);
			splitByQuadrant(customers);
			
			// Extreme Points of L and R to find smallest axis-aligned bounding box
			XL = new Point[customers.getSize()-1][4]; // Xmin, Xmax, Ymin, Ymax
			XR = new Point[customers.getSize()-1][4];
			// Extreme Points of UR_i,j and DL_i,j
			XUR = new Point[customers.getSize()][customers.getSize()][4];
			XDL = new Point[customers.getSize()][customers.getSize()][4];
			
			// Find extreme points for all sets L_i, R_i for smallest axis-aligned rects
			for (int i = 0; i < customers.getSize()-1; i++){
				XL[i] = L[i].getExtremePoints();
				XR[i] = R[i].getExtremePoints();
			}
			for (int i = 0; i < customers.getSize(); i++){
				for (int j = 0; j < customers.getSize(); j++){
					if (!UR[i][j].isEmpty()){
						XUR[i][j] = UR[i][j].getExtremePoints();
					}
					if (!DL[i][j].isEmpty()){
						XDL[i][j] = DL[i][j].getExtremePoints();
					}
				}
			}
			
			c1 = new PointList[customers.getSize()-1];
			c2 = new PointList[customers.getSize()-1];
			
			
			// solve basic problem for all pairs (L,R)
			for (int i = 0; i < L.length; i++){

				eps1 = Math.max(0, L[i].delta() + highWayLength/velocity - R[i].delta());
				eps2 = Math.max(0, R[i].delta() - L[i].delta() - highWayLength/velocity);
				
				x = BPradius(L[i], R[i], 0, maxDist);
				c1[i] = center(L[i], L[i].delta() + eps2 + x);
				c2[i] = center(R[i], R[i].delta() + eps1 + x);
			
				solveBasicProblem(L[i], R[i], highWayLength, velocity);
				facilityPointsLR[i] = currentFacility;
				turnpikePointsLR[i] = currentTurnpikeStart;
				partitionRadiusLR[i] = currentRadius;
				System.out.println("Facility: " + facilityPointsLR[i].toString() + "T: " + turnpikePointsLR[i]);
				System.out.println("Distance: " + Math.sqrt(facilityPointsLR[i].distanceSquaredTo(turnpikePointsLR[i])));
				System.out.println("Radius: " + partitionRadiusLR[i]);
				
			}	
			// case c)
			// Find extreme points for all sets UR_i,j
			
		}
	}
	
	
	public double BPradius(PointList list1, PointList list2, double m, double M){
		
		double y = (m+M)/2;
		
		double e1 = Math.max(0, list1.delta() + highwayLength/velocity - list2.delta());
		double e2 = Math.max(0, list2.delta() - list1.delta() - highwayLength/velocity);
		
		PointList centers1 = center(list1, list1.delta() + e2 + y); // Center(H, d(H)+e2+x)
		PointList centers2 = center(list2, list2.delta() + e1 + y); // Center(W, d(W)+e1+x)
			
		// find maximum distance between centers1 and centers2
		double maxDist = maxDist(centers1, centers2);
		
		// find minimum distance between centers1 and centers2
		double minDist = minDist(centers1, centers2);
		
		if ((int)Math.abs(prevY - y) == 0){
			return prevY;
		}
		if (maxDist >= highwayLength && minDist <= highwayLength){
			prevY = y;
			if ((int)Math.abs(M-m) == 0){
				return y;
			} else return BPradius(list1, list2, m, y);
		} else {
			return BPradius(list1, list2, y, M);
		}
		
	}
	
	public void solveBasicProblem(PointList list1, PointList list2, double highWayLength, double velocity){
		
		double e1 = Math.max(0, list1.delta() + highWayLength/velocity - list2.delta());
		double e2 = Math.max(0, list2.delta() - list1.delta() - highWayLength/velocity);
		
		x = BPradius(list1, list2, 0, maxDist);
		
		PointList centers1 = center(list1, list1.delta() + e2 + x); // Center(H, d(H)+e2+x)
		PointList centers2 = center(list2, list2.delta() + e1 + x); // Center(W, d(W)+e1+x)
	
		if (centers1.getSize() == 2 && centers2.getSize() == 2){
			
			LineSegment line1 = new LineSegment(centers1.points.get(0), centers1.points.get(1));
			LineSegment line2 = new LineSegment(centers2.points.get(0), centers2.points.get(1));
			Point2 p11 = new Point2(centers1.points.get(0).posX, centers1.points.get(0).posY);
			Point2 p12 = new Point2(centers1.points.get(1).posX, centers1.points.get(1).posY);
			Point2 p21 = new Point2(centers2.points.get(0).posX, centers2.points.get(0).posY);
			Point2 p22 = new Point2(centers2.points.get(1).posX, centers2.points.get(1).posY);
			Segment2 s1 = new Segment2(p11,p12);
			Segment2 s2 = new Segment2(p21,p22);
			Circle2 c1 = new Circle2(p11, (float)highWayLength);
			Circle2 c2 = new Circle2(p12, (float)highWayLength);
			Circle2 c3 = new Circle2(p21, (float)highWayLength);
			Circle2 c4 = new Circle2(p22, (float)highWayLength);
			Intersection i;
			Intersection i1 = c1.intersection(s2);
			Intersection i2 = c2.intersection(s2);
			Intersection i3 = c3.intersection(s1);
			Intersection i4 = c4.intersection(s1);
			if (i1.getList().length() < 1){
				if (i2.getList().length() < 1){
					if (i3.getList().length() < 1){
						i = i4;
						currentTurnpikeStart = new Point(p22.getX(), p22.getY());
//									System.out.println("C4");
					} else {
						i = i3;
						currentTurnpikeStart = new Point(p21.getX(), p21.getY());
//									System.out.println("C3");
					}
				} else {
					i = i2;
					currentTurnpikeStart = new Point(p12.getX(), p12.getY());
//								System.out.println("C2");
				}
			} else {
				i = i1; 
				currentTurnpikeStart = new Point(p11.getX(), p11.getY());
//							System.out.println("C1");
			}
			if (i.getList() != null){
				SimpleList list = i.getList();
//							System.out.println("Anzahl Schnitte: " + list.length());
//							System.out.println(list.toString());
				if (list.getValueAt(0) instanceof Point2) {
					Point2 p = (Point2) list.getValueAt(0);
					currentFacility = new Point(p.getX(), p.getY());
				}
			}
			currentRadius = Math.max(list2.delta() + e1 + x, list1.delta() + e2 + x);
			System.out.println("2 Lines: " + currentFacility.distanceSquaredTo(currentTurnpikeStart));
		} else if (centers1.getSize() == 4 && centers2.getSize() == 2){
			Rectangle2 rect1 = new Rectangle2((float)centers1.points.get(0).posX, (float)centers1.points.get(0).posY,
					(float)(centers1.points.get(2).posX-centers1.points.get(0).posX), 
					(float)(centers1.points.get(1).posY-centers1.points.get(0).posY));
			LineSegment line2 = new LineSegment(centers2.points.get(0), centers2.points.get(1));
			
			// Circles around rect1
			Point2 p11 = new Point2(rect1.getX(), rect1.getY()); // upper left corner
			Point2 p12 = new Point2(rect1.getX() + rect1.width, rect1.getY()); // upper right
			Point2 p13 = new Point2(rect1.getX() + rect1.width, rect1.getY() + rect1.height);//lower right
			Point2 p14 = new Point2(rect1.getX(), rect1.getY() + rect1.height); // lower left
			Circle2 c1 = new Circle2(p11, (float)highWayLength);
			Circle2 c2 = new Circle2(p12, (float)highWayLength);
			Circle2 c3 = new Circle2(p13, (float)highWayLength);
			Circle2 c4 = new Circle2(p14, (float)highWayLength);
			
			// Circles around line2
			Point2 p21 = new Point2(line2.start.posX, line2.start.posY);
			Point2 p22 = new Point2(line2.end.posX, line2.end.posY);
			Circle2 c5 = new Circle2(p21, (float)highWayLength);
			Circle2 c6 = new Circle2(p22, (float)highWayLength);
			
			// check intersection from rect1 circles with line2
			Segment2 s1 = new Segment2(p21, p22);
			Intersection i = null;
			Intersection i1 = c1.intersection(s1);
			Intersection i2 = c2.intersection(s1);
			Intersection i3 = c3.intersection(s1);
			Intersection i4 = c4.intersection(s1);
			if (i1.getList().length() < 1){
				if (i2.getList().length() < 1){
					if (i3.getList().length() < 1){
						if (i4.getList().length() < 1){
							Point p1 = CircleRectInt(c5, rect1);
							if (p1 != null){
								currentTurnpikeStart = new Point(p21.getX(), p21.getY());
								currentFacility = p1;
							} else {
								Point p2 = CircleRectInt(c6, rect1);
								currentTurnpikeStart = new Point(p22.getX(), p22.getY());
								currentFacility = p2;
							}
						} else {
							i = i4;
						currentTurnpikeStart = new Point(p14.getX(), p14.getY());
//									System.out.println("C4");
						}
					} else {
						i = i3;
						currentTurnpikeStart = new Point(p13.getX(), p13.getY());
//									System.out.println("C3");
					}
				} else {
					i = i2;
					currentTurnpikeStart = new Point(p12.getX(), p12.getY());
//								System.out.println("C2");
				}
			} else {
				i = i1; 
				currentTurnpikeStart = new Point(p11.getX(), p11.getY());
//							System.out.println("C1");
			}
			if(i != null){
				if (i.getList() != null){
					SimpleList list = i.getList();
//								System.out.println("Anzahl Schnitte: " + list.length());
					System.out.println(list.toString());
					if (list.getValueAt(0) instanceof Point2) {
						Point2 p = (Point2) list.getValueAt(0);
						currentFacility = new Point(p.getX(), p.getY());
					}
				}
			}
				currentRadius = Math.max(list2.delta() + e1 + x, list1.delta() + e2 + x);
				System.out.println("1 Rect, 1 Line, a: " + currentFacility.distanceSquaredTo(currentTurnpikeStart));
			} else if (centers1.getSize() == 2 && centers2.getSize() == 4){
			Rectangle2 rect1 = new Rectangle2((float)centers2.points.get(0).posX, (float)centers2.points.get(0).posY,
					(float)(centers2.points.get(2).posX-centers2.points.get(0).posX), 
					(float)(centers2.points.get(1).posY-centers2.points.get(0).posY));
			LineSegment line2 = new LineSegment(centers1.points.get(0), centers1.points.get(1));
			// Circles around rect1
			Point2 p11 = new Point2(rect1.getX(), rect1.getY()); // upper left corner
			Point2 p12 = new Point2(rect1.getX() + rect1.width, rect1.getY()); // upper right
			Point2 p13 = new Point2(rect1.getX() + rect1.width, rect1.getY() + rect1.height);//lower right
			Point2 p14 = new Point2(rect1.getX(), rect1.getY() + rect1.height); // lower left
			Circle2 c1 = new Circle2(p11, (float)highWayLength);
			Circle2 c2 = new Circle2(p12, (float)highWayLength);
			Circle2 c3 = new Circle2(p13, (float)highWayLength);
			Circle2 c4 = new Circle2(p14, (float)highWayLength);
			
			// Circles around line2
			Point2 p21 = new Point2(line2.start.posX, line2.start.posY);
			Point2 p22 = new Point2(line2.end.posX, line2.end.posY);
			Circle2 c5 = new Circle2(p21, (float)highWayLength);
			Circle2 c6 = new Circle2(p22, (float)highWayLength);
			
			// check intersection from rect1 circles with line2
			Segment2 s1 = new Segment2(p21, p22);
			Intersection i = null;
			Intersection i1 = c1.intersection(s1);
			Intersection i2 = c2.intersection(s1);
			Intersection i3 = c3.intersection(s1);
			Intersection i4 = c4.intersection(s1);
			if (i1.getList().length() < 1){
				if (i2.getList().length() < 1){
					if (i3.getList().length() < 1){
						if (i4.getList().length() < 1){
							Point p1 = CircleRectInt(c5, rect1);
							if (p1 != null){
								currentTurnpikeStart = new Point(p21.getX(), p21.getY());
								currentFacility = p1;
							} else {
								Point p2 = CircleRectInt(c6, rect1);
								currentTurnpikeStart = new Point(p22.getX(), p22.getY());
								currentFacility = p2;
							}
						} else {
							i = i4;
						currentTurnpikeStart = new Point(p14.getX(), p14.getY());
//									System.out.println("C4");
						}
					} else {
						i = i3;
						currentTurnpikeStart = new Point(p13.getX(), p13.getY());
//									System.out.println("C3");
					}
				} else {
					i = i2;
					currentTurnpikeStart = new Point(p12.getX(), p12.getY());
//								System.out.println("C2");
				}
			} else {
				i = i1; 
				currentTurnpikeStart = new Point(p11.getX(), p11.getY());
//							System.out.println("C1");
			}
			if(i != null){
				if (i.getList() != null){
					SimpleList list = i.getList();
					if (list.getValueAt(0) instanceof Point2) {
						Point2 p = (Point2) list.getValueAt(0);
						currentFacility = new Point(p.getX(), p.getY());
					}
				}
			}
			currentRadius = Math.max(list2.delta() + e1 + x, list1.delta() + e2 + x);
			System.out.println("1 Rect, 1 Line, b: " + currentFacility.distanceSquaredTo(currentTurnpikeStart));
		} else if (centers1.getSize() == 4 && centers2.getSize() == 4){
			Rectangle2 rect1 = new Rectangle2((float)centers1.points.get(0).posX, (float)centers1.points.get(0).posY,
					(float)(centers1.points.get(2).posX-centers1.points.get(0).posX), 
					(float)(centers1.points.get(1).posY-centers1.points.get(0).posY));
		
			Rectangle2 rect2 = new Rectangle2((float)centers2.points.get(0).posX, (float)centers2.points.get(0).posY,
					(float)(centers2.points.get(2).posX-centers2.points.get(0).posX), 
					(float)(centers2.points.get(1).posY-centers2.points.get(0).posY));
			// Rect1
			Point2 p11 = new Point2(rect1.getX(), rect1.getY()); // upper left corner
			Point2 p12 = new Point2(rect1.getX() + rect1.width, rect1.getY()); // upper right
			Point2 p13 = new Point2(rect1.getX() + rect1.width, rect1.getY() + rect1.height);//lower right
			Point2 p14 = new Point2(rect1.getX(), rect1.getY() + rect1.height); // lower left
			
			Circle2 c1 = new Circle2(p11, (float)highWayLength);
			Circle2 c2 = new Circle2(p12, (float)highWayLength);
			Circle2 c3 = new Circle2(p13, (float)highWayLength);
			Circle2 c4 = new Circle2(p14, (float)highWayLength);
			
			//Rect2
			Point2 p21 = new Point2(rect2.getX(), rect2.getY()); // upper left corner
			Point2 p22 = new Point2(rect2.getX() + rect2.width, rect2.getY()); // upper right
			Point2 p23 = new Point2(rect2.getX() + rect2.width, rect2.getY() + rect2.height);//lower right
			Point2 p24 = new Point2(rect2.getX(), rect2.getY() + rect2.height); // lower left
			
			Circle2 c5 = new Circle2(p21, (float)highWayLength);
			Circle2 c6 = new Circle2(p22, (float)highWayLength);
			Circle2 c7 = new Circle2(p23, (float)highWayLength);
			Circle2 c8 = new Circle2(p24, (float)highWayLength);
			
			if (CircleRectInt(c1, rect2) != null){
				currentTurnpikeStart = new Point(p11.getX(), p11.getY());
				currentFacility = CircleRectInt(c1, rect2);
			} else if (CircleRectInt(c2, rect2) != null){
				currentTurnpikeStart = new Point(p12.getX(), p12.getY());
				currentFacility = CircleRectInt(c2, rect2);
			} else if (CircleRectInt(c3, rect2) != null){
				currentTurnpikeStart = new Point(p13.getX(), p13.getY());
				currentFacility = CircleRectInt(c3, rect2);
			} else if (CircleRectInt(c4, rect2) != null){
				currentTurnpikeStart = new Point(p14.getX(), p14.getY());
				currentFacility = CircleRectInt(c4, rect2);
			} else if (CircleRectInt(c5, rect1) != null){
				currentTurnpikeStart = new Point(p21.getX(), p21.getY());
				currentFacility = CircleRectInt(c5, rect2);
			} else if (CircleRectInt(c6, rect1) != null){
				currentTurnpikeStart = new Point(p22.getX(), p22.getY());
				currentFacility = CircleRectInt(c6, rect2);
			} else if (CircleRectInt(c7, rect1) != null){
				currentTurnpikeStart = new Point(p23.getX(), p23.getY());
				currentFacility = CircleRectInt(c7, rect2);
			} else if (CircleRectInt(c8, rect1) != null){
				currentTurnpikeStart = new Point(p24.getX(), p24.getY());
				currentFacility = CircleRectInt(c8, rect2);
			}
			currentRadius = Math.max(list2.delta() + e1 + x, list1.delta() + e2 + x);
			System.out.println("2Rect: " + currentFacility.distanceSquaredTo(currentTurnpikeStart));
		}
	}
	
	public double minDist(PointList list1, PointList list2){
		
		// returns minimum distance between two objects defined by list1 and list2
		
		double minDist = Double.POSITIVE_INFINITY;
		
		if (list1.getSize() == 1 && list2.getSize() == 1){
			// minimum distance between two points
			Point2 p1 =  new Point2(list1.points.get(0).posX, list1.points.get(0).posY);
			Point2 p2 =  new Point2(list2.points.get(0).posX, list2.points.get(0).posY);
			minDist = p1.distance(p2);
		} else if (list1.getSize() == 1 && list2.getSize() == 2){
			// minimum distance between a point and a line segment
			Point2 p1 =  new Point2(list1.points.get(0).posX, list1.points.get(0).posY);
			Point2 p21 = new Point2(list2.points.get(0).posX, list2.points.get(0).posY);
			Point2 p22 = new Point2(list2.points.get(1).posX, list2.points.get(1).posY);
			Segment2 l2 = new Segment2(p21, p22);
			minDist = l2.distance(p1);
		} else if (list1.getSize() == 2 && list2.getSize() == 1){
			// minimum distance between a point and a line segment
			Point2 p11 = new Point2(list1.points.get(0).posX, list1.points.get(0).posY);
			Point2 p12 = new Point2(list1.points.get(1).posX, list1.points.get(1).posY);
			Point2 p2 =  new Point2(list2.points.get(0).posX, list2.points.get(0).posY);
			Segment2 l1 = new Segment2(p11, p12);
			minDist = l1.distance(p2);
		} else if (list1.getSize() == 2 && list2.getSize() == 2){
			// minimum distance between two line segments
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			LineSegment l1 = new LineSegment(p11, p12);
			LineSegment l2 = new LineSegment(p21, p22);
			minDist = l1.distanceTo(l2);
		} else if (list1.getSize() == 4 && list2.getSize() == 1){
			// minimum distance between a rectangle and a point
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p13 = new Point(list1.points.get(2).posX, list1.points.get(2).posY);
			Point p2 =  new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Rectangle2 r1 = new Rectangle2((float)p11.posX, (float)p11.posY, (float)(Math.abs(p13.posX - p11.posX)), (float)(Math.abs(p12.posY - p11.posY)));
			minDist = p2.distanceTo(r1);
		} else if (list1.getSize() == 1 && list2.getSize() == 4){
			// minimum distance between a rectangle and a point
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Point p23 = new Point(list2.points.get(2).posX, list2.points.get(2).posY);
			Point p1 =  new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Rectangle2 r2 = new Rectangle2((float)p21.posX, (float)p21.posY, (float)(Math.abs((p23.posX - p21.posX))), (float)(Math.abs((p22.posY - p21.posY))));
			minDist = p1.distanceTo(r2);
		} else if (list1.getSize() == 2 && list2.getSize() == 4){
			// minimum distance between a rectangle and a line segment
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Point p23 = new Point(list2.points.get(2).posX, list2.points.get(2).posY);
			LineSegment l1 = new LineSegment(p11, p12);
			Rectangle2 r2 = new Rectangle2((float)p21.posX, (float)p21.posY, (float)(Math.abs((p23.posX - p21.posX))), (float)(Math.abs(p22.posY - p21.posY)));
			minDist = l1.distanceTo(r2);
		} else if (list1.getSize() == 4 && list2.getSize() == 2){
			// minimum distance between a rectangle and a line segment
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p13 = new Point(list1.points.get(2).posX, list1.points.get(2).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Rectangle2 r1 = new Rectangle2((float)p11.posX, (float)p11.posY, (float)(Math.abs((p13.posX - p11.posX))), (float)(Math.abs((p12.posY - p11.posY))));
			LineSegment l2 = new LineSegment(p21, p22);
			minDist = l2.distanceTo(r1);
		} else if (list1.getSize() == 4 && list2.getSize() == 4){
			// minimum distance between two rectangles
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p13 = new Point(list1.points.get(2).posX, list1.points.get(2).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Point p23 = new Point(list2.points.get(2).posX, list2.points.get(2).posY);
			Rectangle2 r1 = new Rectangle2((float)p11.posX, (float)p11.posY, (float)(Math.abs((p13.posX - p11.posX))), (float)(Math.abs((p12.posY - p11.posY))));
			Rectangle2 r2 = new Rectangle2((float)p21.posX, (float)p21.posY, (float)(Math.abs((p23.posX - p21.posX))), (float)(Math.abs((p22.posY - p21.posY))));
			minDist = RectDistanceToRect(r1, r2);
		}
		
		return minDist;
		
	}
	
	public double maxDist(PointList list1, PointList list2){
		
		// returns max distance between p in list 1 and q in list 2

		double maxDist = Double.NEGATIVE_INFINITY;
		ArrayList<Double> distances = new ArrayList<Double>();
		
		for (int i = 0; i < list1.getSize(); i++){
			for (int j = 0; j < list2.getSize(); j++){
				distances.add(Math.sqrt(list1.points.get(i).distanceSquaredTo(list2.points.get(j))));
			}
		}

		for (Double dist : distances){
			if (dist > maxDist) maxDist = dist;
		}
		
		return maxDist;
		
	}
	
	public double maxDistInSet(PointList S){
		// returns max distance between two points in set S
		double maxDist = Double.NEGATIVE_INFINITY;
		ArrayList<Double> distances = new ArrayList<Double>();
		for (int i = 0; i < S.points.size(); i++){
			for (int j = 0; j < S.points.size(); j++){
				distances.add(Math.sqrt(S.points.get(i).distanceSquaredTo(S.points.get(j))));
			}
		}

		for (Double dist : distances){
			if (dist > maxDist) maxDist = dist;
		}
		
		return maxDist;
	}
	
	public double RectDistanceToRect(Rectangle2 rect1, Rectangle2 rect2){
		// return min dist between two rects
		
		if (rectContainsRect(rect1, rect2) || rectContainsRect(rect2, rect1)) return 0;
		
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
		
		ArrayList<Double> distances = new ArrayList<Double>();
		distances.add(l1.distanceTo(rect2));
		distances.add(l2.distanceTo(rect2));
		distances.add(l3.distanceTo(rect2));
		distances.add(l4.distanceTo(rect2));
		
		for (double d : distances){
			if (d < minDist) minDist = d;
		}
		
		return minDist;
	}
	
	public boolean rectContainsRect(Rectangle2 rect1, Rectangle2 rect2){
		
		// return true, if rect2 is contained in rect1
		if (rect1.contains(rect2.getX(), rect2.getY(), rect2.getWidth(), rect2.getHeight())){
			return true;
		} else return false;
	}
	
	public boolean RectContainsCircle(Circle2 circ, Rectangle2 rect){
		// returns true if the circle is fully contained in rect
		
		float r = circ.radius;
		Point2 circMid = circ.centre;
		Point2 p11 = new Point2(rect.getX(), rect.getY()); // upper left corner
		Point2 p12 = new Point2(rect.getX() + rect.width, rect.getY()); // upper right
		Point2 p13 = new Point2(rect.getX() + rect.width, rect.getY() + rect.height);//lower right
		Point2 p14 = new Point2(rect.getX(), rect.getY() + rect.height); // lower left
		if (rect.contains(circMid)&&(p11.distance(circMid) >= r) && (p12.distance(circMid) >= r) 
				&& (p13.distance(circMid) >= r) && (p14.distance(circMid) >= r)){
			return true;
		} 
		
		return false;
		
		
	}
	
	public Point CircleRectInt(Circle2 circ, Rectangle2 rect){
		// returns one intersection point between circ and rect on rect
		Point intersectionPoint = null;
		
		Segment2 s1 = rect.top();
		Segment2 s2 = rect.bottom();
		Segment2 s3 = rect.left();
		Segment2 s4 = rect.right();

		Intersection i1 = circ.intersection(s1);
		Intersection i2 = circ.intersection(s2);
		Intersection i3 = circ.intersection(s3);
		Intersection i4 = circ.intersection(s4);
		Intersection i = null;
		
		if (i1.getList().length() < 1){
			if (i2.getList().length() < 1){
				if (i3.getList().length() < 1){
					if (i4.getList().length() < 1){
						if (RectContainsCircle(circ, rect)){
							intersectionPoint = new Point(circ.centre.getX() + circ.radius, circ.centre.getY());
						}
					} else {
						i = i4;
					}
				} else {
					i = i3;
				}
			} else {
				i = i2;
			}
		} else {
			i = i1; 
		}
		if (i != null){
			if (i.getList() != null){
				SimpleList list = i.getList();
				if (list.getValueAt(0) instanceof Point2) {
					Point2 p = (Point2) list.getValueAt(0);
					intersectionPoint = new Point(p.getX(), p.getY());
				}
			}
		}
		
		return intersectionPoint;
		
	}
	
	public PointList center(PointList T, double radius){
			
		// returns centers: locus of the centers of the axis-parallel squares of radius r that cover T
		// center(T,r) = center(extreme(T),r)
		
		PointList centers = new PointList(Color.PINK);
		Point[] extrema = new Point[4];
		Point currentCenter;
		
		double xLength;
		double yLength;
		double xStart;
		double xEnd;
		double yStart;
		double yEnd;
		
		extrema = T.getExtremePoints();
		
		if (radius < T.delta()) return centers; // centers is empty
		
		// find X coordinates
		currentCenter = new Point(extrema[0].posX + radius, extrema[0].posY);
		if (currentCenter.posX + radius == extrema[1].posX) {
			// current x position is only possible x position
			xStart = currentCenter.posX;
			xEnd = xStart;
			
		} else {
			// we can move in x direction
			xLength = currentCenter.posX + radius - extrema[1].posX;
			xStart = currentCenter.posX - xLength;
			xEnd = currentCenter.posX + xLength;
		}
		
		// find Y coordinates
		currentCenter = new Point(extrema[2].posX, extrema[2].posY + radius);
		if (currentCenter.posY + radius == extrema[3].posY) {
			// current y position is only possible y position
			yStart = currentCenter.posY;
			yEnd = yStart;
		} else {
			// we can move in y direction
			yLength = currentCenter.posY + radius - extrema[3].posY;
			yStart = currentCenter.posY - yLength;
			yEnd = currentCenter.posY + yLength;
		}
		
		// points of centers
		/*
		 * 0----2
		 * |    |
		 * |    |
		 * 1----3
		 */
		centers.addPoint(new Point(xStart, yStart));
		if (!centers.contains(new Point(xStart, yEnd))){
			centers.addPoint(new Point(xStart, yEnd));
		}
		if (!centers.contains(new Point(xEnd, yStart))){
			centers.addPoint(new Point(xEnd, yStart));
		}
		if (!centers.contains(new Point(xEnd, yEnd))){
			centers.addPoint(new Point(xEnd, yEnd));
		}
		

		return centers;
	
	}
	
	public void splitByQuadrant(PointList S){
		
		PointList Y = S;
		PointList X = S;
		
		Collections.sort(Y.points, Point.COMPARE_BY_YCoord); // q_i
		Collections.sort(X.points, Point.COMPARE_BY_XCoord); // p_i
		
		// initialize all point lists
		for (int i = 0; i < S.getSize(); i++){
			for (int j = 0; j < S.getSize(); j++){
				UR[i][j] = new PointList(Color.YELLOW);
				DL[i][j] = new PointList(Color.GREEN);
			}
		}
		// find points in UR
		for (int u = 0; u < S.getSize(); u++){
			for (int i = Y.getSize()-1; i >= 0; i--){
				for (int j = 0; j < X.getSize(); j++){
					if (S.points.get(u).posY > Y.points.get(i).posY && S.points.get(u).posX < X.points.get(j).posX)
						UR[i][j].addPoint(S.points.get(u));
				}
			}
		}
		
		// find points in DL
		for (int u = 0; u < S.getSize(); u++){
			for (int i = Y.getSize()-1; i >= 0; i--){
				for (int j = 0; j < X.getSize(); j++){
					if (!UR[i][j].contains(S.points.get(u))) {
						DL[i][j].addPoint(S.points.get(u));
					}
				}
			}
		}
	}
	
	public void splitByLine(PointList S){

		// case a) and b)

		// initialize all point lists
		for (int i = 0; i < S.getSize()-1; i++){
				L[i] = new PointList(Color.BLUE);
				R[i] = new PointList(Color.RED);
		}
		
		Collections.sort(S.points, Point.COMPARE_BY_XCoord);
		// Divide into L_i, R_i for all 1<=i<n
		for (int i = 0; i < S.getSize()-1; i++){
			for (int k = 0; k < i+1; k++){
				L[i].addPoint(S.points.get(k));	
			}
			for (int j = i+1; j < S.getSize(); j++){
				R[i].addPoint(S.points.get(j));
			}

//			System.out.println("L[" + i + "] =" +LR[0][i].toString());
//			System.out.println("R[" + i + "] =" +LR[1][i].toString());
		}
		
	}
	
}
