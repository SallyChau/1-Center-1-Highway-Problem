package OCOH;

import anja.swinggui.StartButtonApplet;

/**
 * This applet provides the start button that will start the application
 * 
 * @author Ramtin Azimi
 */

public class Applet extends StartButtonApplet {
	private static final long serialVersionUID = 556L;

	/**
	 * Starts the applet.
	 * 
	 * @see appsStandAlone.BRNN.BRNN
	 */
	public Applet() {
		super("Start the algorithm!");
		addApplication("OCOH");
	}

}
