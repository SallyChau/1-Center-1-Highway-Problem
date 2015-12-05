package OCOH;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import anja.swinggui.JRepeatButton;

/**
 * Provides the GUI for the OCOHAlgorithm focussed on the 1-Center and 1-Turnpike
 * problem. This GUI provides functionalities to visualize the steps of the algorithm
 * as well as an animation option to display all steps in one row. Also the optimal 
 * location of the turnpike within the demand points is displayed.
 * 
 * Implemented in the context of the Project Group "Computational Geometry" at the 
 * University of Bonn, 24.02.2015.
 * 
 * @author Sally Chau
 * 
 */
public class OCOHGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	//**************************************************************************
	// Variables: GUI Elements
	//**************************************************************************
	
	private Graphics g;
	private static OCOHGUI _panel;
	Listener l = new Listener();
	
	// Fonts
	Font font_title = new Font("Default", Font.BOLD, 14);
	Font font_default = new Font("Default", Font.PLAIN, 12);
	
	// Panels
	JPanel panel_east = new JPanel();
	JPanel panel_west = new JPanel();
	JPanel panel_screen = new JPanel();
	JPanel panel_south = new JPanel();
	JPanel panel_examples = new JPanel();

	// Labels
	JLabel label_mode = new JLabel("MODE 1C1H");
	JLabel label_underline = new JLabel("________________________");
	JLabel label_underline1 = new JLabel("________________________");
	JLabel label_underline2 = new JLabel("________________________");
	JLabel label_highway = new JLabel("Highway: ");
	JLabel label_length = new JLabel();
	JLabel label_velocity = new JLabel();
	JLabel label_examples = new JLabel("Examples:");
	JLabel label_algorithm = new JLabel("ALGORITHM");
	JLabel label_step = new JLabel("Step by step: ");
	JLabel label_show = new JLabel("SHOW");
	JLabel label_xyCoord = new JLabel();
	JLabel label_aniSpeed = new JLabel();
	JLabel label_comment = new JLabel(" ");

	// Sliders
	private JSlider slider_length = new JSlider(JSlider.HORIZONTAL, 0, 400, 100);
	private JSlider slider_velocity = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
	private JSlider slider_aniSpeed = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
	
	// Buttons
	JButton button_prev = new JButton("\u2759\u25c4");
	JButton button_next = new JButton("\u25BA\u2759");
	JButton button_clear = new JButton();
	JButton button_animation = new JButton("\u25BA");
	JButton button_display = new JButton("\u21BA");
	JRepeatButton button_zoomIn = new JRepeatButton();
	JRepeatButton button_zoomOut = new JRepeatButton();
	
	// Checkbox
	JCheckBox checkBox_fixedLength = new JCheckBox("fixed");
	JCheckBox checkBox_showCustomers = new JCheckBox("Customers");
	JCheckBox checkBox_showSolution = new JCheckBox("Solution");
	JCheckBox checkBox_showAlgoSteps = new JCheckBox("Algorithm steps");
	JCheckBox checkBox_showGrid = new JCheckBox("Grid");
	// only for presentation purpose
	JCheckBox checkBox_showAxisAlignedBox = new JCheckBox("Bounding Box");
	JCheckBox checkBox_showCenters = new JCheckBox("Centers");

	// Combobox
	String[] string_examples = { "- - - - - - - - - - -", "Example 1", "Example 2", "Example 3" };
	JComboBox<String> box_examples = new JComboBox<String>(string_examples);
	String[] string_highways = { "Turnpike", "Freeway" };
	JComboBox<String> box_highways = new JComboBox<String>(string_highways);

	// Image icons
	ImageIcon icon_zoomIn = createImageIcon("resources/zoomIn.png");
	ImageIcon icon_zoomOut = createImageIcon("resources/zoomOut.png");
	ImageIcon icon_clear = createImageIcon("resources/trash.png");
	
	JTextArea textArea = new JTextArea(36, 10);
	JFrame frame = new JFrame();

	//**************************************************************************
	// Variables
	//**************************************************************************
	
	private boolean _animationActive = false;
	private boolean _algoRunnable = true;
	OCOHAlgorithm algorithm;
	public PointList customersList = new PointList();
	public int velocity;
	public int highwayLength;
	Point dragged;
	Point pointed;
	int stepCounter = 0;
	int prevStepCounter = -1;
	int aniSpeed = 1;

	//**************************************************************************
	// Constructor
	//**************************************************************************
	
	/**
	 * Creates an OCOHGUI object that provides the OCOHAlgorithm class with a 
	 * GUI to display the progress of the algorithm.
	 * @param algorithm
	 */
	private OCOHGUI(OCOHAlgorithm algorithm) {
		
		this.algorithm = algorithm;
		
		defaultButtonSettings();
		constrainButtons();
		createGUI();
		addMouseListener(MouseHandler.getMouseHandler(this));
		addMouseMotionListener(MouseHandler.getMouseHandler(this));
		ToolTipManager.sharedInstance().registerComponent(this);
		
	}

	//**************************************************************************
	// Getter
	//**************************************************************************
	
	/**
	 * Returns the panel on which OCOHGUI mainly works on.
	 * @param algorithm
	 * @return
	 * 		panel
	 */
	public static OCOHGUI getOCOHGUI(OCOHAlgorithm algorithm) {
		
		if (_panel == null) _panel = new OCOHGUI(algorithm);
		return _panel;
		
	}

	//**************************************************************************
	// Create GUI/ Panels
	//**************************************************************************
	
	/**
	 * Creates the pop-up window for displaying the current partition radius.
	 */
	public void createRadiusWindow() {
		
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
            	frame.setAutoRequestFocus(false);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocation(1190,45);

            	JPanel panel_newFrame = new JPanel();
                panel_newFrame.setLayout(new BoxLayout(panel_newFrame, BoxLayout.Y_AXIS));
                panel_newFrame.setOpaque(true);
                
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
        		textArea.setText("Computing turnpike location ... \n\n Partitionradius:  \n\n");
                
                JScrollPane scroller = new JScrollPane(textArea);
                scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                
                DefaultCaret caret = (DefaultCaret) textArea.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                panel_newFrame.add(scroller);
                frame.getContentPane().add(BorderLayout.CENTER, panel_newFrame);
                frame.pack();
                frame.setResizable(false);
            }
        });
    }
	
	/**
	 * Creates the west panel which is the main toolbar for the GUI.
	 */
	public void createWestPanel(){
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		panel_west.setOpaque(true);
		panel_west.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		panel_west.setLayout(new GridBagLayout());

		// Label "Mode"
		label_mode.setFont(font_title);
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel_west.add(label_mode, gbc);
		gbc.insets = new Insets(10, 0, 3, 0);
		panel_west.add(label_underline, gbc);

		// Label "Highway"
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.gridy = 1;
		label_highway.setFont(font_default);
		panel_west.add(label_highway, gbc);

		// ComboBox for Highwaytypes
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 2;
		box_highways.setFont(font_default);
		box_highways.setToolTipText("Change kind of highway");
		panel_west.add(box_highways, gbc);

		// Label "Length"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 3;
		label_length.setFont(font_default);
		panel_west.add(label_length, gbc);

		// Checkbox for length
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 4;
		checkBox_fixedLength.setFont(font_default);
		if (checkBox_fixedLength.isSelected()) checkBox_fixedLength.setToolTipText("Make length variable");
		else checkBox_fixedLength.setToolTipText("Make length invariable");
		panel_west.add(checkBox_fixedLength, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 5;
		slider_length.setPreferredSize(new Dimension(160,50));
		slider_length.setMajorTickSpacing(200);
		slider_length.setMinorTickSpacing(100);
		slider_length.setPaintTicks(true);
		slider_length.setPaintLabels(true);
		slider_length.setFont(font_default);
		panel_west.add(slider_length, gbc);
		
		// Label "Velocity"
		gbc.gridy = 6;
		label_velocity.setFont(font_default);
		panel_west.add(label_velocity, gbc);

		// Slider for velocity
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 7;
		slider_velocity.setPreferredSize(new Dimension(120,50));
		slider_velocity.setMajorTickSpacing(25);
		slider_velocity.setMinorTickSpacing(5);
		slider_velocity.setPaintTicks(true);
		slider_velocity.setPaintLabels(true);
		slider_velocity.setFont(font_default);
		panel_west.add(slider_velocity, gbc);

		gbc.gridy = 8;
		panel_west.add(new JLabel(""), gbc);

		// Label "Algorithm"
		label_algorithm.setFont(font_title);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 9;
		panel_west.add(label_algorithm, gbc);
		gbc.insets = new Insets(10, 0, 3, 0);
		panel_west.add(label_underline1, gbc);

		// ComboBox for Examples
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 10;
		button_display.setFont(font_default);
		button_display.setToolTipText("Display chosen example");
		box_examples.setFont(font_default);
		panel_examples.add(box_examples);
		panel_examples.add(button_display);
		panel_west.add(panel_examples, gbc);

//		// Label "Step by Step"
//		gbc.fill = GridBagConstraints.NONE;
//		gbc.gridy = 12;
//		label_step.setFont(font_default);
//		panel_west.add(label_step, gbc);

		// Buttons Step by Step
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 13;
		JPanel panel_steps = new JPanel();
		button_prev.setToolTipText("Show previous step");
		button_prev.setFont(font_default);
		button_next.setToolTipText("Show next step");
		button_next.setFont(font_default);
		button_animation.setFont(font_default);
		button_animation.setToolTipText("Start animation");
		panel_steps.add(button_prev);
		panel_steps.add(button_animation);
		panel_steps.add(button_next);
		panel_west.add(panel_steps, gbc);
		
		// Animation speed slider
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 14;
		label_aniSpeed.setFont(font_default);
		panel_west.add(label_aniSpeed, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 15;
		slider_aniSpeed.setPreferredSize(new Dimension(80,50));
		slider_aniSpeed.setMajorTickSpacing(1);
		slider_aniSpeed.setPaintTicks(true);
		slider_aniSpeed.setPaintLabels(true);
		slider_aniSpeed.setFont(font_default);
		panel_west.add(slider_aniSpeed, gbc);

		// Label "Show"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 16;
		label_show.setFont(font_title);
		gbc.anchor = GridBagConstraints.LINE_START;
		panel_west.add(label_show, gbc);
		gbc.insets = new Insets(10, 0, 3, 0);
		panel_west.add(label_underline2, gbc);

		// Checkbox
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.gridy = 17;
		checkBox_showCustomers.setFont(font_default);
		panel_west.add(checkBox_showCustomers, gbc);
		gbc.gridy = 18;
		checkBox_showSolution.setFont(font_default);
		panel_west.add(checkBox_showSolution, gbc);
		gbc.gridy = 19;
		checkBox_showAlgoSteps.setFont(font_default);
		panel_west.add(checkBox_showAlgoSteps, gbc);
		gbc.gridy = 20;
		checkBox_showGrid.setFont(font_default);
		panel_west.add(checkBox_showGrid, gbc);
		gbc.gridy = 21;
		checkBox_showAxisAlignedBox.setFont(font_default);
		panel_west.add(checkBox_showAxisAlignedBox, gbc);
		gbc.gridy = 22;
		checkBox_showCenters.setFont(font_default);
		panel_west.add(checkBox_showCenters, gbc);
		
		adjustText();
	
	}
	
	/**
	 * Creates the panel which works on the panel which receives direct user input.
	 * Zoom funtions and clear button.
	 */
	public void createScreenPanel(){
		
		button_zoomIn.setIcon(icon_zoomIn);
		button_zoomIn.setPreferredSize(new Dimension(26, 26));
		
		button_zoomOut.setIcon(icon_zoomOut);
		button_zoomOut.setPreferredSize(new Dimension(26, 26));
		
		button_clear.setIcon(icon_clear);
		button_clear.setPreferredSize(new Dimension(26, 26));
		
		panel_screen.setBackground(Color.WHITE);
		panel_screen.add(button_zoomIn);
		panel_screen.add(button_zoomOut);
		panel_screen.add(button_clear);
		
	}
	
	/**
	 * Creates the panel which contains all the GUI elements
	 * which do not belong to west panel.
	 */
	public void createEastPanel(){
		
		createScreenPanel();
		
		label_xyCoord.setBackground(Color.WHITE);
		label_xyCoord.setOpaque(true);
		
		panel_east.setOpaque(false);
		panel_east.setLayout(new BorderLayout());
		panel_east.setBackground(Color.WHITE);
		
		panel_east.add(panel_screen, BorderLayout.NORTH);
		panel_east.add(label_xyCoord, BorderLayout.SOUTH);
		
	}
	
	/**
	 * Creates panel which actively comments the scene at hand. 
	 */
	public void createSouthPanel(){
		
		panel_south.setOpaque(true);
		panel_south.setLayout(new BorderLayout());
		
		label_comment.setHorizontalAlignment(JLabel.CENTER);
		label_comment.setVerticalAlignment(JLabel.CENTER);
		panel_south.add(label_comment, BorderLayout.CENTER);
		
	}
	
	/** 
	 * Creates the whole GUI and puts all bits and pieces of it together.
	 */
	public void createGUI() {
		
		createWestPanel();
		createEastPanel();
		createSouthPanel();
		createRadiusWindow();

		setLayout(new BorderLayout());
		add(panel_south, BorderLayout.SOUTH);
		add(panel_west, BorderLayout.WEST);
		add(panel_east, BorderLayout.EAST);

		registerListeners();
		constrainButtons();
		
	}
	
	/**
	 * Changes the text for the comment toolbar as soon as the scene changes.
	 */
	public void adjustText(){

		label_length.setText("Length: " + slider_length.getValue());
		label_velocity.setText("Velocity: " + slider_velocity.getValue());
		label_aniSpeed.setText("Animation speed: " + slider_aniSpeed.getValue());
		
	}
	
	/**
	 * Displays the current mouse position in screen.
	 * @param p
	 * 		current mouse position
	 */
	public void setXYLabel(Point p){
		
		label_xyCoord.setFont(new Font("Default", Font.PLAIN, 12));
		label_xyCoord.setText("X: " + p.posX + ", Y: " + p.posY);
		
		repaint();
		moveMouse(p);
	
	}
	
	/**
	 * Enters according partition radius into pop-up window while animation is running.
	 * @param i
	 */
	public void enterRadius(int i){
	
		textArea.setFont(font_default);
		textArea.append((i+1) + "/" + algorithm.set_withTurnpike.size() + ": "
				+ (Math.round(algorithm.partitionRadius.get(i)*100)/100.00) + "\n");
		
	}
	
	/**
	 * Displays the according labels for the points set on screen.
	 * Indicates whether the demand point use the highway to reach the facility
	 * or not; indicates where the facilty point is, where the turnpike startpoint is.
	 */
	public String getToolTipText(MouseEvent event) {

		Point p = new Point(event.getX(), event.getY());

		if (algorithm.set_withTurnpike == null) {
			return null;
		}

		for (int j = 0; j < algorithm.set_withTurnpike.get(stepCounter).getSize(); j++) {
			if (p.equals(algorithm.set_withTurnpike.get(stepCounter).points.get(j))) {

				return "Customer uses TP" ;
			}
		}
		
		for (int j = 0; j < algorithm.set_withoutTurnpike.get(stepCounter).getSize(); j++) {
			if (p.equals(algorithm.set_withoutTurnpike.get(stepCounter).points.get(j))) {

				return "Customer does not use TP" ;
			}
		}
		
		if (checkBox_showSolution.isSelected()){
			if (isFacilityPoint(p)) return "optimal facility point";
			if (isTurnpikeStartPoint(p)) return "optimal turnpike startpoint";
		} else {
			if (p.equals(algorithm.facilityPoints.get(stepCounter))) return "Facility point";
			if (p.equals(algorithm.turnpikePoints.get(stepCounter))) return "Turnpike startpoint";
		}
		
		return null;

	}
	
	//**************************************************************************
	// Button Methods
	//**************************************************************************
	
	/**
	 * Default settings for all GUI elements.
	 */
	public void defaultButtonSettings() {
		
		frame.setVisible(false);
		checkBox_showGrid.setSelected(false);
		checkBox_fixedLength.setEnabled(false);
		checkBox_fixedLength.setSelected(true);
		checkBox_showCustomers.setSelected(true);
		checkBox_showAxisAlignedBox.setSelected(false);
		checkBox_showCenters.setSelected(false);
		slider_aniSpeed.setValue(3);
		slider_length.setValue(100);
		slider_velocity.setValue(10);
		button_zoomIn.setEnabled(false);
		button_zoomOut.setEnabled(false);

	}
	
	/**
	 * Checks which buttons and boxes are selected and constrains the use of others
	 * as their coincidental action would contradict each other.
	 */
	public void constrainButtons(){
		
		if (algorithm.set_withTurnpike != null){
			
			if (stepCounter < algorithm.set_withTurnpike.size()-1 && customersList.getSize() > 2){
				button_next.setEnabled(true);
			} else button_next.setEnabled(false);
		
			if (stepCounter > 0 && customersList.getSize() > 2){
				button_prev.setEnabled(true);
			} else {
				button_prev.setEnabled(false);
			}
			
			if (algorithm.set_withTurnpike.size() < 1 || customersList.getSize() < 2){
				button_animation.setEnabled(false);
				button_prev.setEnabled(false);
				button_next.setEnabled(false);
			} else {
				button_animation.setEnabled(true);
			}
			
		} else {
			button_animation.setEnabled(false);
			button_prev.setEnabled(false);
			button_next.setEnabled(false);
		}
		
		if (_animationActive) {
			frame.setVisible(true);
			button_clear.setEnabled(false);
			button_prev.setEnabled(false);
			button_next.setEnabled(false);
			button_display.setEnabled(false);
			button_animation.setText("\u25A0");
			button_animation.setToolTipText("Stop animation");
			checkBox_showSolution.setSelected(true);
			checkBox_showAlgoSteps.setSelected(true);
			checkBox_showAlgoSteps.setEnabled(false);
			checkBox_showSolution.setEnabled(false);
			checkBox_showCustomers.setSelected(false);
			checkBox_showCustomers.setEnabled(false);
			slider_length.setEnabled(false);
			slider_velocity.setEnabled(false);
			box_examples.setEnabled(false);
			box_highways.setEnabled(false);
		} else {
			button_clear.setEnabled(true);
			button_animation.setText("\u25BA");
			button_animation.setToolTipText("Start animation");
			button_display.setEnabled(true);
			checkBox_showSolution.setEnabled(true);
			checkBox_showCustomers.setEnabled(true);
			checkBox_showAlgoSteps.setEnabled(true);
			slider_length.setEnabled(true);
			slider_velocity.setEnabled(true);
			box_examples.setEnabled(true);
			box_highways.setEnabled(true);
		}
		
	}
	
	//**************************************************************************
	// Listeners
	//**************************************************************************
	
	private class Listener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
		
			repaint();
		
		}
	
	}
	
	private class SliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
	
		}
		
	}
	
	public void registerListeners() {

		// Register the listeners
		
		Listener l = new Listener();
		
		checkBox_showAxisAlignedBox.addActionListener(l);
		
		checkBox_showCenters.addActionListener(l);
		
		checkBox_showGrid.addActionListener(l);
		
		checkBox_showAlgoSteps.addActionListener(l);
		
		checkBox_showSolution.addActionListener(l);
		
		checkBox_showCustomers.addActionListener(l);
		
		button_display.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				clear();
				defaultButtonSettings();
				if (box_examples.getSelectedIndex() == 1) {
					runExample1();
				} else if (box_examples.getSelectedIndex() == 2) {
					runExample2();
				} else if (box_examples.getSelectedIndex() == 3) {
					runExample3();
				} 
				
			}
			
		});
		
		slider_aniSpeed.addChangeListener(new SliderListener());
		
		slider_length.addChangeListener(new SliderListener());
		
		slider_velocity.addChangeListener(new SliderListener());

		button_clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				clear();
			
			}
		});

		button_next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (stepCounter < algorithm.set_withTurnpike.size()-1) {
					checkBox_showSolution.setSelected(false);
					checkBox_showAlgoSteps.setSelected(true);
					stepCounter++;
				}
					
				repaint();
				
			}
		});

		button_prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (stepCounter > 0) {
					checkBox_showSolution.setSelected(false);
					checkBox_showAlgoSteps.setSelected(true);
					stepCounter--;
				}
				
				repaint();
				
			}

		});

		button_animation.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
	        		textArea.setText("Computing\nturnpike location ... \n\nPartitionradius:  \n\n");
				
					stepCounter = 0;
					
					_animationActive = !_animationActive;
					
					AnimationThread animation = new AnimationThread();
					animation.start();
				
			}
			
		});
	}
	
	//**************************************************************************
	// Mouse Listeners
	//**************************************************************************
	
	public void moveMouse(Point p) {
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		pointed = null;
		
		if (pointAlreadyExists(p)) {
			
			pointed = searchPoint(p);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		}
		
		repaint();

	}
	
	/**
	 * Delete point if right mouse button is clicked.
	 * @param p
	 */
	public void rightMouseClick(Point p) {
		
		if (!_animationActive){
			if (pointAlreadyExists(p)) {
	
				deletePoint(p);
			
			}
	
			pointed = null;
			// algorithm.clear();
			constrainButtons();
			// runAlgorithm();
			repaint();
		}
	}
	
	/**
	 * Add point to customersList if left mouse button is clicked.
	 * @param p
	 */
	public void leftMouseClick(Point p) {

		if (!_animationActive){
			if (!pointAlreadyExists(p) && (checkBox_showCustomers.isSelected() || checkBox_showSolution.isSelected())) {
				
				addPoint(p);
			
			} else {
	
				if (customersList.contains(p) && (checkBox_showCustomers.isSelected() || checkBox_showSolution.isSelected())) {
					
					dragged = customersList.search(p);
				
				}
				// Highlight the clicked point
				repaint();
			
			
			}
	
			constrainButtons();
		}
	}

	//**************************************************************************
	// Draw Methods
	//**************************************************************************
	
	/**
	 * Draws the partition which provides the optimal solution to the OCOH problem,
	 * as well as the turnpike location.
	 */
	public void drawSolution(){
		
		label_comment.setText("optimal location of turnpike");
		int index = algorithm.solutionIndex;
		stepCounter = index;
		drawBalls(index);
		drawPartition(index);
		drawTurnpikePos(index);
		
	}
	
	/**
	 * Draws the partition which for step i.
	 * @param i
	 * 		step i
	 */
	public void drawPartition(int i){
		
		algorithm.set_withTurnpike.get(i).draw(g);
		algorithm.set_withoutTurnpike.get(i).draw(g);
		
	}
	
	/**
	 * Draws the turnpike location of the basic problem of step i.
	 * @param i
	 * 		step i
	 */
	public void drawTurnpikePos(int i){
		
		((Graphics2D) g).setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		g.drawLine((int) algorithm.facilityPoints.get(i).posX,
				(int) algorithm.facilityPoints.get(i).posY,
				(int) algorithm.turnpikePoints.get(i).posX,
				(int) algorithm.turnpikePoints.get(i).posY);
		algorithm.facilityPoints.get(i).setColor(Color.GREEN);
		algorithm.facilityPoints.get(i).draw(g);
		algorithm.turnpikePoints.get(i).setColor(Color.ORANGE);
		algorithm.turnpikePoints.get(i).draw(g);
		((Graphics2D)g).setStroke(new BasicStroke(1));
		
	}
	
	/**
	 * Draws the balls which indicate which customers do and do not use the turnpike
	 * to reach the facility point.
	 * @param i
	 * 		step i
	 */
	public void drawBalls(int i){
		
		double r = algorithm.partitionRadius.get(i) + Point.RADIUS;
		Point f = algorithm.facilityPoints.get(i);
		Point t = algorithm.turnpikePoints.get(i);

		g.setColor(new Color(255,193,214,100));
		g.fillRect((int)(f.posX - r), (int)(f.posY - r), (int)(2*r), (int)(2*r));
		g.setColor(new Color(130,228,176,100));
		g.fillRect((int)(t.posX - (r-highwayLength/velocity)), (int)(t.posY - (r-highwayLength/velocity)), (int)(2*(r-highwayLength/velocity)), (int)(2*(r-highwayLength/velocity)));
		
	}
		
	/**
	 * Draws a grid on screen for better orientation and understanding of 
	 * the infinity norm.
	 * @param g
	 */
	public void drawGrid(Graphics g){
		
		if (checkBox_showGrid.isSelected()){
			
			for (int i = 0; i < _panel.getWidth(); i += 20){
				((Graphics2D)g).setStroke(new BasicStroke(0.4f));
				g.drawLine(i, 0, i, _panel.getHeight());
			}
			for (int i = 0; i < _panel.getHeight(); i +=20){
				((Graphics2D)g).setStroke(new BasicStroke(0.4f));
				g.drawLine(0, i, _panel.getWidth(), i);
			}
			
			
		}
		((Graphics2D)g).setStroke(new BasicStroke(1));
	}
	
	/**
	 * Draws all customer points.
	 */
	public void drawAllPoints() {

		if (checkBox_showCustomers.isSelected()) {
			customersList.draw(g);
		
			// Draw a rectangle around the point which is selected
			if (dragged != null) {
				dragged.drawHighlight(g);
			}
			if (pointed != null) {
				pointed.drawBoundings(g);
			}
		}
		
		repaint();
	
	}
	
	// only for debugging purposes
	
	/**
	 * Draws the maximum and minimum distance points.
	 * @param i
	 * 		step i
	 */
	public void drawDistPoints(int i){
		
		algorithm.maxDist1.get(i).setColor(Color.CYAN);
		algorithm.maxDist2.get(i).setColor(Color.MAGENTA);
		algorithm.minDist1.get(i).setColor(Color.CYAN);
		algorithm.minDist2.get(i).setColor(Color.MAGENTA);
		algorithm.maxDist1.get(i).draw(g);
		algorithm.maxDist2.get(i).draw(g);
		algorithm.minDist1.get(i).draw(g);
		algorithm.minDist2.get(i).draw(g);
		
	}

	/**
	 * Draws squares on the extreme points of the center sets in order to check 
	 * if center() was correctly computed.
	 * @param i
	 * 		step i
	 */
	public void checkCenter(int i){
		
		PointList c1 = algorithm.list_centersWithoutTurnpike.get(i);
		PointList c2 = algorithm.list_centersWithTurnpike.get(i);
		
		double r = algorithm.partitionRadius.get(i);
		
		for (int j = 0; j < c1.getSize(); j++){
			g.setColor(Color.BLACK);
			g.drawRect((int)(c1.points.get(j).posX-r),(int)(c1.points.get(j).posY-r), (int)(2*r), (int)(2*r));
		}
		
		for (int j = 0; j < c2.getSize(); j++){
			g.setColor(Color.BLACK);
			g.drawRect((int)(c2.points.get(j).posX-r),(int)(c2.points.get(j).posY-r), (int)(2*r), (int)(2*r));
		}
		
	}
	
	/**
	 * Draws smallest axis-algined rectangle in order to visually divide the customer set
	 * into two partitions.
	 * @param i
	 * 		step i
	 */
	public void drawSmallestAxisAlignedRect(int i){

			((Graphics2D)g).setStroke(new BasicStroke(3));
			g.setColor(Color.BLUE);
			g.drawRect(
					(int) algorithm.extremePoints1.get(i)[0].posX,
					(int) algorithm.extremePoints1.get(i)[2].posY,
					(int) Math.abs(algorithm.extremePoints1.get(i)[0].posX
							- algorithm.extremePoints1.get(i)[1].posX),
					(int) Math.abs(algorithm.extremePoints1.get(i)[2].posY
							- algorithm.extremePoints1.get(i)[3].posY));
			g.setColor(Color.RED);
			g.drawRect(
					(int) algorithm.extremePoints2.get(i)[0].posX,
					(int) algorithm.extremePoints2.get(i)[2].posY,
					(int) Math.abs(algorithm.extremePoints2.get(i)[0].posX
							- algorithm.extremePoints2.get(i)[1].posX),
					(int) Math.abs(algorithm.extremePoints2.get(i)[2].posY
							- algorithm.extremePoints2.get(i)[3].posY));
			((Graphics2D)g).setStroke(new BasicStroke(1));

	}
	
	/**
	 * Draws the set of points where the center of a square of radius r can be placed, that covers
	 * the according partition set.
	 * @param i
	 * 		step i
	 */
	public void drawCenters(int i){
		
		if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 1) {
			algorithm.list_centersWithoutTurnpike.get(i).draw(g);
		} else if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 2) {
			((Graphics2D)g).setStroke(new BasicStroke(3));
			g.setColor(Color.GRAY);
			g.drawLine((int) algorithm.list_centersWithoutTurnpike.get(i).points.get(0)
					.getX(), (int) algorithm.list_centersWithoutTurnpike.get(i).points
					.get(0).getY(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(1)
							.getX(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(1)
							.getY());
			((Graphics2D)g).setStroke(new BasicStroke(1));
		} else if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 4) {
			g.setColor(Color.GRAY);
			g.fillRect(
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(0)
							.getX(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(0)
							.getY(),
					Math.abs((int) algorithm.list_centersWithoutTurnpike.get(i).points
							.get(2).getX()
							- (int) algorithm.list_centersWithoutTurnpike.get(i).points
									.get(0).getX()),
					Math.abs((int) algorithm.list_centersWithoutTurnpike.get(i).points
							.get(1).getY())
							- (int) algorithm.list_centersWithoutTurnpike.get(i).points
									.get(0).getY());
		}
		if (algorithm.list_centersWithTurnpike.get(i).getSize() == 1) {
			algorithm.list_centersWithTurnpike.get(i).draw(g);
		} else if (algorithm.list_centersWithTurnpike.get(i).getSize() == 2) {
			g.setColor(Color.GRAY);
			((Graphics2D)g).setStroke(new BasicStroke(2));
			g.drawLine((int) algorithm.list_centersWithTurnpike.get(i).points.get(0)
					.getX(), (int) algorithm.list_centersWithTurnpike.get(i).points
					.get(0).getY(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(1)
							.getX(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(1)
							.getY());
			((Graphics2D)g).setStroke(new BasicStroke(1));
		} else if (algorithm.list_centersWithTurnpike.get(i).getSize() == 4) {
			g.setColor(Color.GRAY);
			g.fillRect(
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(0)
							.getX(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(0)
							.getY(),
					Math.abs((int) algorithm.list_centersWithTurnpike.get(i).points
							.get(2).getX()
							- (int) algorithm.list_centersWithTurnpike.get(i).points
									.get(0).getX()),
					Math.abs((int) algorithm.list_centersWithTurnpike.get(i).points
							.get(1).getY())
							- (int) algorithm.list_centersWithTurnpike.get(i).points
									.get(0).getY());
		}
	}
	
	//**************************************************************************
	// Boolean
	//**************************************************************************
	
	/**
	 * Checks if point is already existing in customer list.
	 * @param p
	 * @return
	 */
	public boolean pointAlreadyExists(Point p) {

		if (customersList.contains(p)) {
			
			return true;
		
		}
		
		return false;
	}

	/**
	 * Checks whether the point p is the optimal facility point.
	 * @param p
	 * @return
	 * 		true, if p is optimal facility point.
	 */
	public boolean isFacilityPoint(Point p){

		Point f = algorithm.solution_facility;
		if (f != null) {
			
			if (f.equals(p)) return true;
		
		}
		
		return false;
	}
	
	/**
	 * Checks whether the point p is the turnpike startpoint of the optimal solution.
	 * @param p
	 * @return
	 * 		true, if p is the turnpike startpoint of the optimal solution.
	 */
	public boolean isTurnpikeStartPoint(Point p){
		
		Point t = algorithm.solution_turnpikeStart;
		if (t != null) {
			
			if (t.equals(p)) return true;
		
		}
		
		return false;
	
	}
	
	/**
	 * Checks whether a point's circle is intersecting and therefore colliding with 
	 * another one.
	 * @param p
	 * @return
	 * 		true, if two points collide
	 */
	public boolean collisionExists(Point p) {
		
		if (!(customersList.collisionExists(p))) {
		
			return false;
		
		}
		
		return true;
	
	}
	
	//**************************************************************************
	// Panel Interaction Methods
	//**************************************************************************
	
	/**
	 * Clears the current screen and sets everything back to default settings.
	 */
	public void clear() {
		
		_animationActive = false;
		customersList.clear();
		// algorithm.clear();
		adjustText();
		defaultButtonSettings();
		constrainButtons();
		stepCounter = 0;
		repaint();
		
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 * 		Point p
	 */
	public Point searchPoint(Point p) {

		return customersList.search(p);

	}
	
	/**
	 * Restarts the algorithm for the moments where a point is dragged and moved.
	 * @param p
	 */
	public void dragged(Point p) {

		if (dragged != null && !collisionExists(p)) {
		
			dragged.setPosX(p.getX());
			dragged.setPosY(p.getY());

		}
		
		runAlgorithm();
		repaint();
	
	}

	/**
	 * Deletes point from customer list.
	 * @param p
	 */
	public void deletePoint(Point p) {

		customersList.remove(p);

	}
	
	/**
	 * Adds point to customer list.
	 * @param p
	 */
	public void addPoint(Point p) {

		if (!collisionExists(p)) {

			customersList.addPoint(p);
			runAlgorithm();

		}
		
		repaint();
		moveMouse(p);
	
	}

	/**
	 * Measures the screen size which is used for user input of points.
	 * @return
	 */
	public int[] getScreenCoordinates() {

		int[] coordinates = { panel_west.getWidth(), _panel.getWidth(),
				panel_screen.getWidth(), panel_screen.getHeight() };

		return coordinates;

	}

	//**************************************************************************
	// Preset Examples
	//**************************************************************************
	
	/**
	 * Example 1: 
	 */
	private void runExample1(){
		
		// case c)
		clear();
		customersList.addPoint(new Point(613, 102));
		customersList.addPoint(new Point(388, 371));
		runAlgorithm();
		
	}
	
	/**
	 * Example 2:
	 */
	private void runExample2(){

		clear();
		customersList.addPoint(new Point(325, 255));
		customersList.addPoint(new Point(347, 181));
		customersList.addPoint(new Point(499, 230));
		customersList.addPoint(new Point(519, 259));
		customersList.addPoint(new Point(602, 179));
		customersList.addPoint(new Point(612, 104));
		customersList.addPoint(new Point(681, 140));
		customersList.addPoint(new Point(638, 251));
		customersList.addPoint(new Point(652, 333));
		runAlgorithm();
		
	}
	
	/**
	 * Example 3:
	 */
	private void runExample3(){
		
		clear();
		customersList.addPoint(new Point(496, 258));
		customersList.addPoint(new Point(507, 275));
		customersList.addPoint(new Point(655, 215));
		customersList.addPoint(new Point(647, 186));
		customersList.addPoint(new Point(670, 169));
		runAlgorithm();
		
	}
	
	//**************************************************************************
	// Run Algorithm/ Thread
	//**************************************************************************
	
	/**
	 * Starts the algorithm to solve the 1-Center and 1-Highway Problem for
	 * fixed length turnpikes.
	 */
	public void runAlgorithm() {
		
		highwayLength = slider_length.getValue();
		velocity = slider_velocity.getValue();
		aniSpeed = (int)slider_aniSpeed.getValue();
		if (velocity == 0 || highwayLength == 0) _algoRunnable = false;
		else _algoRunnable = true;
		
		if (_algoRunnable){
			algorithm.runAlgorithm(customersList, highwayLength, velocity);
			repaint();
		}
		
	}
	
	/**
	 * Animation for OCOH Algorithm
	 * @author Sally Chau
	 *
	 */
	public class AnimationThread extends Thread{
		
		public void run(){
			
			while(_animationActive && stepCounter < algorithm.set_withTurnpike.size()){
				
				int i = stepCounter + 1;
				label_comment.setText("Computing optimal location ... Step " + i + " of " + algorithm.set_withTurnpike.size());
				
				try {
					
					sleep(5000 / aniSpeed);
			
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				
				repaint();
				
				stepCounter ++;
				
			}
			
			
			stepCounter = 0;
			label_comment.setText(" ");
			_animationActive = false;
			
			
		}
		
	}
	
	//**************************************************************************
	// Paint Method
	//**************************************************************************
	
	public void paintComponent(Graphics graph) {
		
		Graphics2D g = (Graphics2D) graph;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		super.paintComponent(graph);
		this.g = g;

		drawGrid(g);
		constrainButtons();
		adjustText();
		drawAllPoints();

		if (!customersList.isEmpty()) {
			runAlgorithm();
			if (_algoRunnable){
				if (algorithm.set_withTurnpike != null){
					if (algorithm.set_withTurnpike.size() > 0) {
						if (_animationActive && stepCounter > -1){
							if (prevStepCounter != stepCounter) {
								enterRadius(stepCounter);
								if (stepCounter == (algorithm.set_withoutTurnpike.size()-1)){
									textArea.append("============ \n smallest radius \n found at step " 
										+ (algorithm.solutionIndex + 1) + ": \n Radius = " 
										+ algorithm.partitionRadius.get(algorithm.solutionIndex) + "\n\n"
										+ "-> Locate Turnpike\nand Facility as seen!");
								}
							}
							drawBalls(stepCounter);
							drawPartition(stepCounter);
							drawTurnpikePos(stepCounter);
							prevStepCounter = stepCounter;
							} else {
							// only for presentation purpose
							if(checkBox_showCenters.isSelected()){
								drawCenters(stepCounter);
							}
							if(checkBox_showAxisAlignedBox.isSelected()){
								drawSmallestAxisAlignedRect(stepCounter);
							} // presentation purpose
							
							if (checkBox_showCustomers.isSelected()){
								label_comment.setText("Customer locations");
								customersList.draw(g);
							}	
							if (checkBox_showSolution.isSelected()) {
								customersList.draw(g);
								drawSolution();
							} 
							if (checkBox_showAlgoSteps.isSelected()){
								if (stepCounter != algorithm.solutionIndex){
									label_comment.setText("Computing optimal location ... Step " + (stepCounter+1) + " of " + algorithm.set_withTurnpike.size());
								} else label_comment.setText("Optimal location of turnpike found.");
								
								drawBalls(stepCounter);
								drawPartition(stepCounter);
								drawTurnpikePos(stepCounter);
							} 
						
						}
					}
				}
			}
		}
	}
	
	public static ImageIcon createImageIcon(String path) {
		
		Image look = null;
		
		try {
			
			look = ImageIO.read(OCOHGUI.class.getClassLoader().getResource(path));
			
		} catch (IOException e) {
		
			e.printStackTrace();
		
		}
		
		return new ImageIcon(look);
		
	}
	
}
