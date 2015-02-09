package OCOH;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

/**
 * Provides the MouseHandler class for OCOHGUI supporting OCOHAlgorithm.
 * @author Ramtin Azimi, Sally Chau
 *
 */

public class MouseHandler extends MouseAdapter{

	//**************************************************************************
	// Variables
	//**************************************************************************
		
	OCOHGUI panel;
	private static MouseHandler _mouseHandler;

	//**************************************************************************
	// Constructors
	//**************************************************************************
		
	private MouseHandler(OCOHGUI panel) {
		this.panel = panel;
	}

	//**************************************************************************
	// Getter
	//**************************************************************************
		
	public static MouseHandler getMouseHandler(OCOHGUI panel) {

		if (_mouseHandler == null) {
			_mouseHandler = new MouseHandler(panel);
		}
		return _mouseHandler;
	}
	
	public static MouseHandler getMouse(){
		return _mouseHandler;
	}
	
	//**************************************************************************
	// Mouse Event Methods
	//**************************************************************************
		
	@Override
	public void mousePressed(MouseEvent e) {
		
		Point p = new Point(e.getX(), e.getY());
		if(mouseInScreen(e)){
			
			if(SwingUtilities.isRightMouseButton(e)){
				panel.rightMouseClick(p);
			}else if(SwingUtilities.isLeftMouseButton(e)){
				panel.leftMouseClick(p);				
			}
			
		}
	}

	public void mouseReleased(MouseEvent e){
	
		 // Erase the "click" highlight
        if (panel.dragged != null) {
            panel.repaint();
            
        }
        panel.dragged = null;
        
	}
	
	public void mouseDragged(MouseEvent e) {

		if(mouseInScreen(e)){
			Point p = new Point(e.getX(), e.getY());
			panel.dragged(p);
			panel.setXYLabel(p);
		}

	}
	
	public void mouseMoved(MouseEvent e){
		 
		Point p = new Point(e.getX(),e.getY());
		panel.moveMouse(p);
		panel.setXYLabel(p);
		
	}
	
	/**
	 * Checks whether the mouse is within the space of the panel which displays the 
	 * OCOH Algorithm.
	 * @param e
	 * @return
	 * 		true, if the mouse coordinates are within the right panel.
	 */
	public boolean mouseInScreen(MouseEvent e){
		
		int[] coordinates = panel.getScreenCoordinates();
		int x_start = coordinates[0];
		int x_end   = coordinates[1];
		int screenPanelXStart = panel.getWidth() - coordinates[2];
		int screenPanelYStart = 0;
		Rect screenPanelRect = 
				new Rect(new Point(screenPanelXStart - Point.RADIUS, screenPanelYStart), coordinates[2], coordinates[3] + Point.RADIUS);
		
		if (x_start + Point.RADIUS >= e.getX() || x_end - Point.RADIUS <= e.getX() 
				|| screenPanelRect.contains(new Point(e.getX(), e.getY()))) {
			return false;
		}
		return true;
	}

}
