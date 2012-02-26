package main;
import javax.media.opengl.*;


/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
public class Circle extends SceneGraphNode {

	private static final int PARTS = 100;

	public Circle() {
		this(true);
	}

	public Circle(boolean pickable) {
		super(pickable);
	}

	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glColor3f(.7f, .8f, 0);

		float cosine, sine;
		gl.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < 100; i++) {
			cosine = (float) Math.cos(i * 2 * Math.PI / PARTS) / 2;
			sine = (float) Math.sin(i * 2 * Math.PI / PARTS) / 2;
			gl.glVertex2f(cosine, sine);
		}
		gl.glEnd();

	}
}
