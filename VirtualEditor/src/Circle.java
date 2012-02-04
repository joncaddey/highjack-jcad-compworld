import javax.media.opengl.*;

public class Circle extends SceneGraphNode {

	private static final int PARTS = 100;
	private static final int CIRCLE_LIST = 2;

	public Circle() {
		this(true);
	}

	public Circle(boolean pickable) {
		super(pickable);
	}

	public void renderGeometry(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glColor3f(1, .8f, 0);

		float cosine, sine;
		//gl.glNewList(CIRCLE_LIST, GL2.GL_COMPILE_AND_EXECUTE);
		gl.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < 100; i++) {
			cosine = (float) Math.cos(i * 2 * Math.PI / PARTS) / 2;
			sine = (float) Math.sin(i * 2 * Math.PI / PARTS) / 2;
			gl.glVertex2f(cosine, sine);
		}
		gl.glEnd();
		//gl.glEndList();

	}
}
