package OCOH;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import anja.swinggui.JRepeatButton;

public class OCOHGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// GUI elements
	private Graphics g;
	private static OCOHGUI panel;
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
	JLabel label_length = new JLabel("Length: ");
	JLabel label_velocity = new JLabel("Velocity: ");
	JLabel label_examples = new JLabel("Examples:");
	JLabel label_algorithm = new JLabel("ALGORITHM");
	JLabel label_step = new JLabel("Step by step: ");
	JLabel label_show = new JLabel("SHOW");
	JLabel label_xyCoord = new JLabel();
	JLabel step = new JLabel(" ");

	// Sliders
	private JSlider slider_length = new JSlider(JSlider.HORIZONTAL, 50, 200, 100);
	private JSlider slider_velocity = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
	private JSlider slider_aniSpeed = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
	
	// Buttons
	JButton button_prev = new JButton("\u2759\u25c4");
	JButton button_next = new JButton("\u25BA\u2759");
	JButton button_clear = new JButton();
	JButton button_animation = new JButton("\u25BA");
	JRepeatButton button_zoomIn = new JRepeatButton();
	JRepeatButton button_zoomOut = new JRepeatButton();
	JButton button_display = new JButton("\u21BA");
	
	// Checkbox
	JCheckBox checkBox_fixedLength = new JCheckBox("fixed");
	JCheckBox checkBox_showCustomers = new JCheckBox("Customers");
	JCheckBox checkBox_showSolution = new JCheckBox("Solution");

	// Combobox
	String[] string_examples = { "- - - - - - - - - - -", "Example 1", "Example 2", "Example 3" };
	JComboBox box_examples = new JComboBox(string_examples);
	String[] string_highways = { "Turnpike", "Freeway" };
	JComboBox box_highways = new JComboBox(string_highways);

	// Image icons
	ImageIcon icon_zoomIn = createImageIcon("resources/zoomIn.png");
	ImageIcon icon_zoomOut = createImageIcon("resources/zoomOut.png");
	ImageIcon icon_clear = createImageIcon("resources/trash.png");

	// Variables
	private boolean animationActive = false;
	private boolean button_next_clicked = false;
	private boolean button_prev_clicked = false;
	private boolean runAlgo = true;
	OCOHAlgorithm algorithm;
	public PointList customersList = new PointList();
	public int velocity;
	public int highwayLength;
	Point dragged;
	Point pointed;
	int stepCounter = 0;
	int aniSpeed = 1;
	
	private OCOHGUI(OCOHAlgorithm algorithm) {
		
		this.algorithm = algorithm;
		
		defaultButtonSettings();
		constrainButtons();
		createGUI();
		addMouseListener(MouseHandler.getMouseHandler(this));
		addMouseMotionListener(MouseHandler.getMouseHandler(this));
		ToolTipManager.sharedInstance().registerComponent(this);
		
	}

	public static OCOHGUI getOCOHGUI(OCOHAlgorithm algorithm) {
		
		if (panel == null) {
			
			panel = new OCOHGUI(algorithm);
		
		}
		
		return panel;
		
	}

	public void defaultButtonSettings() {
		
		checkBox_fixedLength.setEnabled(false);
		checkBox_fixedLength.setSelected(true);
		checkBox_showCustomers.setSelected(true);

	}
	
	public void constrainButtons(){
		
		if (algorithm.set_withoutTurnpike != null){
			if (stepCounter < algorithm.set_withoutTurnpike.size()-1 && customersList.getSize() > 2){
				button_next.setEnabled(true);
			} else button_next.setEnabled(false);
		
			if (stepCounter > 0 && customersList.getSize() > 2){
				button_prev.setEnabled(true);
			} else {
				button_prev.setEnabled(false);
				
			}
			
			if (algorithm.set_withoutTurnpike.size() < 1 || customersList.getSize() < 2){
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
		
		if (animationActive) {
			
			button_prev.setEnabled(false);
			button_next.setEnabled(false);
			checkBox_showSolution.setSelected(true);
			checkBox_showSolution.setEnabled(false);
			checkBox_showCustomers.setSelected(false);
			checkBox_showCustomers.setEnabled(false);
			slider_length.setEnabled(false);
			slider_velocity.setEnabled(false);
			box_examples.setEnabled(false);
			box_highways.setEnabled(false);
			
			button_animation.setText("\u25A0");
			button_animation.setToolTipText("Stop animation");
		
		} else {
			button_animation.setText("\u25BA");
			button_animation.setToolTipText("Start animation");
			checkBox_showSolution.setEnabled(true);
			checkBox_showCustomers.setEnabled(true);
			slider_length.setEnabled(true);
			slider_velocity.setEnabled(true);
			box_examples.setEnabled(true);
			box_highways.setEnabled(true);
		}
		
	}
	
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
		slider_length.setMajorTickSpacing(50);
		slider_length.setMinorTickSpacing(25);
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
		box_examples.setFont(font_default);
		panel_examples.add(box_examples);
		panel_examples.add(button_display);
		panel_west.add(panel_examples, gbc);

		// Label "Step by Step"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 12;
		label_step.setFont(font_default);
		panel_west.add(label_step, gbc);

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
		JLabel label_aniSpeed = new JLabel("Animationspeed: ");
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
	
	}
	
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
	
	public void createEastPanel(){
		
		createScreenPanel();
		
		panel_east.setOpaque(false);
		panel_east.setLayout(new BorderLayout());
		panel_east.setBackground(Color.WHITE);
		
		panel_east.add(panel_screen, BorderLayout.NORTH);
		panel_east.add(label_xyCoord, BorderLayout.SOUTH);
		
	}
	
	public void createSouthPanel(){
		
		panel_south.setOpaque(true);
		panel_south.setLayout(new BorderLayout());
		
		step.setHorizontalAlignment(JLabel.CENTER);
		step.setVerticalAlignment(JLabel.CENTER);
		panel_south.add(step, BorderLayout.CENTER);
		
	}
	
	public void createGUI() {
		
		createWestPanel();
		createEastPanel();
		createSouthPanel();

		setLayout(new BorderLayout());
		add(panel_south, BorderLayout.SOUTH);
		add(panel_west, BorderLayout.WEST);
		add(panel_east, BorderLayout.EAST);

		registerListeners();
		constrainButtons();
		
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
	
	public void registerListeners() {

		// Register the listeners
		
		button_display.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {

				clear();
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
		
		checkBox_showSolution.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				repaint();
				
			}
			
			
		});
		
		checkBox_showCustomers.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				repaint();
				
			}
		});

		button_clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				clear();
			
			}
		});

		button_next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (stepCounter < algorithm.set_withoutTurnpike.size()-1) {
					
					button_next_clicked = true;
					
					stepCounter++;
					
				}
					
				repaint();
				
			}
		});

		button_prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (stepCounter > 0) {
				
					button_prev_clicked = true;
					
					stepCounter--;
				
				}
				
				repaint();
				
			}

		});

		button_animation.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
					stepCounter = 0;
					AnimationThread animation = new AnimationThread();
				if (!animationActive){
					animation.start();
				} else {
					animationActive = false;
					animation.interrupt();
				}
				
			}
			
		});
	}

	public void clear() {
		
		animationActive = false;
		customersList.clear();
		// algorithm.clear();
		defaultButtonSettings();
		constrainButtons();
		stepCounter = 0;
		repaint();
		
	}

	// mouse methods

	public void moveMouse(Point p) {
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		pointed = null;
		
		if (pointAlreadyExists(p)) {
			
			pointed = searchPoint(p);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		}
		
		repaint();

	}
	
	public void rightMouseClick(Point p) {
		
		if (!animationActive){
			if (pointAlreadyExists(p)) {
	
				deletePoint(p);
			
			}
	
			pointed = null;
			// algorithm.clear();
			buttonsCheck();
			// runAlgorithm();
			repaint();
		}
	}
	
	public void leftMouseClick(Point p) {

		if (!animationActive){
			if (!pointAlreadyExists(p) && (checkBox_showCustomers.isSelected() || checkBox_showSolution.isSelected())) {
				
				addPoint(p);
			
			} else {
	
				if (customersList.contains(p) && (checkBox_showCustomers.isSelected() || checkBox_showSolution.isSelected())) {
					
					dragged = customersList.search(p);
				
				}
				// Highlight the clicked point
				repaint();
			
			
			}
	
			buttonsCheck();
			// runAlgorithm();
		}
	}
	
	// draw methods

	public void drawSolution(){
		
		step.setText("optimal location of turnpike");
		int index = algorithm.solutionIndex;
		drawBalls(index);
		drawPartition(index);
		drawTurnpikePos(index);
		
	}
	
	public void drawPartition(int i){
		
		algorithm.set_withoutTurnpike.get(i).draw(g);
		algorithm.set_withTurnpike.get(i).draw(g);
		
	}
	
	public void drawTurnpikePos(int i){
		
		// draw turnpike
		
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
		
	}
	
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
	
	public void drawBalls(int i){
		
		double r = algorithm.partitionRadius.get(i) + Point.RADIUS;
		Point f = algorithm.facilityPoints.get(i);
		Point t = algorithm.turnpikePoints.get(i);
		g.setColor(new Color(255,193,214,100));
		g.fillRect((int)(f.posX - r), (int)(f.posY - r), (int)(2*r), (int)(2*r));
		g.setColor(new Color(130,228,176,100));
		g.fillRect((int)(t.posX - (r-highwayLength/velocity)), (int)(t.posY - (r-highwayLength/velocity)), (int)(2*(r-highwayLength/velocity)), (int)(2*(r-highwayLength/velocity)));
	
	}

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
	
	public void drawSmallestAxisAlignedRect(int i){
		// draw smallest axis aligned rects
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

	}
	
	public void drawCenters(int i){
		// draw centers
		if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 1) {
			algorithm.list_centersWithoutTurnpike.get(i).draw(g);
		} else if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 2) {
			g.drawLine((int) algorithm.list_centersWithoutTurnpike.get(i).points.get(0)
					.getX(), (int) algorithm.list_centersWithoutTurnpike.get(i).points
					.get(0).getY(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(1)
							.getX(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(1)
							.getY());
		} else if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 4) {
			g.setColor(Color.PINK);
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
			g.drawLine((int) algorithm.list_centersWithTurnpike.get(i).points.get(0)
					.getX(), (int) algorithm.list_centersWithTurnpike.get(i).points
					.get(0).getY(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(1)
							.getX(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(1)
							.getY());
		} else if (algorithm.list_centersWithTurnpike.get(i).getSize() == 4) {
			g.setColor(new Color(214, 207, 234, 145));
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
	
	// paint
	
	public void paintComponent(Graphics graph) {

		Graphics2D g = (Graphics2D) graph;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		super.paintComponent(graph);
		this.g = g;

		constrainButtons();
		drawAllPoints();

		if (!customersList.isEmpty()) {
			runAlgorithm();
			if (runAlgo){
				if (algorithm.set_withoutTurnpike != null){
					if (algorithm.set_withoutTurnpike.size() > 0) {
						if (animationActive && stepCounter > -1){
							drawBalls(stepCounter);
							drawPartition(stepCounter);
							drawTurnpikePos(stepCounter);
						} else if (button_next_clicked || button_prev_clicked){
							drawBalls(stepCounter);
							drawPartition(stepCounter);
							drawTurnpikePos(stepCounter);
						}
						else if (checkBox_showSolution.isSelected()) {
							customersList.draw(g);
							drawSolution();
						} 
						else if (checkBox_showCustomers.isSelected()){
								step.setText("Customer locations");
								customersList.draw(g);
						}
						
						
					}
				}
			}
		}
	}
	
	public boolean pointAlreadyExists(Point p) {

		if (customersList.contains(p)) {
			
			return true;
		
		}
		
		return false;
	}

	public void buttonsCheck() {

	}

	public Point searchPoint(Point p) {

		return customersList.search(p);

	}
	
	public boolean isFacilityPoint(Point p){

		Point f = algorithm.solution_facility;
		if (f != null) {
			
			if (f.equals(p)) return true;
		
		}
		
		return false;
	}
	
	public boolean isTurnpikeStartPoint(Point p){
		
		Point t = algorithm.solution_turnpikeStart;
		if (t != null) {
			
			if (t.equals(p)) return true;
		
		}
		
		return false;
	
	}

	public void dragged(Point p) {

		if (dragged != null && !collisionExists(p)) {
		
			dragged.setPosX(p.getX());
			dragged.setPosY(p.getY());

		}
		
		runAlgorithm();
		repaint();
	
	}

	public void deletePoint(Point p) {

		customersList.remove(p);

	}

	public boolean collisionExists(Point p) {
		
		if (!(customersList.collisionExists(p))) {
		
			return false;
		
		}
		
		return true;
	
	}

	public void addPoint(Point p) {

		if (!collisionExists(p)) {

			customersList.addPoint(p);
			runAlgorithm();

		}
		
		repaint();
		moveMouse(p);
	
	}

	public int[] getScreenCoordinates() {

		int[] coordinates = { panel_west.getWidth(), panel.getWidth(),
				panel_screen.getWidth(), panel_screen.getHeight() };

		return coordinates;

	}

	private class Listener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
		
			repaint();
		
		}
	
	}

	public void setXYLabel(Point p){
		
		label_xyCoord.setFont(new Font("Default", Font.PLAIN, 12));
		label_xyCoord.setText("X: " + p.posX + ", Y: " + p.posY);
		
		repaint();
		moveMouse(p);
	
	}
	
	public String getToolTipText(MouseEvent event) {

		Point p = new Point(event.getX(), event.getY());

		if (algorithm.set_withoutTurnpike == null) {
			return null;
		}
		// TODO: change to labels for current list of points
		for (int j = 0; j < algorithm.set_withoutTurnpike.get(stepCounter).getSize(); j++) {
			if (p.equals(algorithm.set_withoutTurnpike.get(stepCounter).points.get(j))) {

				return "Customer: no use of TP" ;
			}
		}
		
		for (int j = 0; j < algorithm.set_withTurnpike.get(stepCounter).getSize(); j++) {
			if (p.equals(algorithm.set_withTurnpike.get(stepCounter).points.get(j))) {

				return "Customer: use of TP" ;
			}
		}
		
		if (isFacilityPoint(p)) return "Facility point";
		if (isTurnpikeStartPoint(p)) return "Turnpike startpoint";
		

		return null;

	}
	
	public void runExample1(){
		
		// case c)
		clear();
		customersList.addPoint(new Point(613, 102));
		customersList.addPoint(new Point(388, 371));
		runAlgorithm();
		
	}
	
	public void runExample2(){

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
	
	public void runExample3(){
		
		clear();
		customersList.addPoint(new Point(496, 258));
		customersList.addPoint(new Point(507, 275));
		customersList.addPoint(new Point(655, 215));
		customersList.addPoint(new Point(647, 186));
		customersList.addPoint(new Point(670, 169));
		runAlgorithm();
		
	}
	
	public void runAlgorithm() {
		
		highwayLength = slider_length.getValue();
		velocity = slider_velocity.getValue();
		aniSpeed = (int)slider_aniSpeed.getValue();
		if (velocity == 0) runAlgo = false;
		else runAlgo = true;
		
		if (runAlgo){
			algorithm.runAlgorithm(customersList, highwayLength, velocity);
			repaint();
		}
		
	}
	
	public class AnimationThread extends Thread{
		
		public void run(){
			
			animationActive = true;

			try {
				
				sleep(1000);
		
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
			while(stepCounter < algorithm.set_withoutTurnpike.size()){
				
				
				int i = stepCounter + 1;
				step.setText("Step: " + i + " of " + algorithm.set_withoutTurnpike.size());
				
				try {
					
					sleep(5000 / aniSpeed);
			
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				
				repaint();
				
				stepCounter ++;
				
			}
			
			animationActive = false;
			stepCounter = 0;
			step.setText(" ");
			
		}
		
	}
	
	class SliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
		
			
		}
		
	}
	

	
	

}
