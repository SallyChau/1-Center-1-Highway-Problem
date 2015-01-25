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
	
		Point[] minDist1;
		Point[] minDist2;
		Point[] maxDist1;
		Point[] maxDist2;
		
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
			maxDist1 = new Point[customers.getSize()-1];
			maxDist2 = new Point[customers.getSize()-1];
			minDist1 = new Point[customers.getSize()-1];
			minDist2 = new Point[customers.getSize()-1];
			
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

				eps1 = Math.max(0, L[i].delta() + highwayLength/velocity - R[i].delta());
				eps2 = Math.max(0, R[i].delta() - L[i].delta() - highwayLength/velocity);
//				solveBasicProblem(L[i], R[i]);
				solveBP(L[i], R[i]); 
				facilityPointsLR[i] = currentFacility;
				turnpikePointsLR[i] = currentTurnpikeStart;
				partitionRadiusLR[i] = currentRadius;
				c1[i] = center(L[i], L[i].delta() + eps2 + x);
				c2[i] = center(R[i], R[i].delta() + eps1 + x);

				if (contains(c1[i], minDistPoints(c1[i], c2[i])[0])){
					minDist1[i] = minDistPoints(c1[i], c2[i])[0]; 
					minDist2[i] = minDistPoints(c1[i], c2[i])[1];
				} else {
					minDist1[i] = minDistPoints(c1[i], c2[i])[1]; 
					minDist2[i] = minDistPoints(c1[i], c2[i])[0];
				}
				
				maxDist1[i] = maxDistPoints(c1[i], c2[i])[0]; 
				maxDist2[i] = maxDistPoints(c1[i], c2[i])[1];
				
			
				
