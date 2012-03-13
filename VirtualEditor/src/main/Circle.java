package main;
import javax.media.opengl.*;


/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
public class Circle extends SceneGraphNode {

	private static final int POINTS = 20;

	public Circle() {
		this(true);
	}

	public Circle(boolean pickable) {
		super(pickable);
	}
	
	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glColor3f(current_red, current_green, current_blue);
		gl.glVertex2f(0, 0);
		gl.glColor3f(1, 1, 1);
		gl.glVertex2f(1, 0);
		gl.glColor3f(current_red, current_green, current_blue);
		for (int i = 1; i < POINTS; i++) {
			double radians = 2 * Math.PI * i / POINTS;
			gl.glVertex2d(Math.cos(radians), Math.sin(radians));
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex2f(1, 0);
		gl.glEnd();
	}
}
