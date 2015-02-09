package OCOH;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the algorithm to solve the 1-Center and 1-Highway Problem
 * focussed on the 1-Center and 1-Turnpike problem. 
 * Input: Set of demand points S, length of turnpike, velocity of turnpike
 * Output: Optimal location of facility point f and the turnpike
 * 
 * Given a set of points S and a velocity v > 1, we are interested in locating
 * the facility point f and the highway h (turnpike) that minimizes the maximum
 * time needed to travel from a point p in S to f. We consider the highwaylength l 
 * to be fixed.
 * 
 * Implemented in the context of the Project Group "Computational Geometry" at the 
 * University of Bonn, 24.02.2015.
 * 
 * @author Sally Chau
 * 
 */
public class OCOHAlgorithm {

	//**************************************************************************
	// Public Variables 
	// (mainly for the purpose of drawing)
	//**************************************************************************
	
	public List<PointList> set_withTurnpike; // t 
	public List<PointList> set_withoutTurnpike; // f
	public List<Point[]> extremePoints1;
	public List<Point[]> extremePoints2;
	
	// set of points resulting from center()
	public List<PointList> list_centersWithoutTurnpike; 
	public List<PointList> list_centersWithTurnpike;
	
	// minDistPoints
	public List<Point> minDist1; 
	public List<Point> minDist2;
	
	// maxDistPoints
	public List<Point> maxDist1; 
	public List<Point> maxDist2;
	
	// variables describing the optimal location for the turnpike and the facility
	public int solutionIndex;
	public Point solution_facility;
	public Point solution_turnpikeStart;
	public double solution_radius;
	
	// variables describing all basic problem solutions for all partitions {W,H}
	public List<Point> facilityPoints;
	public List<Point> turnpikePoints;
	public List<Double> partitionRadius;

	//**************************************************************************
	// Private Variables
	//**************************************************************************
	
	// auxiliary variables
	private Point _currentFacility = new Point(); // turnpike endpoint 
	private Point _currentTurnpikeStart = new Point();
	private double _currentRadius = 0;
	private double _prevY = 0;
	private double _eps1, _eps2, _x = 0;
	private double _maxDist;
	
	// properties of turnpike to be located
	private int _highwayLength;
	private int _velocity;
		
	//**************************************************************************
	// Public Methods
	//**************************************************************************
	
