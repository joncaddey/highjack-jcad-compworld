package main;


import javax.media.opengl.*;

public class Rectangle extends SceneGraphNode {

	public Rectangle() {
		this(true);
	}
	
	public Rectangle(boolean pickable) {
		super(pickable);
	}
	
	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(Math.min(1, red + .2f), Math.min(1, green + .2f), Math.min(1, blue + .2f));
		gl.glVertex2f(-.5f, -.5f);
		gl.glColor3f(Math.min(1, red + .1f), Math.min(1, green + .1f), Math.min(1, blue + .1f));
		gl.glVertex2f(.5f, -.5f);
		gl.glColor3f(red, green, blue);
		gl.glVertex2f(.5f, .5f);
		gl.glColor3f(red, green, blue);
		gl.glVertex2f(-.5f, .5f);
		gl.glEnd();
	}
}