//				System.out.println("Facility: " + facilityPointsLR[i].toString() + " T: " + turnpikePointsLR[i]);
//				System.out.println("Distance: " + Math.sqrt(facilityPointsLR[i].distanceSquaredTo(turnpikePointsLR[i])));
//				System.out.println("Radius: " + partitionRadiusLR[i]);
				
			}	
			// case c)
			// Find extreme points for all sets UR_i,j
			
			System.out.println("F: "+currentFacility.toString() + " T: " + currentTurnpikeStart);
			System.out.println("Distance: " + Math.sqrt(currentFacility.distanceSquaredTo(currentTurnpikeStart)));
			
		}
	}

	/**
	 * Computes the smallest radius needed to place a turnpike 
	 * of length highwayLength as an input for the method 
	 * @see{OCOH.OCOHAlgorithm.center(PointList T, double radius)}.
	 * This method uses binary search to find the smallest radius 
	 * in time O(log n).
	 * 
	 * @param list1
	 * 			List of Points 
	 * @param list2
	 * 			List of Points
	 * @param m
	 * 			lower bound
	 * @param M
	 * 			upper bound
	 * @return
	 * 			the minimum radius needed to place a turnpike
	 */
	public double getBPradius(PointList list1, PointList list2, double m, double M){
		
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
			} else return getBPradius(list1, list2, m, y);
		} else {
			return getBPradius(list1, list2, y, M);
		}
		
	}
	
	private void setCurrentTurnpike(Point t){
		currentTurnpikeStart = t;
	}
	
	private void setCurrentFacility(Point f){
		currentFacility = f;
	}
	
	public void solvePointToPointBP(PointList centers1, PointList centers2){
		Point t = new Point(centers1.points.get(0).posX, centers1.points.get(0).posY);
		Point f = new Point(centers2.points.get(0).posX, centers2.points.get(0).posY);
		setCurrentTurnpike(t);
		setCurrentFacility(f);
	}
	/**
	 * @param centers1 
	 * 			input point
	 * @param centers2
	 * 			input line segment
	 */
	public void solvePointToLineBP(PointList centers1, PointList centers2){
		Point2 p1 = new Point2(centers1.points.get(0).posX, centers1.points.get(0).posY);
		Point2 p21 = new Point2(centers2.points.get(0).posX, centers2.points.get(0).posY);
		Point2 p22 = new Point2(centers2.points.get(1).posX, centers2.points.get(1).posY);
		Segment2 s = new Segment2(p21, p22);
		Circle2 c = new Circle2(p1, (float)highwayLength);
		Intersection i = c.intersection(s);
		Point2 iPoint = (Point2) i.getList().getValueAt(0);
		Point t = new Point(p1.getX(), p1.getY());
		Point f = new Point(iPoint.getX(), iPoint.getY());
		setCurrentFacility(f);
		setCurrentTurnpike(t);
	}
	
	public void solveLineToLineBP(PointList centers1, PointList centers2){
		
		Point t = new Point();
		Point f = new Point();
		
		Point2 p11 = new Point2(centers1.points.get(0).posX, centers1.points.get(0).posY);
		Point2 p12 = new Point2(centers1.points.get(1).posX, centers1.points.get(1).posY);
		Point2 p21 = new Point2(centers2.points.get(0).posX, centers2.points.get(0).posY);
		Point2 p22 = new Point2(centers2.points.get(1).posX, centers2.points.get(1).posY);
		Segment2 s1 = new Segment2(p11,p12);
		Segment2 s2 = new Segment2(p21,p22);
		Circle2 c1 = new Circle2(p11, (float)highwayLength);
		Circle2 c2 = new Circle2(p12, (float)highwayLength);
		Circle2 c3 = new Circle2(p21, (float)highwayLength);
		Circle2 c4 = new Circle2(p22, (float)highwayLength);
		List<Circle2> circs = new ArrayList<Circle2>();
		circs.add(c1);
		circs.add(c2);
		circs.add(c3);
		circs.add(c4);
		
		PointList interPoints = new PointList(Color.CYAN);
		List<Intersection> inters = new ArrayList<Intersection>();
		inters.add(c1.intersection(s2));
		inters.add(c2.intersection(s2));
		inters.add(c3.intersection(s1));
		inters.add(c4.intersection(s1));
		for (int i = 0; i < inters.size(); i++){
			if (inters.get(i).getList() != null){
				if (inters.get(i).getList().length() > 0){
					for (int j = 0; j < inters.get(i).getList().length(); j++){
						Point2 q = (Point2)(inters.get(i).getList().getValueAt(j));
						Point p = new Point(q.getX(), q.getY());
						interPoints.addPoint(p);
					}
					t = new Point(circs.get(i).centre.getX(), circs.get(i).centre.getY());
					
					setCurrentTurnpike(t);
					for(int j = 0; j < interPoints.getSize(); j++){
						if (!interPoints.points.get(j).equals(t)) {
							f = interPoints.points.get(j);
							setCurrentFacility(f);
							break; // break for
						}
					} break; // break for
				}
			}
		}
		
		
		
	}
	
	//TODO method that takes an Intersection object and returns a pointlist instead
	//TODO solvePointToRectBP()
	
	/**
	 * 
	 * @param centers1
	 * 			input list of 4 points which define edges of rectangle
	 * @param centers2
	 * 			input list of 2 points which define line segment
	 */
	public void solveLineToRectBP(PointList centers1, PointList centers2){
		
		Point t = new Point();
		Point f = new Point();
		
		PointList interPoints = new PointList(Color.CYAN);
		
		Rectangle2 rect = new Rectangle2((float)centers1.points.get(0).posX, (float)centers1.points.get(0).posY,
				(float)(Math.abs(centers1.points.get(2).posX-centers1.points.get(0).posX)), 
				(float)(Math.abs(centers1.points.get(1).posY-centers1.points.get(0).posY)));
		// edges of rectangle
		Point2 p11 = new Point2(rect.getX(), rect.getY()); // upper left corner
		Point2 p12 = new Point2(rect.getX() + rect.width, rect.getY()); // upper right
		Point2 p13 = new Point2(rect.getX() + rect.width, rect.getY() + rect.height);//lower right
		Point2 p14 = new Point2(rect.getX(), rect.getY() + rect.height); // lower left
		// Circles around rectangle
		Circle2 c1 = new Circle2(p11, (float)highwayLength);
		Circle2 c2 = new Circle2(p12, (float)highwayLength);
		Circle2 c3 = new Circle2(p13, (float)highwayLength);
		Circle2 c4 = new Circle2(p14, (float)highwayLength);
		
		// end points of line
		Point2 p21 = new Point2(centers2.points.get(0).posX, centers2.points.get(0).posY);
		Point2 p22 = new Point2(centers2.points.get(1).posX, centers2.points.get(1).posY);
		Segment2 seg = new Segment2(p21, p22);
		// Circles around line
		Circle2 c5 = new Circle2(p21, (float)highwayLength);
		Circle2 c6 = new Circle2(p22, (float)highwayLength);
		
		List<Circle2> circs = new ArrayList<Circle2>();
		circs.add(c1);
		circs.add(c2);
		circs.add(c3);
		circs.add(c4);
		circs.add(c5);
		circs.add(c6);
		
		// check intersection from rect circles with line
		List<Intersection> inters = new ArrayList<Intersection>();
		inters.add(c1.intersection(seg));
		inters.add(c2.intersection(seg));
		inters.add(c3.intersection(seg));
		inters.add(c4.intersection(seg));
		for (int i = 0; i < inters.size(); i++){
			if (inters.get(i).getList() != null){
				if (inters.get(i).getList().length() > 0){
					for (int j = 0; j < inters.get(i).getList().length(); j++){
						Point2 q = (Point2)(inters.get(i).getList().getValueAt(j));
						Point p = new Point(q.getX(), q.getY());
						interPoints.addPoint(p);
					}
					t = new Point(circs.get(i).centre.getX(), circs.get(i).centre.getY());
					
					setCurrentTurnpike(t);
					for(int j = 0; j < interPoints.getSize(); j++){
						if (!interPoints.points.get(j).equals(t)) {
							f = interPoints.points.get(j);
							setCurrentFacility(f);
							break; // break for
						}
					} break; // break for
				}
				
			}
		}
		
		
		// check intersection from line circles with rect
		if (interPoints.points.size() == 0){
			if(circleRectInt(c5, rect).points.size() > 0){
				interPoints = circleRectInt(c5, rect);
				t = new Point(c5.centre.getX(), c5.centre.getY()); // circle center
				setCurrentTurnpike(t);
				for(int i = 0; i < interPoints.getSize(); i++){
					if (!interPoints.points.get(i).equals(t)){
						f = interPoints.points.get(i);
						setCurrentFacility(f);
						break; // break for
					}
				}
			} else if(circleRectInt(c6, rect).points.size() > 0){
				t = new Point(c6.centre.getX(), c6.centre.getY()); // circle center
				setCurrentTurnpike(t);
				for(int i = 0; i < interPoints.getSize(); i++){
					if (!interPoints.points.get(i).equals(t)){
						f = interPoints.points.get(i);
						setCurrentFacility(f);
						break; // break for
					}
				}
			}
		}
	}
	
	public void solveRectToRectBP(PointList centers1, PointList centers2){
		
		Point t = new Point();
		Point f = new Point();
		
		List<PointList> inters = new ArrayList<PointList>();
		PointList interPoints = new PointList(Color.CYAN);
		
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
		
		Circle2 c1 = new Circle2(p11, (float)highwayLength);
		Circle2 c2 = new Circle2(p12, (float)highwayLength);
		Circle2 c3 = new Circle2(p13, (float)highwayLength);
		Circle2 c4 = new Circle2(p14, (float)highwayLength);
		
		//Rect2
		Point2 p21 = new Point2(rect2.getX(), rect2.getY()); // upper left corner
		Point2 p22 = new Point2(rect2.getX() + rect2.width, rect2.getY()); // upper right
		Point2 p23 = new Point2(rect2.getX() + rect2.width, rect2.getY() + rect2.height);//lower right
		Point2 p24 = new Point2(rect2.getX(), rect2.getY() + rect2.height); // lower left
		
		Circle2 c5 = new Circle2(p21, (float)highwayLength);
		Circle2 c6 = new Circle2(p22, (float)highwayLength);
		Circle2 c7 = new Circle2(p23, (float)highwayLength);
		Circle2 c8 = new Circle2(p24, (float)highwayLength);
		
		List<Circle2> circs = new ArrayList<Circle2>();
		circs.add(c1);
		circs.add(c2);
		circs.add(c3);
		circs.add(c4);
		circs.add(c5);
		circs.add(c6);
		circs.add(c7);
		circs.add(c8);
		
		// calculate all intersection points between all circles and rects
		inters.add(circleRectInt(c1, rect2));
		inters.add(circleRectInt(c2, rect2));
		inters.add(circleRectInt(c3, rect2));
		inters.add(circleRectInt(c4, rect2));
		inters.add(circleRectInt(c5, rect1));
		inters.add(circleRectInt(c6, rect1));
		inters.add(circleRectInt(c7, rect1));
		inters.add(circleRectInt(c8, rect1));
		
		for (int i = 0; i < inters.size(); i++){
			if (inters.get(i).getSize() > 0){
				for (int j = 0; j < inters.get(i).getSize(); j++){
					Point p = inters.get(i).points.get(j);
					interPoints.addPoint(p);
				}
				t = new Point(circs.get(i).centre.getX(), circs.get(i).centre.getY());
			
				setCurrentTurnpike(t);
				for(int j = 0; j < interPoints.getSize(); j++){
					if (!interPoints.points.get(j).equals(t)) {
						f = interPoints.points.get(j);
						setCurrentFacility(f);
						break; // break for
					}
				} break; // break for
			}
			
		}
		
	}
		
	//TODO set currentradius
	
	public void solveBasicProblem(PointList list1, PointList list2){
		
		double e1 = Math.max(0, list1.delta() + highwayLength/velocity - list2.delta());
		double e2 = Math.max(0, list2.delta() - list1.delta() - highwayLength/velocity);
		
		x = getBPradius(list1, list2, 0, maxDist);
		
		PointList centers1 = center(list1, list1.delta() + e2 + x); // Center(H, d(H)+e2+x)
		PointList centers2 = center(list2, list2.delta() + e1 + x); // Center(W, d(W)+e1+x)
		
		if (centers1.getSize() == 1 && centers2.getSize() == 1){
			solvePointToPointBP(centers1, centers2);
		} else if (centers1.getSize() == 1 && centers2.getSize() == 2){
			solvePointToLineBP(centers1, centers2);
		} else if (centers1.getSize() == 2 && centers2.getSize() == 1){
			solvePointToLineBP(centers2, centers1);
		} else if (centers1.getSize() == 2 && centers2.getSize() == 2){
			solveLineToLineBP(centers1, centers2);
		} else if (centers1.getSize() == 4 && centers2.getSize() == 2){
			solveLineToRectBP(centers1, centers2);
		} else if (centers1.getSize() == 2 && centers2.getSize() == 4){
			solveLineToRectBP(centers2, centers1);
		} else if (centers1.getSize() == 4 && centers2.getSize() == 4){
			solveRectToRectBP(centers1, centers2);
		}
	}
	
	public void solveBP(PointList list1, PointList list2){
		double e1 = Math.max(0, list1.delta() + highwayLength/velocity - list2.delta());
		double e2 = Math.max(0, list2.delta() - list1.delta() - highwayLength/velocity);
		
		x = getBPradius(list1, list2, 0, maxDist);
		
		PointList centers1 = center(list1, list1.delta() + e2 + x); // Center(H, d(H)+e2+x)
		PointList centers2 = center(list2, list2.delta() + e1 + x); // Center(W, d(W)+e1+x)
		
		Point l1Start = new Point();
		Point l1End = new Point();
		Point l2Start = new Point();
		Point l2End = new Point();
		
		Point[] minDistPoints = minDistPoints(centers1, centers2);
		Point[] maxDistPoints = maxDistPoints(centers1, centers2);
		
		// find correct lines
		if (contains(centers1, minDistPoints[0])){
			l1Start = minDistPoints[0];
			l2Start = minDistPoints[1];
		} else {
			l1Start = minDistPoints[1];
			l2Start = minDistPoints[0];
		} 
		
		l1End = maxDistPoints[0];
		l2End = maxDistPoints[1];
		
		Point f = new Point();
		Point t = new Point(); 
		
		// parameterize lines
		Double[] v1 = getDirectionVector(l1Start, l1End);
		Double[] v2 = getDirectionVector(l2Start, l2End);
		
		double r = getParam(l1Start, l2Start, v1, v2);
		
		f = new Point(l1Start.posX + r * v1[0], l1Start.posY + r * v1[1]);
		t = new Point(l2Start.posX + r * v2[0], l2Start.posY + r * v2[1]);
		
		setCurrentFacility(f);
		setCurrentTurnpike(t);
		 
	}
	
	public boolean contains(PointList list, Point p){
		// returns true if the object defined by list contains p

		boolean contains = false;
		Point2 point = new Point2(p.posX, p.posY);
		
		if (list.getSize() == 1){
			if (list.points.get(0).equals(p)) contains = true;
		} else if (list.getSize() == 2){
			Point2 p1 = new Point2(list.points.get(0).posX, list.points.get(0).posY);
			Point2 p2 = new Point2(list.points.get(1).posX, list.points.get(1).posY);
			Segment2 seg = new Segment2(p1, p2);
			if(seg.liesOn(point)) contains = true;
		} else if (list.getSize() == 4){
			Point p1 = new Point(list.points.get(0).posX, list.points.get(0).posY);
			Point p2 = new Point(list.points.get(1).posX, list.points.get(1).posY);
			Point p3 = new Point(list.points.get(2).posX, list.points.get(2).posY);
			Point p4 = new Point(list.points.get(3).posX, list.points.get(3).posY);
			Rect r = new Rect(p1, p3, p2, p4);
			if (r.contains(p)) contains = true;
		}
		
		return contains;
	}
	
	
	public double getParam(Point p1, Point p2, Double[] v1, Double[] v2){
		double r;
		
		double l = highwayLength;
		
		// auxiliary variables
		double A = (Math.pow(v1[0] - v2[0], 2.0)) + (Math.pow(v1[1] - v2[1], 2.0));
		double B = (2 * (p1.posX - p2.posX) * (v1[0] - v2[0])) + (2 * (p1.posY - p2.posY) * (v1[1] - v2[1]));
		double C = (Math.pow(p1.posX - p2.posX, 2.0)) + (Math.pow(p1.posY - p2.posY, 2.0));
		
		// using pq formula to find r
		double phalf = B / (2 * A);
		double q = ((C - (l * l)) / A);
		double r1 = - phalf + Math.sqrt((phalf * phalf) - q);
		double r2 = - phalf - Math.sqrt((phalf * phalf) - q);
		
		if (r1 >= 0 && r1 <= 1) r = r1;
		else r = r2;
		
		return r;
	}
	
	public Double[] getDirectionVector(Point p, Point q){
		// returns the direction vector from point p to point q
		// v= q-p
		Double[] v = new Double[2];
		
		v[0] = q.posX - p.posX;
		v[1] = q.posY - p.posY;
		
		return v;
		
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
			if(rectContainsRect(r1, r2)){
				minDist = 0;
			} else {
				Point[] points = minDistPoints(list1, list2);
				minDist = Math.sqrt(points[0].distanceSquaredTo(points[1]));
			}
		}
		
		return minDist;
		
	}
	
	public Point[] minDistPoints(PointList list1, PointList list2){

		// returns points on list1 and list2 which build
		// minimum distance between two objects defined by list1 and list2
		// minDistPoints[0] of list1, minDistPoints[1] of list2: needs to be checked!!!!
		
		Point[] minDistPoints = new Point[2];
		
		double minDist = Double.POSITIVE_INFINITY;
		
		if (list1.getSize() == 1 && list2.getSize() == 1){
			// minimum distance between two points
			Point p1 =  new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p2 =  new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			minDistPoints[0] = p1;
			minDistPoints[1] = p2;
		} else if (list1.getSize() == 1 && list2.getSize() == 2){
			// minimum distance between a point and a line segment
			Point2 p1 =  new Point2(list1.points.get(0).posX, list1.points.get(0).posY);
			Point2 p21 = new Point2(list2.points.get(0).posX, list2.points.get(0).posY);
			Point2 p22 = new Point2(list2.points.get(1).posX, list2.points.get(1).posY);
			Segment2 l2 = new Segment2(p21, p22);
			Point2 p = l2.closestPoint(p1);
			minDistPoints[0] = new Point(p1.getX(), p1.getY());
			minDistPoints[1] = new Point(p.getX(), p.getY());
		} else if (list1.getSize() == 2 && list2.getSize() == 1){
			// minimum distance between a point and a line segment
			Point2 p11 = new Point2(list1.points.get(0).posX, list1.points.get(0).posY);
			Point2 p12 = new Point2(list1.points.get(1).posX, list1.points.get(1).posY);
			Point2 p2 =  new Point2(list2.points.get(0).posX, list2.points.get(0).posY);
			Segment2 l1 = new Segment2(p11, p12);
			Point2 p = l1.closestPoint(p2);
			minDistPoints[0] = new Point(p.getX(), p.getY());
			minDistPoints[1] = new Point(p2.getX(), p2.getY());
		} else if (list1.getSize() == 2 && list2.getSize() == 2){
			// minimum distance between two line segments
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			LineSegment l1 = new LineSegment(p11, p12);
			LineSegment l2 = new LineSegment(p21, p22);
			minDistPoints = l1.minDistPointsTo(l2);
		} else if (list1.getSize() == 4 && list2.getSize() == 1){
			// minimum distance between a rectangle and a point
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p13 = new Point(list1.points.get(2).posX, list1.points.get(2).posY);
			Point p2 =  new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point2 p = new Point2(p2.posX, p2.posY);
			Rectangle2 r1 = new Rectangle2((float)p11.posX, (float)p11.posY, (float)(Math.abs(p13.posX - p11.posX)), (float)(Math.abs(p12.posY - p11.posY)));

			Segment2 s1 = r1.top();
			Segment2 s2 = r1.bottom();
			Segment2 s3 = r1.left();
			Segment2 s4 = r1.right();
			
			List<Point2> points = new ArrayList<Point2>();
			points.add(s1.closestPoint(p));
			points.add(s2.closestPoint(p));
			points.add(s3.closestPoint(p));
			points.add(s4.closestPoint(p));

			for (int i = 0; i < points.size(); i++){
				if (p.distance(points.get(i)) < minDist) {
					minDist = p.distance(points.get(i));
					Point2 q = points.get(i);
					minDistPoints[0] = new Point(q.getX(), q.getY());
				}
			}
			minDistPoints[1] = new Point(p2.getX(), p2.getY());
			
		} else if (list1.getSize() == 1 && list2.getSize() == 4){
			// minimum distance between a rectangle and a point
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Point p23 = new Point(list2.points.get(2).posX, list2.points.get(2).posY);
			Point p1 =  new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point2 p = new Point2(p1.posX, p1.posY);
			Rectangle2 r2 = new Rectangle2((float)p21.posX, (float)p21.posY, (float)(Math.abs((p23.posX - p21.posX))), (float)(Math.abs((p22.posY - p21.posY))));
		
			Segment2 s1 = r2.top();
			Segment2 s2 = r2.bottom();
			Segment2 s3 = r2.left();
			Segment2 s4 = r2.right();
			
			List<Point2> points = new ArrayList<Point2>();
			points.add(s1.closestPoint(p));
			points.add(s2.closestPoint(p));
			points.add(s3.closestPoint(p));
			points.add(s4.closestPoint(p));

			for (int i = 0; i < points.size(); i++){
				if (p.distance(points.get(i)) < minDist) {
					minDist = p.distance(points.get(i));
					Point2 q = points.get(i);
					minDistPoints[1] = new Point(q.getX(), q.getY());
				}
			}
			minDistPoints[0] = new Point(p1.getX(), p1.getY());
			
		} else if (list1.getSize() == 2 && list2.getSize() == 4){
			// minimum distance between a rectangle and a line segment
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Point p23 = new Point(list2.points.get(2).posX, list2.points.get(2).posY);
			LineSegment l1 = new LineSegment(p11, p12);
			Rectangle2 r2 = new Rectangle2((float)p21.posX, (float)p21.posY, (float)(Math.abs((p23.posX - p21.posX))), (float)(Math.abs(p22.posY - p21.posY)));
			minDistPoints = l1.minDistPointsTo(r2);
		} else if (list1.getSize() == 4 && list2.getSize() == 2){
			// minimum distance between a rectangle and a line segment
			Point p11 = new Point(list1.points.get(0).posX, list1.points.get(0).posY);
			Point p12 = new Point(list1.points.get(1).posX, list1.points.get(1).posY);
			Point p13 = new Point(list1.points.get(2).posX, list1.points.get(2).posY);
			Point p21 = new Point(list2.points.get(0).posX, list2.points.get(0).posY);
			Point p22 = new Point(list2.points.get(1).posX, list2.points.get(1).posY);
			Rectangle2 r1 = new Rectangle2((float)p11.posX, (float)p11.posY, (float)(Math.abs((p13.posX - p11.posX))), (float)(Math.abs((p12.posY - p11.posY))));
			LineSegment l2 = new LineSegment(p21, p22);
			minDistPoints = l2.minDistPointsTo(r1);
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
			//TODO
			if(rectContainsRect(r1, r2)){
			}
			minDistPoints = minDistPointsRects(r1, r2);
		}
		
		return minDistPoints;
	}
	
	public double maxDist(PointList list1, PointList list2){
		
		// returns max distance between p in list 1 and q in list 2

		double maxDist = Double.NEGATIVE_INFINITY;
		List<Double> distances = new ArrayList<Double>();
		
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
	/**
	 * 
	 * @param list1
	 * @param list2
	 * @return Point[] maxDistPoints
	 * 		maxDistPoints[0] on list1, maxDistPoints[1] on list2
	 */
	public Point[] maxDistPoints(PointList list1, PointList list2){
		
		double maxDist = Double.NEGATIVE_INFINITY;
		
		Point[] maxDistPoints = new Point[2]; // Point[0] on list1, Point[1] on list2
		Double distance;
		
		for (int i = 0; i < list1.getSize(); i++){
			for (int j = 0; j < list2.getSize(); j++){
				
				distance = Math.sqrt(list1.points.get(i).distanceSquaredTo(list2.points.get(j)));
				
				if (distance > maxDist){
					maxDist = distance;
					maxDistPoints[0] = list1.getPoints().get(i);
					maxDistPoints[1] = list2.getPoints().get(j);
				}
			}
		}
		
		return maxDistPoints;
		
	}
	
	public double maxDistInSet(PointList S){
		// returns max distance between two points in set S
		double maxDist = Double.NEGATIVE_INFINITY;
		List<Double> distances = new ArrayList<Double>();
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
	
	public Point[] minDistPointsRects(Rectangle2 rect1, Rectangle2 rect2){
		
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
	
	public double rectDistanceToRect(Rectangle2 rect1, Rectangle2 rect2){
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
		
		List<Double> distances = new ArrayList<Double>();
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
	
	public boolean rectContainsCircle(Circle2 circ, Rectangle2 rect){
		// returns true if the circle is fully contained in rect
		/*
		 * p11---p15---p12
		 * |			|
		 * |			|
		 * p17		   p18
		 * |			|
		 * |			|
		 * p14---p16---p13
		 */
		
		
		
		float r = circ.radius;
		Point2 circMid = circ.centre;
		Point2 p11 = new Point2(rect.getX(), rect.getY()); // upper left corner
		Point2 p12 = new Point2(rect.getX() + rect.width, rect.getY()); // upper right
		Point2 p13 = new Point2(rect.getX() + rect.width, rect.getY() + rect.height);//lower right
		Point2 p14 = new Point2(rect.getX(), rect.getY() + rect.height); // lower left
		Point2 p15 = new Point2(p11.getX() + 0.5*rect.width, p11.getY()); // point between p11 and p12
		Point2 p16 = new Point2(p14.getX() + 0.5*rect.width, p14.getY()); // point between p14 and p13
		Point2 p17 = new Point2(p11.getX(), p11.getY() + 0.5*rect.height); // point between p11 and p14
		Point2 p18 = new Point2(p12.getX(), p12.getY() + 0.5*rect.height); // point between p13 and p12
		if (rect.contains(circMid)
				&& (p11.distance(circMid) >= r) && (p12.distance(circMid) >= r) 
				&& (p13.distance(circMid) >= r) && (p14.distance(circMid) >= r) 
				&& (p15.distance(circMid) >= r) && (p16.distance(circMid) >= r)
				&& (p17.distance(circMid) >= r)&& (p18.distance(circMid) >= r)){
			return true;
		} 
		
		return false;
		
		
	}
	/**
	 * 
	 * @param circ
	 * @param rect
	 * @return PoinList object, list of intersection points between the circle and the rectangle. 
	 * 			If the rectangle contains the circle, then one point on the circle arc  
	 */
	public PointList circleRectInt(Circle2 circ, Rectangle2 rect){
		// returns intersection points between circ and rect on rect
		
		PointList interPoints = new PointList(Color.CYAN);

		// if circle is contained in rect, then return one point on circle arc
		if (rectContainsCircle(circ, rect)){
			interPoints.addPoint(new Point(circ.centre.getX() + circ.radius, circ.centre.getY()));
			return interPoints;
		}
		
		Segment2 s1 = rect.top();
		Segment2 s2 = rect.bottom();
		Segment2 s3 = rect.left();
		Segment2 s4 = rect.right();

		List<Intersection> inters = new ArrayList<Intersection>();
		inters.add(circ.intersection(s1));
		inters.add(circ.intersection(s2));
		inters.add(circ.intersection(s3));
		inters.add(circ.intersection(s4));
		
		for(Intersection intersection : inters){
			for(int i = 0; i < intersection.getList().length(); i++ ){
				Point2 q = (Point2)intersection.getList().getValueAt(i);
				Point p = new Point(q.getX(), q.getY());
				interPoints.addPoint(p);
			}
		}
		
		return interPoints;
		
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
			xLength = Math.abs(currentCenter.posX + radius - extrema[1].posX);
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
			yLength = Math.abs(currentCenter.posY + radius - extrema[3].posY);
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
