package OCOH;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.crypto.spec.GCMParameterSpec;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


import javax.swing.JTextField;
import javax.swing.border.Border;

import anja.swinggui.JRepeatButton;
import anja.util.GraphicsContext;
import appsStandalone.fcvd.voronoi.VoronoiDiagram;
import appsSwingGui.topoVoro.Arc2_Sweep;
import appsSwingGui.topoVoro.dcel.DCEL_Edge;
import appsSwingGui.topoVoro.dcel.DCEL_Face;

public class OCOHGUI extends JPanel {

	int counterCells;
	Point dragged;
	Point pointed;
	
	int stepCounter = 0; 
	int stepCounter1 = 0;
	
	Listener l = new Listener();

	private static final long serialVersionUID = 1L;
	
	JPanel eastPanel = new JPanel();
	
	JPanel westPanel = new JPanel();
	
	JPanel screenPanel = new JPanel();
	
    JLabel modesLabel = new JLabel("MODE 1C1H");
    
    JLabel underlineModeLabel = new JLabel("________________");

	JLabel highwayLabel = new JLabel("Highway: ");

	String [] strHighway = {"Turnpike", "Freeway"};
	JComboBox highwayBox = new JComboBox(strHighway);
	
	private JTextField txtLength = new JTextField("100");
	
	JCheckBox fixedLengthCheckBox = new JCheckBox("fixed");
	
	JLabel velocityLabel = new JLabel("Velocity: ");

	JTextField txtVelocity = new JTextField("2");

    JLabel examplesLabel = new JLabel("Examples:");
    
	String [] strArray = {"Set points ...", "Example 1", "Example 2", "Example 3"};
	JComboBox examplesBox = new JComboBox(strArray);
	
    JLabel algoLabel = new JLabel("ALGORITHM");
    
    JLabel underlineAlgoLabel = new JLabel("________________");

	JCheckBox startButton = new JCheckBox("Run.");
	
	JLabel stepLabel = new JLabel("Step by step: ");

	JButton prevButton = new JButton("\u25c4");

	JButton nextButton = new JButton("\u25BA");
	
	JLabel showLabel = new JLabel("SHOW");
	    
	JLabel underlineShowLabel = new JLabel("________________");
	
	JCheckBox showBalls = new JCheckBox("Balls");
	
	JCheckBox showFacility = new JCheckBox("Facility");
	
	JCheckBox showHighway = new JCheckBox("Highway");
	
	JCheckBox showCustomers = new JCheckBox("Customers");
	
	ImageIcon zoomInIcon = createImageIcon("resources/zoomIn.png");
	
	ImageIcon zoomOutIcon = createImageIcon("resources/zoomOut.png");
	
	ImageIcon trashIcon = createImageIcon("resources/trash.png");
	
	JRepeatButton zoomInButton = new JRepeatButton();
	
	JRepeatButton zoomOutButton = new JRepeatButton();
	
	JButton clearButton = new JButton();
	
	public PointList customersList = new PointList();
	public int velocity;
	public int highwayLength;

	private Graphics g;

	
	OCOHAlgorithm algorithm;

	
	private static OCOHGUI panel;
	
	
	private OCOHGUI(OCOHAlgorithm algorithm) {
		this.algorithm = algorithm;
		
		defaultButtonSettings();
		createGUI();
		addMouseListener(MouseHandler.getMouseHandler(this));
		addMouseMotionListener(MouseHandler.getMouseHandler(this));
	}

	public static OCOHGUI getOCOHGUI(OCOHAlgorithm algorithm) {
		if (panel == null) {
			panel = new OCOHGUI(algorithm);
		}
		return panel;
	}

	public void defaultButtonSettings() {
		
		fixedLengthCheckBox.setSelected(true);
		showCustomers.setSelected(true);
		startButton.setSelected(true);
		
	}

