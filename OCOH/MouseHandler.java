package OCOH;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class MouseHandler extends MouseAdapter{

	OCOHGUI panel;
	
	private static MouseHandler mouseHandler;

	private MouseHandler(OCOHGUI panel) {
		this.panel = panel;
	}

	public static MouseHandler getMouseHandler(OCOHGUI panel) {

		if (mouseHandler == null) {
			mouseHandler = new MouseHandler(panel);
		}
		return mouseHandler;
	}
	
	public static MouseHandler getMouse(){
		return mouseHandler;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		
		Point p = new Point(e.getX(), e.getY());
		
		System.out.println(p.toString());
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
		
		//System.err.println("MOUSE DRAGGED:"+e.getX()+" "+e.getY());

		if(mouseInScreen(e)){
			Point p = new Point(e.getX(), e.getY());
			panel.dragged(p);
		}

	}
	
	public void mouseMoved(MouseEvent e){
		 
		Point p = new Point(e.getX(),e.getY());
		panel.moveMouse(p);
		 
	}
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
