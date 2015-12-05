package OCOH;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;

/**
 * 
 * @author Sally Chau
 *
 */

public class OCOH{

	private static final String TITLE = "1-Center 1-Highway Problem";
	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 700;

	public OCOH(){
		
        JFrame frame = new JFrame();     						      	// Create window
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);  					// Set window size
        frame.setTitle(TITLE);     							 			// Set window title
        frame.setLayout(new BorderLayout());   							// Specify layout manager
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			// Specify closing behavior
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);    
        
		OCOHAlgorithm algorithm = new OCOHAlgorithm();
		OCOHGUI panel = OCOHGUI.getOCOHGUI(algorithm);
        panel.setBackground(Color.WHITE);
		frame.add(panel, "Center");  	         						// Place panel into window
        frame.setVisible(true);         	       						// Show the window

	}
	
	public static void main(String[] args){
		
		OCOH b = new OCOH();
		Applet applet = new Applet();    								// Create applet
        applet.setVisible(true);                          				// Applet initialization
       
	}
	
}