	/**
	 * Starts the algorithm for solving the 1-Center and 1-Highway problem 
	 * for the version of locating a turnpike with fixed length. 
	 * This algorithm is solved in O(n^2) time.	
	 * 
	 * @param customers
	 * 			List of points (customers) 
	 * @param highwayLength
	 * 			Length of highway that should be located
	 * @param velocity
	 * 			Speed of highway that should be located: 
	 * 			The highway increases the transportation speed between any demand point and the facility.
	 */
	public void runAlgorithm(PointList customers, int highwayLength, int velocity) {
		
		if (!customers.isEmpty()){
			this._highwayLength = highwayLength;
			this._velocity = velocity;
			_maxDist = customers.maxDist();
			
			// for drawing purposes only
			facilityPoints = new ArrayList<Point>();
			turnpikePoints = new ArrayList<Point>();
			partitionRadius = new ArrayList<Double>();
			
			set_withTurnpike = new ArrayList<PointList>();
			set_withoutTurnpike = new ArrayList<PointList>();
			extremePoints1 = new ArrayList<Point[]>();
			extremePoints2 = new ArrayList<Point[]>();
			list_centersWithoutTurnpike = new ArrayList<PointList>();
			list_centersWithTurnpike = new ArrayList<PointList>();
			
			getPartition(customers);
			
			maxDist1 = new ArrayList<Point>();
			maxDist2 = new ArrayList<Point>();
			minDist1 = new ArrayList<Point>();
			minDist2 = new ArrayList<Point>();
			
			// compute extreme points
			for (PointList p : set_withTurnpike){
				extremePoints1.add(p.getExtremePoints());
			}
			for (PointList p : set_withoutTurnpike){
				extremePoints2.add(p.getExtremePoints());
			}
			
			// solve basic problem for all partitions {W,H}
			for (int i = 0; i < set_withTurnpike.size(); i++){

				_eps1 = Math.max(0, set_withTurnpike.get(i).delta() + highwayLength/velocity - set_withoutTurnpike.get(i).delta());
				_eps2 = Math.max(0, set_withoutTurnpike.get(i).delta() - set_withTurnpike.get(i).delta() - highwayLength/velocity);

				solveBP(set_withTurnpike.get(i), set_withoutTurnpike.get(i)); 
				facilityPoints.add(_currentFacility);
				turnpikePoints.add(_currentTurnpikeStart);
				partitionRadius.add(_currentRadius);
				
				// for drawing purposes
				// fixed length
				list_centersWithoutTurnpike.add(center(set_withTurnpike.get(i), set_withTurnpike.get(i).delta() + _eps2 + _x));
				list_centersWithTurnpike.add(center(set_withoutTurnpike.get(i), set_withoutTurnpike.get(i).delta() + _eps1 + _x));
				
				if (list_centersWithoutTurnpike.get(i).objectContains(list_centersWithoutTurnpike.get(i).objectMinDistPoints(list_centersWithTurnpike.get(i))[0])){
					minDist1.add(list_centersWithoutTurnpike.get(i).objectMinDistPoints(list_centersWithTurnpike.get(i))[0]); 
					minDist2.add(list_centersWithoutTurnpike.get(i).objectMinDistPoints(list_centersWithTurnpike.get(i))[1]);
				} else {
					minDist1.add(list_centersWithoutTurnpike.get(i).objectMinDistPoints(list_centersWithTurnpike.get(i))[1]); 
					minDist2.add(list_centersWithoutTurnpike.get(i).objectMinDistPoints(list_centersWithTurnpike.get(i))[0]);
				}
				maxDist1.add(list_centersWithoutTurnpike.get(i).objectMaxDistPoints(list_centersWithTurnpike.get(i))[0]); 
				maxDist2.add(list_centersWithoutTurnpike.get(i).objectMaxDistPoints(list_centersWithTurnpike.get(i))[1]);
			}	
			
			if(customers.getSize() > 1){
				// find optimal solution
				solutionIndex = getMinRadiusIndex(); 
				solution_facility = facilityPoints.get(solutionIndex);
				solution_turnpikeStart = turnpikePoints.get(solutionIndex);
//				solution_radius = partitionRadius.get(solutionIndex);
			}
			
		}
	}
	
	//**************************************************************************
	// Private Methods
	//**************************************************************************
	
