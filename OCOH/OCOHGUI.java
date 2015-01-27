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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import anja.swinggui.JRepeatButton;

public class OCOHGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// GUI elements
	
	private Graphics g;
	private static OCOHGUI panel;
	Listener l = new Listener();
	
	// Panels
	JPanel panel_east = new JPanel();
	JPanel panel_west = new JPanel();
	JPanel panel_screen = new JPanel();

	// Labels
	JLabel label_mode = new JLabel("MODE 1C1H");
	JLabel label_underline = new JLabel("________________");
	JLabel label_underline1 = new JLabel("________________");
	JLabel label_underline2 = new JLabel("________________");
	JLabel label_highway = new JLabel("Highway: ");
	JLabel label_velocity = new JLabel("Velocity: ");
	JLabel label_examples = new JLabel("Examples:");
	JLabel label_algorithm = new JLabel("ALGORITHM");
	JLabel label_step = new JLabel("Step by step: ");
	JLabel label_show = new JLabel("SHOW");

	// Textfields
	private JTextField text_length = new JTextField("100");
	private JTextField text_velocity = new JTextField("2");
	
	// Buttons
	JButton button_prev = new JButton("\u25c4");
	JButton button_next = new JButton("\u25BA");
	JRepeatButton button_zoomIn = new JRepeatButton();
	JRepeatButton button_zoomOut = new JRepeatButton();
	JButton button_clear = new JButton();
	
	// Checkbox
	JCheckBox checkBox_fixedLength = new JCheckBox("fixed");
	JCheckBox checkBox_run = new JCheckBox("Run.");
	JCheckBox checkBox_showBalls = new JCheckBox("Balls");
	JCheckBox checkBox_showFacility = new JCheckBox("Facility");
	JCheckBox checkBox_showHighway = new JCheckBox("Highway");
	JCheckBox checkBox_showCustomers = new JCheckBox("Customers");

	// Combobox
	String[] string_examples = { "Set points ...", "Example 1", "Example 2", "Example 3" };
	JComboBox box_examples = new JComboBox(string_examples);
	String[] string_highways = { "Turnpike", "Freeway" };
	JComboBox box_highways = new JComboBox(string_highways);

	// Image icons
	ImageIcon icon_zoomIn = createImageIcon("resources/zoomIn.png");
	ImageIcon icon_zoomOut = createImageIcon("resources/zoomOut.png");
	ImageIcon icon_clear = createImageIcon("resources/trash.png");

	// Variables
	OCOHAlgorithm algorithm;
	public PointList customersList = new PointList();
	public int velocity;
	public int highwayLength;
	Point dragged;
	Point pointed;
	int stepCounter = 0;
	
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

		checkBox_fixedLength.setSelected(true);
		checkBox_showCustomers.setSelected(true);
		checkBox_run.setSelected(true);

	}

	public void createGUI() {

		setLayout(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		panel_west.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		panel_west.setLayout(new GridBagLayout());

		// Label "Mode"
		label_mode.setFont(new Font("Default", Font.BOLD, 14));
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel_west.add(label_mode, gbc);
		gbc.insets = new Insets(10, 0, 3, 0);
		panel_west.add(label_underline, gbc);

		// Label "Highway"
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.gridy = 1;
		panel_west.add(label_highway, gbc);

		// ComboBox for Highwaytypes
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 2;
		panel_west.add(box_highways, gbc);

		// Label "Length"
		gbc.fill = GridBagConstraints.NONE;
		JLabel lengthLabel = new JLabel("Length: ");
		gbc.gridy = 3;
		panel_west.add(lengthLabel, gbc);

		// textfield for length
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 5;
		panel_west.add(text_length, gbc);

		// Checkbox for length
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 4;
		panel_west.add(checkBox_fixedLength, gbc);

		// Label "Velocity"
		gbc.gridy = 6;
		panel_west.add(label_velocity, gbc);

		// textfield for velocity
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 7;
		panel_west.add(text_velocity, gbc);

		gbc.gridy = 8;
		panel_west.add(new JLabel(""), gbc);

		// Label "Algorithm"
		label_algorithm.setFont(new Font("Default", Font.BOLD, 14));
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 9;
		panel_west.add(label_algorithm, gbc);
		gbc.insets = new Insets(10, 0, 3, 0);
		panel_west.add(label_underline1, gbc);

		// ComboBox for Examples
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 10;
		panel_west.add(box_examples, gbc);

		// Run Button
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 11;
		panel_west.add(checkBox_run, gbc);

		// Label "Step by Step"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 12;
		panel_west.add(label_step, gbc);

		// Buttons Step by Step
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 13;
		JPanel stepsPanel = new JPanel();
		stepsPanel.add(button_prev);
		stepsPanel.add(button_next);
		panel_west.add(stepsPanel, gbc);

		gbc.gridy = 14;
		panel_west.add(new JLabel(""), gbc);

		// Label "Show"
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 15;
		label_show.setFont(new Font("Default", Font.BOLD, 14));
		gbc.anchor = GridBagConstraints.LINE_START;
		panel_west.add(label_show, gbc);
		gbc.insets = new Insets(10, 0, 3, 0);
		panel_west.add(label_underline2, gbc);

		// Checkbox
		gbc.insets = new Insets(0, 0, 3, 0);
		gbc.gridy = 16;
		panel_west.add(checkBox_showCustomers, gbc);
		gbc.gridy = 17;
		panel_west.add(checkBox_showFacility, gbc);
		gbc.gridy = 18;
		panel_west.add(checkBox_showHighway, gbc);
		gbc.gridy = 19;
		panel_west.add(checkBox_showBalls, gbc);

		panel_screen.setBackground(Color.WHITE);
		button_zoomIn.setIcon(icon_zoomIn);
		button_zoomIn.setPreferredSize(new Dimension(26, 26));
		panel_screen.add(button_zoomIn);
		button_zoomOut.setIcon(icon_zoomOut);
		button_zoomOut.setPreferredSize(new Dimension(26, 26));
		panel_screen.add(button_zoomOut);
		button_clear.setIcon(icon_clear);
		button_clear.setPreferredSize(new Dimension(26, 26));
		panel_screen.add(button_clear);
		panel_east.setOpaque(false);
		panel_east.setLayout(new BorderLayout());
		panel_east.setBackground(Color.WHITE);
		panel_east.add(panel_screen, BorderLayout.NORTH);

		add(panel_east, BorderLayout.EAST);
		add(panel_west, BorderLayout.WEST);

		registerListeners();
	}

	public static ImageIcon createImageIcon(String path) {
		Image look = null;
		try {
			look = ImageIO.read(OCOHGUI.class.getClassLoader()
					.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ImageIcon(look);
	}

	public void registerListeners() {

		// Register the listeners
		text_length.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		
		text_length.addKeyListener(new KeyAdapter() {
			
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
			    if ( ((c < '0') || (c > '9')) && c != KeyEvent.VK_BACK_SPACE) {
			    	e.consume();  // ignore event
			    }    
			}
			
			public void keyPressed(KeyEvent e) {

				checkBox_run.setSelected(false);
				keyTyped(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
			    	System.out.println("Enter");
			    	try{
						highwayLength = Integer.parseInt(text_length.getText());
					}
					catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
						highwayLength = 100;
						text_length.setText("100");
					}
	                checkBox_run.setSelected(true);
	            }
				
			}
			
		});
		
		text_velocity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		
		text_velocity.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
			    if ( ((c < '0') || (c > '9')) && c != KeyEvent.VK_BACK_SPACE) {
			    	e.consume();  // ignore event
			    }    
			}
			
			public void keyPressed(KeyEvent e) {

				checkBox_run.setSelected(false);
				keyTyped(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
			    	System.out.println("Enter");
			    	try{
						velocity = Integer.parseInt(text_velocity.getText());
					}
					catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
						velocity = 2;
						text_velocity.setText("2");
					}
	                checkBox_run.setSelected(true);
	            }
				
			}
		});

		checkBox_run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

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
				if (stepCounter > 0) {
					button_prev.setEnabled(true);
				}
				if (stepCounter < algorithm.set_withoutTurnpike.size()-1) {
					button_next.setEnabled(true);
					stepCounter++;
				} else {
					button_next.setEnabled(false);
				}
				repaint();

			}
		});

		button_prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (stepCounter < algorithm.set_withoutTurnpike.size()-1) {
					button_next.setEnabled(true);
				}
				if (stepCounter > 0) {
					button_prev.setEnabled(true);
					stepCounter--;
				} else {
					button_prev.setEnabled(false);
				}
				repaint();
				// algorithm.a--; // erniedrigt center() radius
			}

		});

	}

	public void clear() {
		customersList.clear();
		// algorithm.clear();
		defaultButtonSettings();
		stepCounter = 0;
		repaint();
	}

	public void paintComponent(Graphics graph) {

		Graphics2D g = (Graphics2D) graph;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setStroke(new BasicStroke(1));
		super.paintComponent(graph);
		this.g = g;

		drawAllPoints();

		if (!customersList.isEmpty()) {
			runAlgorithm();
			if (algorithm.set_withoutTurnpike.size() > 0) {
//				drawCenters(stepCounter);
//				drawPartition(stepCounter);
//				drawSmallestAxisAlignedRect(stepCounter);
//				drawTurnpikePos(stepCounter);
				drawSolution();
			}
		}
	}
	
	public void drawSolution(){
		int index = algorithm.solutionIndex;
		drawCenters(index);
		drawSmallestAxisAlignedRect(index);
		drawPartition(index);
		drawTurnpikePos(index);
	}
	
	public void drawPartition(int i){
		// draw Partitions
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

	public void drawSmallestAxisAlignedRect(int i){
		// draw smallest axis aligned rects
//		if (algorithm.extremePoints1.get(stepCounter)[0] != null){
			g.setColor(Color.BLUE);
			g.drawRect(
					(int) algorithm.extremePoints1.get(i)[0].posX,
					(int) algorithm.extremePoints1.get(i)[2].posY,
					(int) Math.abs(algorithm.extremePoints1.get(i)[0].posX
							- algorithm.extremePoints1.get(i)[1].posX),
					(int) Math.abs(algorithm.extremePoints1.get(i)[2].posY
							- algorithm.extremePoints1.get(i)[3].posY));
//		}
//		if (algorithm.extremePoints2.get(stepCounter)[0] != null){
			g.setColor(Color.RED);
			g.drawRect(
					(int) algorithm.extremePoints2.get(i)[0].posX,
					(int) algorithm.extremePoints2.get(i)[2].posY,
					(int) Math.abs(algorithm.extremePoints2.get(i)[0].posX
							- algorithm.extremePoints2.get(i)[1].posX),
					(int) Math.abs(algorithm.extremePoints2.get(i)[2].posY
							- algorithm.extremePoints2.get(i)[3].posY));
//		}
	}
	
	public void drawCenters(int i){
		// draw centers
		if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 1) {
			algorithm.list_centersWithoutTurnpike.get(i).draw(g);
		} else if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 2) {
			// algorithm.c1[stepCounter].draw(g);
			g.drawLine((int) algorithm.list_centersWithoutTurnpike.get(i).points.get(0)
					.getX(), (int) algorithm.list_centersWithoutTurnpike.get(i).points
					.get(0).getY(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(1)
							.getX(),
					(int) algorithm.list_centersWithoutTurnpike.get(i).points.get(1)
							.getY());
		} else if (algorithm.list_centersWithoutTurnpike.get(i).getSize() == 4) {
			// algorithm.c1[stepCounter].draw(g);
			g.setColor(new Color(214, 207, 234, 145));
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
			// algorithm.c2[stepCounter].draw(g);
			g.drawLine((int) algorithm.list_centersWithTurnpike.get(i).points.get(0)
					.getX(), (int) algorithm.list_centersWithTurnpike.get(i).points
					.get(0).getY(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(1)
							.getX(),
					(int) algorithm.list_centersWithTurnpike.get(i).points.get(1)
							.getY());
		} else if (algorithm.list_centersWithTurnpike.get(i).getSize() == 4) {
			// algorithm.c2[stepCounter].draw(g);
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

	public boolean pointAlreadyExists(Point p) {

		if (customersList.contains(p)) {
			return true;
		}
		return false;
	}

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
		if (pointAlreadyExists(p)) {

			deletePoint(p);
		}

		pointed = null;
		// algorithm.clear();
		buttonsCheck();
		// runAlgorithm();
		repaint();
	}

	public void buttonsCheck() {

	}

	public void leftMouseClick(Point p) {

		if (!pointAlreadyExists(p) && checkBox_showCustomers.isSelected()) {
			addPoint(p);
		} else {

			if (customersList.contains(p) && checkBox_showCustomers.isSelected()) {
				dragged = customersList.search(p);
			}
			// Highlight the clicked point
			repaint();
		}

		buttonsCheck();
		// runAlgorithm();
	}

	public Point searchPoint(Point p) {

		return customersList.search(p);

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

			System.out.println(p.toString());
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

	public void runAlgorithm() {

		if (checkBox_run.isSelected()) {
			try{
				highwayLength = Integer.parseInt(text_length.getText());
				velocity = Integer.parseInt(text_velocity.getText());
				algorithm.runAlgorithm(customersList, highwayLength, velocity);
				repaint();
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null,
					    "Invalid Input!",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
			

		}
	}
}
