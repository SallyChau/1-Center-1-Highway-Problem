package OCOH;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OCOHAlgorithm {
	
	// case a) and b)
		PointList[][] L_R;
		PointList[] L;
		PointList[] R;
		
		// case c)
		PointList[][][] UR_DL;
		PointList[][] UR;
		PointList[][] DL;
		
		// Extreme Points of L and R
		Point[][] XL;
		Point[][] XR;
		
		// Extreme Points of UR_i,j and DL_i,j
		Point[][][] XUR;
		Point[][][] XDL;
	
	public void runAlgorithm(PointList customers, int highWayLength, int velocity) {
		
		System.out.println("Algo running!");
		
		// case a) and b)
		L_R = splitByLine(customers).clone();
		L = L_R[0];
		R = L_R[1];
		
		// case c)
		UR_DL = splitByQuadrant(customers).clone();
		UR = UR_DL[0];
		DL = UR_DL[1];
		
		// Extreme Points of L and R
		XL = new Point[customers.getSize()-1][4];
		XR = new Point[customers.getSize()-1][4];
		
		// Extreme Points of UR_i,j and DL_i,j
		XUR = new Point[customers.getSize()][customers.getSize()][4];
		XDL = new Point[customers.getSize()][customers.getSize()][4];
		
		// Find extreme points for all sets L_i, R_i !!!!!!!!!!!!CHECK CORRECTNESS!!!!!!!!!!
		for (int i = 0; i < customers.getSize()-1; i++){
			
			XL[i] = L[i].getExtremePoints();
			XR[i] = R[i].getExtremePoints();
		
			for (int k = 0; k < 4; k++){
				System.out.println();
				System.out.println("XL[" + i + "] =" +XL[i][k]);
				System.out.println("XR[" + i + "] =" +XR[i][k]);
			}
			
		}
		
		// case c)
		// Find extreme points for all sets UR_i,j
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
	
		
	}
	
	public BasicProblemSolution solveBasicProblem(PointList list1, PointList list2, double highWayLength, double velocity){
		
		// solve basic problem for all pairs (L[i],R[i])
		// solve basic problem for all pairs for case c)
		
		double e1; 
		double e2;
		
		PointList centers1;
		PointList centers2;
		
		double x = 0;
		
		Point facility = new Point(); // turnpike endpoint
		Point turnpikeStart = new Point();
		double radius = 0;
		
		double l = Math.pow(highWayLength, 2.0);
		double d = 0; // ||f-t||_2
		boolean lengthFound = false;
		
		e1 = Math.max(0, list1.delta() + highWayLength/velocity - list2.delta());
		e2 = Math.max(0, list2.delta() - list1.delta() - highWayLength/velocity);
		
		while(!lengthFound){
			centers1 = center(list1, list1.delta() + e2 + x); // Center(H, d(H)+e2+x)
			centers2 = center(list2, list2.delta() + e1 + x); // Center(W, d(W)+e1+x)
			
			// centers.size <= 4 -> constant size problem
			for (int i = 0; i < centers1.getSize(); i++){
				for (int j = 0; j < centers2.getSize(); j++){
					// checks vertices of Center() to find fitting radius (fitting point x)
					d = centers1.points.get(i).distanceSquaredTo(centers2.points.get(j));
					if (d >= l) // == ? { 
						turnpikeStart = centers1.points.get(i);
						facility = centers2.points.get(j);
						radius = Math.max(list1.delta() + e2 + x, list2.delta() + e1 + x); // is MAX necessary?
						lengthFound = true;
					}
				}
			x++;
		}
		
		// consider case where d > l ??
			return new BasicProblemSolution(facility, turnpikeStart, radius);
	
		
		}
		
		
	//	if (facility.distanceSquaredTo(turnpikeStart) == highWayLength){
//			return facility, turnpikeStart and Radius
//		}

	
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
	
	
	public PointList[][][] splitByQuadrant(PointList S){
		
		PointList[][][] UR_DL = new PointList[2][S.getSize()][S.getSize()];
		
		PointList Y = S;
		PointList X = S;

		PointList UR[][] = new PointList[S.getSize()][S.getSize()];
		PointList DL[][] = new PointList[S.getSize()][S.getSize()];
		
		Collections.sort(Y.points, Point.COMPARE_BY_YCoord); // q_i
		Collections.sort(X.points, Point.COMPARE_BY_XCoord); // p_i
		
		// initialize all point lists
		for (int i = 0; i < S.getSize(); i++){
			for (int j = 0; j < S.getSize(); j++){
				UR[i][j] = new PointList();
				DL[i][j] = new PointList();
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
		
		UR_DL[0] = UR;
		UR_DL[1] = DL;
		
		return UR_DL;
	}
	
	public PointList[][] splitByLine(PointList S){

		// case a) and b)
		PointList[][] LR = new PointList[2][S.getSize()-1];

		// initialize all point lists
		for (int i = 0; i < S.getSize()-1; i++){
				LR[0][i] = new PointList(Color.BLUE);
				LR[1][i] = new PointList(Color.RED);
		}
		
		Collections.sort(S.points, Point.COMPARE_BY_XCoord);
		// Divide into L_i, R_i for all 1<=i<n
		for (int i = 0; i < S.getSize()-1; i++){
			for (int k = 0; k < i+1; k++){
				LR[0][i].addPoint(S.points.get(k));	
			}
			for (int j = i+1; j < S.getSize(); j++){
				LR[1][i].addPoint(S.points.get(j));
			}
//
//			System.out.println("L[" + i + "] =" +LR[0][i].toString());
//			System.out.println("R[" + i + "] =" +LR[1][i].toString());
		}
		
		return LR;
		
	}
	

	
}
