package phyObj;
import javax.media.opengl.*;

import main.SceneGraphNode;


public class PhyCircle extends PhysicsObject {
	private class Renderable extends SceneGraphNode {
		private static final int POINTS = 20;	

		public Renderable() {
			scale = radius;
		}
	
		public void renderGeometry(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glColor3f(red, green, blue);
			gl.glVertex2f(0, 0);
			gl.glColor3f(1, 1, 1);
			gl.glVertex2f(1, 0);
			gl.glColor3f(red, green, blue);
			for (int i = 1; i < POINTS; i++) {
				double radians = 2 * Math.PI * i / POINTS;
				gl.glVertex2d(Math.cos(radians), Math.sin(radians));
			}
			gl.glColor3f(1, 1, 1);
			gl.glVertex2f(1, 0);
			gl.glEnd();
		}
	}
	
	float radius;
	float red;
	float green;
	float blue;

	public PhyCircle(float radius) {
		this(radius, (float)Math.random(), (float)Math.random(), (float)Math.random());
	}
	
	public PhyCircle(float radius, float red, float green, float blue) {
		inverseMomentOfInertia = 1 / (float)(Math.PI * Math.pow(radius, 4) / 4);
		this.radius = radius;
		this.red = red;
		this.green = green;
		this.blue = blue;
		renderable = new Renderable();
	}
}