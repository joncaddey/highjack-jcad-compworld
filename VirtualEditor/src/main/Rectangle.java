package main;


import javax.media.opengl.*;

public class Rectangle extends SceneGraphNode {
	private static final long serialVersionUID = 42L;
	public Rectangle() {
		this(true);
	}
	
	public Rectangle(boolean pickable) {
		super(pickable);
	}
	
	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(Math.min(1, current_red + .2f), Math.min(1, current_green + .2f), Math.min(1, current_blue + .2f));
		gl.glVertex2f(-.5f, -.5f);
		gl.glColor3f(Math.min(1, current_red + .1f), Math.min(1, current_green + .1f), Math.min(1, current_blue + .1f));
		gl.glVertex2f(.5f, -.5f);
		gl.glColor3f(current_red, current_green, current_blue);
		gl.glVertex2f(.5f, .5f);
		gl.glColor3f(current_red, current_green, current_blue);
		gl.glVertex2f(-.5f, .5f);
		gl.glEnd();
	}
}
