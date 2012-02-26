package main;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
public class Triangle extends SceneGraphNode {
	public static final float SIN_60 = (float)Math.sin(Math.PI / 3);

	public Triangle() {
		this(true);
	}
	
	public Triangle(boolean pickable) {
		super(pickable);
	}
	
	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(1, .8f, 0);
		gl.glVertex2f(-.5f, -SIN_60 / 3);
		gl.glColor3f(1, .7f, 0);
		gl.glVertex2f(0, SIN_60 * 2 / 3);
		gl.glColor3f(1, .6f, 0);
		gl.glVertex2f(.5f, -SIN_60 / 3);
		gl.glEnd();
	}
}
