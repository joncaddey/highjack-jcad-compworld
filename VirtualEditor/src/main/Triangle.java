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
	private static final float[] VERTICES_EQ_TRIANGLE = {-.5f, -SIN_60 / 3, .5f, -SIN_60 / 3, 0, SIN_60 * 2 / 3};

	private final float[] vertices;
	public Triangle() {
		this(true);
	}
	
	public Triangle(boolean pickable) {
		super(pickable);
		vertices = VERTICES_EQ_TRIANGLE;
	}
	
	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(1, .8f, 0);
		gl.glVertex2f(vertices[0], vertices[1]);
		gl.glColor3f(1, .7f, 0);
		gl.glVertex2f(vertices[2], vertices[3]);
		gl.glColor3f(1, .6f, 0);
		gl.glVertex2f(vertices[4], vertices[5]);
		gl.glEnd();
	}
}
