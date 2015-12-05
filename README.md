# BA-INF 051 Projectgroup Computational Geometry
University of Bonn, 1st term 2015

Course Convener Priv.-Doz. Dr. Elmar Langetepe

In the context of the project group "Computational Geometry", I implemented the algorithm from J. M. Dıaz-Banez, M. Korman, P. Perez-Lantero, I. Ventura from the paper "The 1-Center and 1-Highway problem" (http://personal.us.es/dbanez/papers/carreteras-minmax.pdf). Furthermore, I designed and implemented a GUI to visualize the step-by-step process of the algorithm. The whole project is implemented in Java.

The 1-Center and 1-Highway Problem

We are given a set of points S. We aim to locate a highway h and a facility point f such that the maximum travel time from a point p in S is minimized. The highway is represented by a line segment in the plane with two endpoints t and t'. It has length l and a velocity v > 1. This means if a point p takes the highway on his way to the facility point f, his speed is going to be increased and thus his traveltime lowered. There are two types of highways which we call turnpike or freeway. The difference is that you can enter and leave a freeway from any point on the line segment allowing you to travel smaller distances than l via the highway. However, a turnpike is only accessible via the predefined endpoints of the highway leaving us to travel the complete highway. Including to keep the length of the highway fixed or variable, this setting leaves us with four different versions of the 1-Center and 1-Highway Problem: 

Locating a: 
- turnpike with fixed length
- tunrpike with variable length
- freeway with fixed length
- freeway with variable length

In the applet at hand we concentrated on implementing the algorithm for solving the problem of locating a turnpike of fixed length. This algorithm runs in O(n²) time. 
The main idea of the algorithm solving the 1-Center and 1-fixed-length-Turnpike problem is to calculate the turnpike position for different bipartitions of the pointset S. By finding the one position of the highway giving us the shortest maximum distance, we found the optimal turnpike location for the whole pointset S.
How to use the applet

Mouse functionalities

The left mouse click can be used to add new points and the right mouse to delete points.

Toolbar

The panel on the left hand side is the main control panel. We can control main settings here:
Choosing the type of the highway we want to locate (this function, however, is not enabled for the current version of the applet),
Manipulation of the length and velocity of the highway,
Starting the animation by pressing the arrow button,
Showing the step by step algorithm by clicking the previous and next buttons.
The Checkboxes have the following functionalities, when selected:
Customers: Displays the customer points on the screen as black circle points.
Solution: Displays the solution of the current set of points: 
Showing the optimal solution of the highway as a line segment as well as the start point of the highway and facility point. The facility point is displayed as a green circle point.
Algorithm steps: Displays the current step of the algorithm: 
It shows the current location of the highway of the currently selected partition of the pointset. 
Here, a blue coloured point indicates that this point uses the turnpike to reach the facility point, while the red coloured points reach the facility point without passing the turnpike.
Centers: Displays the possible positions of the midpoints of squares covering the area of points which do/do not use the turnpike. 
This functionality is mainly used for understanding the background of each algorithm step.
Bounding boxes: Displays the tightest axis-aligned bounding box for the partitionsets. 
This functionality is mainly used for understanding the background of each algorithm step.
