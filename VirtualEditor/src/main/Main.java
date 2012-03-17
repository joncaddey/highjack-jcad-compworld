package main;
import javax.media.opengl.GLProfile;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
public class Main {

	
	public void start() {
		new Window("Highjacad's Asteroids");
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
