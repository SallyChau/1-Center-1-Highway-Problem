package OCOH;

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

	JButton startButton = new JButton("Run.");
	
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
	
	public PointList customersList = new PointList(Color.RED);
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
				try{
					highwayLength = Integer.parseInt(txtLength.getText());
					velocity = Integer.parseInt(txtVelocity.getText());
				}
				catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,
						    "Invalid Input!",
						    "Inane error",
						    JOptionPane.ERROR_MESSAGE);
				}
				runAlgorithm();
				repaint();
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
				runAlgorithm();
				System.out.println(stepCounter);
				System.out.println(algorithm.L.length);
				if(stepCounter < algorithm.L.length){
					algorithm.L[stepCounter].draw(g);
					algorithm.R[stepCounter].draw(g);
					stepCounter ++;
				}
				
			}});
	}


	public void clear() {
		customersList.clear();
//		algorithm.clear();
		defaultButtonSettings();
		repaint();
	}

	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		this.g = g;

		drawAllPoints();
		
	
	}

	
	public void drawAllPoints() {

		
		customersList.draw(g);
		
		// Draw a rectangle around the point which is selected
		if (dragged != null) {
			dragged.drawHighlight(g);
		}
		if(pointed != null){
			pointed.drawBoundings(g);
		}
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
		
		if(!pointAlreadyExists(p)){
			addPoint(p);
		}else{
			
			if(customersList.contains(p)){
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
			
			p.setColor(Color.RED);
			System.out.println(p.toString());
			customersList.addPoint(p);
					
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
//		if(startButton.isSelected()){
			algorithm.runAlgorithm(customersList, highwayLength, velocity);
		
//		}
	}
}
