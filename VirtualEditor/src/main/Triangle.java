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
	private static final long serialVersionUID = 42L;
	public static final float SIN_60 = (float)Math.sin(Math.PI / 3);
	private static final float[] VERTICES_EQ_TRIANGLE = {-.5f, -SIN_60 / 3, .5f, -SIN_60 / 3, 0, SIN_60 * 2 / 3};

	private final float[] vertices;
	public Triangle() {
		this(true, VERTICES_EQ_TRIANGLE);
	}
	
	public Triangle(boolean pickable) {
		this(pickable, VERTICES_EQ_TRIANGLE);
	}
	
	/**
	 * 
	 * @param pickable
	 * @param vertices an array copy is not used.  Must be 6.
	 */
	public Triangle(boolean pickable, float[] vertices) {
		super(pickable);
		this.vertices = vertices;
	}
	
	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(Math.min(1, current_red + .2f), Math.min(1, current_green + .2f), Math.min(1, current_blue + .2f));
		gl.glVertex2f(vertices[0], vertices[1]);
		gl.glColor3f(Math.min(1, current_red + .1f), Math.min(1, current_green + .1f), Math.min(1, current_blue + .1f));
		gl.glVertex2f(vertices[2], vertices[3]);
		gl.glColor3f(current_red, current_green, current_blue);
		gl.glVertex2f(vertices[4], vertices[5]);
		gl.glEnd();
	}
}