	public void createGUI() {
		
		setLayout(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        westPanel.setLayout(new GridBagLayout());
        
        // Label "Mode"
        modesLabel.setFont(new Font("Default", Font.BOLD, 14));
        gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		westPanel.add(modesLabel, gbc);
		gbc.insets = new Insets(10,0,3,0);
		westPanel.add(underlineModeLabel, gbc);
		
		// Label "Highway"
        gbc.insets = new Insets(0,0,3,0);
		gbc.gridy = 1;
		westPanel.add(highwayLabel, gbc);
		
		// ComboBox for Highwaytypes
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 2;
		westPanel.add(highwayBox, gbc);
		
		// Label "Length"
		gbc.fill = GridBagConstraints.NONE;
		JLabel lengthLabel = new JLabel("Length: ");
		gbc.gridy = 3;
		westPanel.add(lengthLabel, gbc);
		
		// textfield for length
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 5;
		westPanel.add(txtLength, gbc);

		// Checkbox for length
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 4;
		westPanel.add(fixedLengthCheckBox, gbc);
		
		// Label "Velocity"
		gbc.gridy = 6;
		westPanel.add(velocityLabel, gbc);
		
		// textfield for velocity
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 7;
		westPanel.add(txtVelocity, gbc);
		
		gbc.gridy = 8;
		westPanel.add(new JLabel(""), gbc);
		
		// Label "Algorithm"
	    algoLabel.setFont(new Font("Default", Font.BOLD, 14));
	    gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 9;
		westPanel.add(algoLabel, gbc);
		gbc.insets = new Insets(10,0,3,0);
		westPanel.add(underlineAlgoLabel, gbc);

		// ComboBox for Examples
		gbc.insets = new Insets(0,0,3,0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 10;
		westPanel.add(examplesBox, gbc);
		
		// Run Button
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 11;
		westPanel.add(startButton, gbc);
		
		// Label "Step by Step"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 12;
		westPanel.add(stepLabel, gbc);
		
		// Buttons Step by Step
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 13;
		JPanel stepsPanel = new JPanel();
		stepsPanel.add(prevButton);
		stepsPanel.add(nextButton);
		westPanel.add(stepsPanel, gbc);
		
		gbc.gridy = 14;
		westPanel.add(new JLabel(""), gbc);
		
		// Label "Show"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 15;
		showLabel.setFont(new Font("Default", Font.BOLD, 14));
        gbc.anchor = GridBagConstraints.LINE_START;
		westPanel.add(showLabel, gbc);
		gbc.insets = new Insets(10,0,3,0);
		westPanel.add(underlineShowLabel, gbc);
		
		// checkboxes
		gbc.insets = new Insets(0,0,3,0);
		gbc.gridy = 16;
		westPanel.add(showCustomers, gbc);
		gbc.gridy = 17;
		westPanel.add(showFacility, gbc);
		gbc.gridy = 18;
		westPanel.add(showHighway, gbc);
		gbc.gridy = 19;
		westPanel.add(showBalls, gbc);
		
		screenPanel.setBackground(Color.WHITE);
		zoomInButton.setIcon(zoomInIcon);
		zoomInButton.setPreferredSize(new Dimension(26,26));
		screenPanel.add(zoomInButton);
		zoomOutButton.setIcon(zoomOutIcon);
		zoomOutButton.setPreferredSize(new Dimension(26,26));
		screenPanel.add(zoomOutButton);
		clearButton.setIcon(trashIcon);
		clearButton.setPreferredSize(new Dimension(26,26));
		screenPanel.add(clearButton);
		eastPanel.setOpaque(false);
		eastPanel.setLayout(new BorderLayout());
		eastPanel.setBackground(Color.WHITE);
		eastPanel.add(screenPanel, BorderLayout.NORTH);
		
		add(eastPanel, BorderLayout.EAST);
		add(westPanel, BorderLayout.WEST);
	
		registerListeners();
	}
	
	public static ImageIcon createImageIcon(String path){
		Image look = null;
		try{
			look = ImageIO.read(OCOHGUI.class.getClassLoader().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ImageIcon(look);
	}

	public void registerListeners() {

		// Register the listeners
		txtLength.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				highwayLength = Integer.parseInt(txtLength.getText());
			}
		});
		
		startButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
			}
		});
		
		clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}});
		
		nextButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(stepCounter < algorithm.L.length-1){
					stepCounter ++;
				}
				repaint();
				
//				algorithm.a++; //erhöht center radius
				
			}});
		
		prevButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(stepCounter > 0){
					stepCounter --;
				}
				repaint();
