package phyObj;
import javax.media.opengl.*;

import main.SceneGraphNode;


public class PhyCircle extends PhyObject {
	private class Renderable extends SceneGraphNode {
		private static final int POINTS = 20;	

		public Renderable() {
			scale = size;
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
	
	float red;
	float green;
	float blue;

	public PhyCircle(float radius) {
		this(radius, (float)Math.random(), (float)Math.random(), (float)Math.random());
	}
	
	public PhyCircle(float radius, float red, float green, float blue) {
		inverseMomentOfInertia = 1 / (float)(Math.PI * Math.pow(radius, 4) / 4);
		this.size = radius;
		this.red = red;
		this.green = green;
		this.blue = blue;
		renderable = new Renderable();
	}
	
	public void setSize(float size) {
		super.setSize(size);
		inverseMomentOfInertia = 1 / (float)(Math.PI * Math.pow(size, 4) / 4) * inverseMass;
	}
	
	public Vector2f getCenter() {
		if (centerOfMass.x == 0 && centerOfMass.y == 0) {
			return position;
		}
		Vector2f r = new Vector2f(centerOfMass);
		r.scale(-1);
		r.rotate(orientation);
		r.sum(position);
		return r;
	}
}