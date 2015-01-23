package OneCenterOneHighway;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Frame extends JFrame{
	
	JLabel mode;
	JLabel highwayMode;
	JLabel lengthMode;
	Label label;
	JButton startButton;
	JButton stopButton;
	JButton resetButton;
	JButton clearButton;
	JCheckBox checkTurnpike;
	JCheckBox checkFreeway;
	JCheckBox checkFixedLength;
	JCheckBox checkVariableLength;
	JCheckBox checkStepByStep;
	JCheckBox checkAnimate;
	JCheckBox checkShowBalls;

	public Frame(){
		setVisible(true);
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("The 1-Center and 1-Highway Problem");
		setResizable(false);
		setLayout(null);
		
		mode = new JLabel("MODE");
		// make it underlined
		mode.setBounds(10,10,80,20);
		add(mode);
		highwayMode = new JLabel("Highwaytype:");
		highwayMode.setBounds(10,40,80,20);
		add(highwayMode);
		lengthMode = new JLabel("Length:");
		lengthMode.setBounds(10,130,80,20);
		add(lengthMode);
		
		startButton = new JButton("Start");
		startButton.setBounds(10,230,80,20);
		add(startButton);
		stopButton = new JButton("Stop");
		stopButton.setBounds(10,260,80,20);
		add(stopButton);
		resetButton = new JButton("Reset");
		resetButton.setBounds(10,290,80,20);
		add(resetButton);
		clearButton = new JButton("Clear");
		clearButton.setBounds(10,320,80,20);
		add(clearButton);
		
		checkTurnpike = new JCheckBox("Turnpike");
		checkTurnpike.setBounds(10,70,80,20);
		add(checkTurnpike);
		checkFreeway = new JCheckBox("Freeway");
		checkFreeway.setBounds(10,100,80,20);
		add(checkFreeway);
		checkFixedLength = new JCheckBox("fixed");
		checkFixedLength.setBounds(10,160,80,20);
		add(checkFixedLength);
		checkVariableLength = new JCheckBox("variable");
		checkVariableLength.setBounds(10,190,80,20);
		add(checkVariableLength);
		
		// Change to +/- buttons
		checkStepByStep = new JCheckBox("Step by Step");
		checkStepByStep.setBounds(10,360,100,20);
		add(checkStepByStep);
		
		checkAnimate = new JCheckBox("Animate");
		checkAnimate.setBounds(10,390,80,20);
		add(checkAnimate);
		checkShowBalls = new JCheckBox("Show Balls");
		checkShowBalls.setBounds(10,420,100,20);
		add(checkShowBalls);
		
		label = new Label();
		label.setBounds(0,0,800,500);
		add(label);
		
		addMouseListener(new MouseHandler());
	}
	
	private class Label extends JLabel{
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawRect(120, 10, 660, 450);
			g.fillRect(255,255,255,255);
		}
	}
	
	public class MouseHandler implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.println("mouse pressed at "+ e.getX() + " " + e.getY());
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}}
	
}