	/**
	 * Solves the Basic Problem for two lists of points in constant time.
	 * Basic Problem: Given a partition {W,H} of S, find the smallest value R (called the radius of the partition)
	 * and the coordinates of f and t such that W \subseq B(f,R) and H \subseq B(t, R-l/v). When we consider
	 * the fixed-length variation of the problem, we also add the constraint that f and t must satisfy ||f-t||_2 = l.
	 * @param list1
	 * @param list2
	 */
	private void solveBP(PointList list1, PointList list2){
		double e1 = Math.max(0, list1.delta() + _highwayLength/_velocity - list2.delta());
		double e2 = Math.max(0, list2.delta() - list1.delta() - _highwayLength/_velocity);
		
		_x = getBPradius(list1, list2, 0, _maxDist);
		
		PointList centers1 = center(list1, list1.delta() + e2 + _x); // Center(H, d(H)+e2+x), t
		PointList centers2 = center(list2, list2.delta() + e1 + _x); // Center(W, d(W)+e1+x), f
		
		double radius = Math.max(list1.delta() + e2 + _x, list2.delta() + e1 + _x);
		
		Point l1Start = new Point();
		Point l1End = new Point();
		Point l2Start = new Point();
		Point l2End = new Point();
		
		Point[] minDistPoints = centers1.objectMinDistPoints(centers2);
		Point[] maxDistPoints = centers1.objectMaxDistPoints(centers2);
		
		// find correct lines
		
		if (centers1.objectContains(minDistPoints[0])){
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
		double[] v1 = l1Start.getDirectionVectorTo(l1End);
		double[] v2 = l2Start.getDirectionVectorTo(l2End);
		
		double r = getParam(l1Start, l2Start, v1, v2);
		
		t = new Point(l1Start.posX + r * v1[0], l1Start.posY + r * v1[1]);
		f = new Point(l2Start.posX + r * v2[0], l2Start.posY + r * v2[1]);
		
		setCurrentFacility(f);
		setCurrentTurnpike(t);
		setCurrentRadius(radius);
		 
	}

	/**
	 * Computes the smallest radius needed to place a turnpike 
	 * of length highwayLength as an input for the method 
	 * OCOH.OCOHAlgorithm.center(PointList T, double radius).
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
	private double getBPradius(PointList list1, PointList list2, double m, double M){
		
		double y = (m+M)/2;
		
		double e1 = Math.max(0, list1.delta() + _highwayLength/_velocity - list2.delta());
		double e2 = Math.max(0, list2.delta() - list1.delta() - _highwayLength/_velocity);
		
		PointList centers1 = center(list1, list1.delta() + e2 + y); // Center(H, d(H)+e2+x)
		PointList centers2 = center(list2, list2.delta() + e1 + y); // Center(W, d(W)+e1+x)
		
		// find maximum distance between centers1 and centers2
		double maxDist = centers1.objectMaxDist(centers2);
		
		// find minimum distance between centers1 and centers2
		double minDist = centers1.objectMinDist(centers2);
		
		if ((int)Math.abs(_prevY - y) == 0){
			return _prevY;
		}
		if (maxDist >= _highwayLength && minDist <= _highwayLength){
			_prevY = y;
			if ((int)Math.abs(M-m) == 0){
				return y;
			} else return getBPradius(list1, list2, m, y);
		} else {
			return getBPradius(list1, list2, y, M);
		}
		
	}
	
	/**
	 * Having found the correct size of balls and the sets of center points 
	 * where those balls can be located, we need to fulfill the constraint 
	 * for the fixed-length case that ||f-t||_2 = l.
	 * To find f and t, we parameterize the path between the minimum distance 
	 * and maximum distance points.
	 * Then we take the Euclidean distance and set it equal to l. 
	 * The next step is to find the needed parameter for points f and t 
	 * to have distance l.
	 * @param p1
	 * @param p2
	 * @param v1
	 * 		direction vector from minimum distance point to maximum distance point for the first path
	 * @param v2
	 * 		direction vector from minimum distance point to maximum distance point for the second path
	 * @return
	 * 		parameter for points f and t such that they have Euclidean distance l
	 */
	private double getParam(Point p1, Point p2, double[] v1, double[] v2){
		double r;
		
		double l = _highwayLength;
		
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
	
	/**
	 * After solving the Basic Problem for all partitions {W,H}
	 * we need to find the one solution with the smallest radius.
	 * @return smallest radius of all Basic problem solutions
	 */
	private int getMinRadiusIndex(){
		
		double minRadius = Double.POSITIVE_INFINITY;
		int radiusIndex = 0;
		
		for (double radius : partitionRadius){
			if (radius < minRadius) {
				minRadius = radius;
				radiusIndex = partitionRadius.indexOf(minRadius);
			}
		}
		
		return radiusIndex;
		
	}

	/**
	 * Calculates the locus of the centers of the axis-parallel squares (L_infty balls) 
	 * of radius r that cover T.
	 * We have:
	 * - center(T,r) = center(extreme(T),r)
	 * - center(T,r) can be empty, a point, an axis-parallel segment, or a rectangle
	 * - center(T,r) is empty iff r  delta(T).
	 * @param T
	 * 		set of points
	 * @param radius
	 * 		radius of the ball that should cover all points of T
	 * @return
	 * 		end points of the object containing all locus of centers. Therefore the PoinList object can contain
	 * 		0, 1, 2 or 4 points which define the object.
	 * 		If centers is a rectangle, the vertex points are returned in the following way:
	 * 		0----2
	 * 		|    |
	 * 		|    |
	 * 		1----3
	 *
	 */
	private PointList center(PointList T, double radius){
		
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
		
		if (extrema[0] == null) return centers; // centers is empty
		
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
			xEnd = currentCenter.posX;
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
			yEnd = currentCenter.posY;
		}
		
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
	
	/**
	 * Splits the customer set S into sets W and H, 
	 * such that they are either divided by an axis-aligned line or rectangle.
	 * @param customers
	 */
	private void getPartition(PointList customers){
		
		splitByQuadrant(customers);
		splitByLine(customers);
		
	}
	
	/**
	 * Splits the customer set S into sets W and H, 
	 * such that they are either divided by an axis-aligned rectangle.
	 * Sweep: Let q1, q2, ..., qn be the points of S sorted in decreasing order of y 
	 * coordinates. For any 1 < = i, j <= n, let UR_i,j and DL_i,j be the smallest bounding
	 * rectangles of the sets UR_i,j := {u in S| y(u) > y(qi) && x(u) < x(pj)} and S\UR_i,j
	 * respectively.
	 * @param S
	 */
	private void splitByQuadrant(PointList S){
		
		PointList[][] UR = new PointList[S.getSize()][S.getSize()];
		PointList[][] DL = new PointList[S.getSize()][S.getSize()];
		PointList Yincr = S;
		PointList X = S;
		PointList Y = new PointList();
		
		Collections.sort(X.points, Point.COMPARE_BY_XCoord); // p_i
		// sort S by y coordinates in decreasing order
		Collections.sort(Yincr.points, Point.COMPARE_BY_YCoord); // q_i
		for (int i = Yincr.getSize()-1; i > 0 ; i--){
				Y.addPoint(Yincr.points.get(i));
		}
		
		// initialize all point lists
		for (int i = 0; i < S.getSize(); i++){
			for (int j = 0; j < S.getSize(); j++){
				UR[i][j] = new PointList(Color.BLUE);
				DL[i][j] = new PointList(Color.RED);
			}
		}
		// find points in UR
		for (int u = 0; u < S.getSize(); u++){
			for (int i = 0; i < Y.getSize(); i++){
				for (int j = 0; j < X.getSize(); j++){
					if (S.points.get(u).posY > Y.points.get(i).posY && S.points.get(u).posX < X.points.get(j).posX){
						UR[i][j].addPoint(S.points.get(u));
					} else {
						DL[i][j].addPoint(S.points.get(u));
					}
					if (!contains(set_withTurnpike, UR[i][j]) && UR[i][j].getSize() > 0){
						
						set_withTurnpike.add(UR[i][j]);
						set_withoutTurnpike.add(DL[i][j]);
					}
				}
			}
		}
		
	}
	
	/**
	 * Splits the customer set S into sets W and H, 
	 * such that they are either divided by an axis-aligned line.
	 * Sweep: Sort the points of S in increasing value of y coordinates; 
	 * let p1, p2, ..., pn be the obtained order. For any 1 <= i < n,
	 * let L_i and R_i be the smallest bounding axis-aligned rectangle containing 
	 * points {p1, ..., pi} and {pi+1,...,pn}, respectively.
	 * The computationally most expensive part of the algorithm is computing the 
	 * initial sorting of the points of S, which needs O(n log n) time. 
	 * @param S
	 */
	private void splitByLine(PointList S){

		PointList[] L = new PointList[S.getSize()-1];
		PointList[] R = new PointList[S.getSize()-1];

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

			set_withTurnpike.add(L[i]);
			set_withoutTurnpike.add(R[i]);

		}
		
	}

	//**************************************************************************
	// Booleans
	//**************************************************************************
	
	/**
	 * Checks whether a point p is contained in a list.
	 * @param list
	 * @param pList
	 * @return
	 * 		true, if p is contained in list;
	 * 		false, if p is not contained in list.
	 */
	private boolean contains(List<PointList> list, PointList pList){
	
		for (PointList p : list){
			if (p.equals(pList)) return true;
		}
		
		return false;
	}
	
	//**************************************************************************
	// Setters
	//**************************************************************************
	
	/**
	 * Sets the current turnpike startpoint for the currently solved Basic problem.
	 * @param t
	 * 		location of the turnpike startpoint
	 */
	private void setCurrentTurnpike(Point t){
		
		_currentTurnpikeStart = t;
	
	}
	
	/**
	 * Sets the current facility point for the currently solved Basic problem.
	 * @param f
	 * 		location of the facility point
	 */		
	private void setCurrentFacility(Point f){
		
		_currentFacility = f;
	
	}
	
	/**
	 * Sets the current radius of the balls for the currently solved Basic problem.
	 * @param r
	 * 		current radius
	 */
	private void setCurrentRadius(double r){
		
		_currentRadius = r;
		
	}
	
}
