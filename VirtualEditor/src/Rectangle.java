

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
		gl.glColor3f(1, .8f, 0);
		gl.glVertex2f(-.5f, -.5f);
		gl.glColor3f(1, .7f, 0);
		gl.glVertex2f(.5f, -.5f);
		gl.glColor3f(1, .6f, 0);
		gl.glVertex2f(.5f, .5f);
		gl.glColor3f(1, .6f, 0);
		gl.glVertex2f(-.5f, .5f);
		gl.glEnd();
	}
}
