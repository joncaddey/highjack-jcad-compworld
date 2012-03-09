package main;
import javax.media.opengl.GLProfile;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
public class Main {

	VirtualCanvas my_canvas;
	public Main() {
		my_canvas = new VirtualCanvas(); 
	}
	
	public void start() {
		new Window("Highjacad's Asteroids", my_canvas);
	}
	public static void main(String args[]) {
		
		GLProfile.initSingleton();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new Main()).start();
			}
		});
	}
}