//				algorithm.a--; // erniedrigt center() radius
			}
				
		});
		
		
	}


	public void clear() {
		customersList.clear();
//		algorithm.clear();
		defaultButtonSettings();
		stepCounter = 0;
		repaint();
	}

	public void paintComponent(Graphics graph) {
		
		Graphics2D g = (Graphics2D) graph;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setStroke(new BasicStroke(1));
		super.paintComponent(graph);
		this.g = g;


		drawAllPoints();
	
		if (!customersList.isEmpty()){
			runAlgorithm();
			if(algorithm.L.length > 0 ){
				
//				for(int i = 0; i < customersList.getSize(); i++){
//					for(int j = 0; j < customersList.getSize(); j++){
//						algorithm.DL[stepCounter][j].draw(g);
//						algorithm.UR[stepCounter][j].draw(g);
					
//					 draw smallest axis-aligned bounding box
//					if (!algorithm.DL[stepCounter][j].isEmpty()){
//					g.setColor(Color.BLUE);
//					g.drawRect((int)algorithm.XDL[stepCounter][j][0].posX, (int)algorithm.XDL[stepCounter][j][2].posY,
//							(int)Math.abs(algorithm.XDL[stepCounter][j][0].posX - algorithm.XDL[stepCounter][j][1].posX), 
//							(int)Math.abs(algorithm.XDL[stepCounter][j][2].posY - algorithm.XDL[stepCounter][j][3].posY));
//					}
//					if (!algorithm.UR[stepCounter][j].isEmpty()){
//					g.setColor(Color.RED);
//					g.drawRect((int)algorithm.XUR[stepCounter][j][0].posX, (int)algorithm.XUR[stepCounter][j][2].posY,
//							(int)Math.abs(algorithm.XUR[stepCounter][j][0].posX - algorithm.XUR[stepCounter][j][1].posX), 
//							(int)Math.abs(algorithm.XUR[stepCounter][j][2].posY - algorithm.XUR[stepCounter][j][3].posY));
//					}
//					}
				
				algorithm.L[stepCounter].draw(g);
				algorithm.R[stepCounter].draw(g);
				
				// draw smallest axis-aligned bounding box
				g.setColor(Color.BLUE);
				g.drawRect((int)algorithm.XL[stepCounter][0].posX, (int)algorithm.XL[stepCounter][2].posY,
						(int)Math.abs(algorithm.XL[stepCounter][0].posX - algorithm.XL[stepCounter][1].posX), 
						(int)Math.abs(algorithm.XL[stepCounter][2].posY - algorithm.XL[stepCounter][3].posY));
				
				g.setColor(Color.RED);
				g.drawRect((int)algorithm.XR[stepCounter][0].posX, (int)algorithm.XR[stepCounter][2].posY,
						(int)Math.abs(algorithm.XR[stepCounter][0].posX - algorithm.XR[stepCounter][1].posX), 
						(int)Math.abs(algorithm.XR[stepCounter][2].posY - algorithm.XR[stepCounter][3].posY));

				
				// draw centers
				if (algorithm.c1[stepCounter].getSize() == 1) {
					algorithm.c1[stepCounter].draw(g);
				} else if (algorithm.c1[stepCounter].getSize() == 2){
					algorithm.c1[stepCounter].draw(g);
					g.drawLine((int)algorithm.c1[stepCounter].points.get(0).getX(),(int) algorithm.c1[stepCounter].points.get(0).getY(), 
							(int)algorithm.c1[stepCounter].points.get(1).getX(), (int)algorithm.c1[stepCounter].points.get(1).getY());
				} else if (algorithm.c1[stepCounter].getSize() == 4){
					algorithm.c1[stepCounter].draw(g);
					g.drawRect((int)algorithm.c1[stepCounter].points.get(0).getX(), (int)algorithm.c1[stepCounter].points.get(0).getY(), 
							Math.abs((int)algorithm.c1[stepCounter].points.get(2).getX()-(int)algorithm.c1[stepCounter].points.get(0).getX()), 
							Math.abs((int)algorithm.c1[stepCounter].points.get(1).getY())-(int)algorithm.c1[stepCounter].points.get(0).getY());
				}
				if (algorithm.c2[stepCounter].getSize() == 1) {
					algorithm.c2[stepCounter].draw(g);
				} else if (algorithm.c2[stepCounter].getSize() == 2){
					algorithm.c2[stepCounter].draw(g);
					g.drawLine((int)algorithm.c2[stepCounter].points.get(0).getX(),(int) algorithm.c2[stepCounter].points.get(0).getY(), 
							(int)algorithm.c2[stepCounter].points.get(1).getX(), (int)algorithm.c2[stepCounter].points.get(1).getY());
				} else if (algorithm.c2[stepCounter].getSize() == 4){
					algorithm.c2[stepCounter].draw(g);
					g.drawRect((int)algorithm.c2[stepCounter].points.get(0).getX(), (int)algorithm.c2[stepCounter].points.get(0).getY(), 
							Math.abs((int)algorithm.c2[stepCounter].points.get(2).getX()-(int)algorithm.c2[stepCounter].points.get(0).getX()), 
							Math.abs((int)algorithm.c2[stepCounter].points.get(1).getY())-(int)algorithm.c2[stepCounter].points.get(0).getY());
				}
					
					algorithm.facilityPointsLR[stepCounter].draw(g);
					algorithm.turnpikePointsLR[stepCounter].draw(g);
//					g.drawOval((int)(algorithm.circ.centre.getX()-algorithm.circ.radius), (int)(algorithm.circ.centre.getY()-algorithm.circ.radius),
//							(int)(2*algorithm.circ.radius), (int)(2*algorithm.circ.radius));
//					g.fillOval((int)(algorithm.circ.centre.getX()-2.5), (int)(algorithm.circ.centre.getY()-2.5),
//							5, 5);
//					
//					g.drawLine((int)algorithm.segm1.source().getX(), (int)algorithm.segm1.source().getY(), 
//							(int)algorithm.segm1.target().getX(), (int)algorithm.segm1.target().getY());
//					g.setColor(Color.GREEN);
//					g.drawLine((int)algorithm.segm2.source().getX(), (int)algorithm.segm2.source().getY(), 
//							(int)algorithm.segm2.target().getX(), (int)algorithm.segm2.target().getY());
//				
//				}
				
				
				
			
								
			}
		}
		
	
	}

	
	public void drawAllPoints() {


		
		if (showCustomers.isSelected()){
			customersList.draw(g);
			// Draw a rectangle around the point which is selected
			if (dragged != null) {
				dragged.drawHighlight(g);
			}
			if(pointed != null){
				pointed.drawBoundings(g);
			}
		}
		repaint();
	}


	public boolean pointAlreadyExists(Point p){
	
		if(customersList.contains(p)){
			return true;
		}
		return false;
	}
	
	public void moveMouse(Point p){
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		pointed = null;
		if(pointAlreadyExists(p)){
			pointed = searchPoint(p);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		repaint();
		
	}
	
	public void rightMouseClick(Point p){
		if(pointAlreadyExists(p)){
			
			deletePoint(p);
		}

		pointed = null;
//		algorithm.clear();
		buttonsCheck();
//		runAlgorithm();
		repaint();
	}
	
	public void buttonsCheck(){
		
	
	}
	
	public void leftMouseClick(Point p){
		
		if(!pointAlreadyExists(p) && showCustomers.isSelected()){
			addPoint(p);
		}else{
			
			if(customersList.contains(p) && showCustomers.isSelected()){
				dragged = customersList.search(p);
			}
			 // Highlight the clicked point
			repaint();
		}		

		buttonsCheck();
//		runAlgorithm();
	}
	
	public Point searchPoint(Point p){
		
	
		return customersList.search(p);
		
		
	}
	
	public void dragged(Point p){
		
		if(dragged != null && !collisionExists(p)){
			dragged.setPosX(p.getX());
			dragged.setPosY(p.getY());
			
		}
		runAlgorithm();
		repaint();
	}
	
	public void deletePoint(Point p){
		
		customersList.remove(p);
			
	}
	
	public boolean collisionExists(Point p){
		if(!(customersList.collisionExists(p))){
			return false;
		}
		return true;
	}
	
	public void addPoint(Point p){
				
		if(!collisionExists(p)){
			
			System.out.println(p.toString());
			customersList.addPoint(p);
			runAlgorithm();
					
		}
		repaint();
		moveMouse(p);
	}
	
	public int[] getScreenCoordinates(){
		
		int[] coordinates = {westPanel.getWidth(), panel.getWidth(), screenPanel.getWidth(), screenPanel.getHeight()};
		
		return coordinates;
		
	}
	
	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	}

	public void runAlgorithm(){
		
		if(startButton.isSelected()){
			try{
				highwayLength = Integer.parseInt(txtLength.getText());
				velocity = Integer.parseInt(txtVelocity.getText());
				repaint();
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null,
					    "Invalid Input!",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
			algorithm.runAlgorithm(customersList, highwayLength, velocity);
		
		}
	}
}
